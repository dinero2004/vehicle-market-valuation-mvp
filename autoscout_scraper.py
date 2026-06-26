#!/usr/bin/env python3
"""Scrape vehicle listing data from AutoScout24 search result pages.

The scraper uses requests for fetching and Beautiful Soup for parsing. It does
not bypass CAPTCHA, Cloudflare, or other anti-bot protections. If a market blocks
plain requests, save the search result HTML in a browser and use --input-html.
"""

from __future__ import annotations

import argparse
import csv
import json
import re
import sys
import time
from dataclasses import asdict, dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Iterable
from urllib.parse import parse_qsl, urlencode, urljoin, urlparse, urlunparse

import requests
from bs4 import BeautifulSoup, Tag


DEFAULT_HEADERS = {
    "User-Agent": (
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0 Safari/537.36"
    ),
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Language": "en-US,en;q=0.9",
}

MARKET_PRESETS = {
    "bmw-m4-ch": "https://www.autoscout24.ch/de/s/mo-m4/mk-bmw",
}

FIELDNAMES = [
    "listing_id",
    "title",
    "subtitle",
    "price",
    "currency",
    "price_amount",
    "make",
    "model",
    "first_registration",
    "mileage_km",
    "fuel_type",
    "power",
    "vin_number",
    "type_approval",
    "seller_type",
    "listing_country",
    "listing_zip_code",
    "url",
    "image_url",
    "source_url",
    "scraped_at",
]


@dataclass
class VehicleListing:
    listing_id: str | None = None
    title: str | None = None
    subtitle: str | None = None
    price: str | None = None
    currency: str | None = None
    price_amount: int | None = None
    make: str | None = None
    model: str | None = None
    first_registration: str | None = None
    mileage_km: int | None = None
    fuel_type: str | None = None
    power: str | None = None
    vin_number: str | None = None
    type_approval: str | None = None
    seller_type: str | None = None
    listing_country: str | None = None
    listing_zip_code: str | None = None
    url: str | None = None
    image_url: str | None = None
    source_url: str | None = None
    scraped_at: str | None = None


def clean_text(value: str | None) -> str | None:
    if value is None:
        return None
    text = re.sub(r"\s+", " ", value).strip()
    return text or None


def parse_int(value: str | None) -> int | None:
    if not value:
        return None
    digits = re.sub(r"[^\d]", "", value)
    return int(digits) if digits else None


def parse_currency(value: str | None) -> str | None:
    if not value:
        return None
    match = re.search(r"(€|CHF|£|\$)", value)
    return match.group(1) if match else None


def extract_vin(text: str) -> str | None:
    match = re.search(r"\b[A-HJ-NPR-Z0-9]{17}\b", text, flags=re.IGNORECASE)
    return match.group(0).upper() if match else None


def extract_labeled_value(text: str, labels: Iterable[str]) -> str | None:
    for label in labels:
        pattern = rf"{re.escape(label)}\s*[:#-]?\s*([A-Z0-9][A-Z0-9./-]{{2,40}})"
        match = re.search(pattern, text, flags=re.IGNORECASE)
        if match:
            return clean_text(match.group(1))
    return None


def text_by_testid(card: Tag, testid: str) -> str | None:
    node = card.select_one(f'[data-testid="{testid}"]')
    return clean_text(node.get_text(" ", strip=True)) if node else None


def first_attr(card: Tag, selector: str, attr: str) -> str | None:
    node = card.select_one(selector)
    if not node:
        return None
    value = node.get(attr)
    if isinstance(value, list):
        return value[0] if value else None
    return value


def extract_title_and_subtitle(card: Tag) -> tuple[str | None, str | None]:
    title_node = card.select_one('[class*="ListItemTitle_title"], [data-testid="listing-title"]')
    subtitle_node = card.select_one('[class*="ListItemTitle_subtitle"], [class*="subtitle"]')
    h2 = card.select_one("h2")

    title = clean_text(title_node.get_text(" ", strip=True)) if title_node else None
    subtitle = clean_text(subtitle_node.get_text(" ", strip=True)) if subtitle_node else None

    if not title and h2:
        spans = [clean_text(span.get_text(" ", strip=True)) for span in h2.find_all("span")]
        spans = [span for span in spans if span]
        if spans:
            title = spans[0]
        if not subtitle and len(spans) > 1:
            subtitle = spans[1]
        if not title:
            title = clean_text(h2.get_text(" ", strip=True))

    return title, subtitle


def find_listing_url(card: Tag, base_url: str) -> str | None:
    for link in card.find_all("a", href=True):
        href = link["href"]
        if "/offers/" in href or "/car/" in href or "/vehicle/" in href:
            return urljoin(base_url, href)
    return None


def is_bot_challenge(response: requests.Response) -> bool:
    header_values = " ".join(f"{key}: {value}" for key, value in response.headers.items()).lower()
    body = response.text[:5000].lower()
    indicators = [
        "cf-mitigated",
        "challenge-platform",
        "cloudflare",
        "/cdn-cgi/challenge-platform",
        "just a moment",
        "enable javascript and cookies to continue",
    ]
    return any(indicator in header_values or indicator in body for indicator in indicators)


def parse_card(card: Tag, base_url: str, source_url: str | None) -> VehicleListing:
    title, subtitle = extract_title_and_subtitle(card)
    card_text = clean_text(card.get_text(" ", strip=True)) or ""
    price = clean_text(
        text_by_testid(card, "regular-price")
        or (card.select_one('[class*="price"]').get_text(" ", strip=True) if card.select_one('[class*="price"]') else None)
    )
    first_registration = clean_text(
        text_by_testid(card, "VehicleDetails-calendar")
        or card.get("data-first-registration")
    )
    mileage_text = clean_text(
        text_by_testid(card, "VehicleDetails-mileage_odometer")
        or card.get("data-mileage")
    )

    return VehicleListing(
        listing_id=card.get("data-guid") or card.get("id"),
        title=title,
        subtitle=subtitle,
        price=price,
        currency=parse_currency(price),
        price_amount=parse_int(card.get("data-price") or price),
        make=card.get("data-make"),
        model=card.get("data-model"),
        first_registration=first_registration,
        mileage_km=parse_int(card.get("data-mileage") or mileage_text),
        fuel_type=clean_text(text_by_testid(card, "VehicleDetails-gas_pump") or card.get("data-fuel-type")),
        power=clean_text(text_by_testid(card, "VehicleDetails-speedometer")),
        vin_number=clean_text(
            card.get("data-vin")
            or extract_vin(card_text)
            or extract_labeled_value(card_text, ["VIN", "VIN number", "Chassis number"])
        ),
        type_approval=clean_text(
            card.get("data-type-approval")
            or card.get("data-typengenehmigung")
            or extract_labeled_value(
                card_text,
                [
                    "Typengenehmigung",
                    "Typengenehmigungsnummer",
                    "Typenschein",
                    "Type approval",
                    "Type approval number",
                ],
            )
        ),
        seller_type=card.get("data-seller-type"),
        listing_country=card.get("data-listing-country"),
        listing_zip_code=card.get("data-listing-zip-code"),
        url=find_listing_url(card, base_url),
        image_url=first_attr(card, '[data-testid="list-item-image"]', "src"),
        source_url=source_url,
        scraped_at=datetime.now(timezone.utc).isoformat(timespec="seconds"),
    )


def parse_listings(html: str, base_url: str, source_url: str | None = None) -> list[VehicleListing]:
    soup = BeautifulSoup(html, "html.parser")
    cards = soup.select('article[data-testid="list-item"], article[data-guid], [data-testid="list-item"][data-guid]')
    listings: list[VehicleListing] = []
    seen: set[str] = set()

    for card in cards:
        listing = parse_card(card, base_url=base_url, source_url=source_url)
        dedupe_key = listing.listing_id or json.dumps(asdict(listing), sort_keys=True)
        if dedupe_key in seen:
            continue
        seen.add(dedupe_key)
        listings.append(listing)

    return listings


def page_url(url: str, page: int) -> str:
    parsed = urlparse(url)
    query = dict(parse_qsl(parsed.query, keep_blank_values=True))
    query["page"] = str(page)
    return urlunparse(parsed._replace(query=urlencode(query, doseq=True)))


def fetch_html(session: requests.Session, url: str, timeout: int) -> str:
    response = session.get(url, timeout=timeout)
    if is_bot_challenge(response):
        raise RuntimeError(
            "AutoScout returned a Cloudflare challenge for this market. "
            "Use --input-html with a browser-saved search results page, or try a market/search URL that allows plain requests."
        )
    response.raise_for_status()
    return response.text


def scrape_url(url: str, pages: int, delay: float, timeout: int) -> list[VehicleListing]:
    session = requests.Session()
    session.headers.update(DEFAULT_HEADERS)

    all_listings: list[VehicleListing] = []
    for page in range(1, pages + 1):
        current_url = page_url(url, page)
        print(f"Fetching page {page}: {current_url}", file=sys.stderr)
        html = fetch_html(session, current_url, timeout=timeout)
        listings = parse_listings(html, base_url=current_url, source_url=current_url)
        print(f"Parsed {len(listings)} listings from page {page}", file=sys.stderr)
        all_listings.extend(listings)
        if page < pages and delay > 0:
            time.sleep(delay)

    return all_listings


def scrape_html_file(path: Path, base_url: str) -> list[VehicleListing]:
    html = path.read_text(encoding="utf-8")
    return parse_listings(html, base_url=base_url, source_url=str(path))


def write_csv(listings: Iterable[VehicleListing], path: Path) -> None:
    rows = [asdict(listing) for listing in listings]
    with path.open("w", newline="", encoding="utf-8") as file:
        writer = csv.DictWriter(file, fieldnames=FIELDNAMES)
        writer.writeheader()
        writer.writerows(rows)


def write_json(listings: Iterable[VehicleListing], path: Path) -> None:
    rows = [asdict(listing) for listing in listings]
    path.write_text(json.dumps(rows, indent=2, ensure_ascii=False), encoding="utf-8")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Scrape AutoScout24 vehicle search result pages.")
    source = parser.add_mutually_exclusive_group(required=True)
    source.add_argument("--url", help="AutoScout24 search results URL to fetch.")
    source.add_argument("--market", choices=sorted(MARKET_PRESETS), help="Named market preset to fetch.")
    source.add_argument("--input-html", type=Path, help="Local search result HTML file to parse.")
    parser.add_argument("--base-url", default="https://www.autoscout24.com", help="Base URL for resolving relative links.")
    parser.add_argument("--pages", type=int, default=1, help="Number of result pages to fetch when using --url.")
    parser.add_argument("--delay", type=float, default=2.0, help="Delay between page requests in seconds.")
    parser.add_argument("--timeout", type=int, default=30, help="HTTP timeout in seconds.")
    parser.add_argument("--output", type=Path, default=Path("vehicles.csv"), help="Output file path (.csv or .json).")
    return parser


def main() -> int:
    args = build_parser().parse_args()

    try:
        if args.url or args.market:
            url = args.url or MARKET_PRESETS[args.market]
            listings = scrape_url(url, pages=args.pages, delay=args.delay, timeout=args.timeout)
        else:
            listings = scrape_html_file(args.input_html, base_url=args.base_url)
    except RuntimeError as exc:
        print(f"Error: {exc}", file=sys.stderr)
        return 1

    if args.output.suffix.lower() == ".json":
        write_json(listings, args.output)
    else:
        write_csv(listings, args.output)

    print(f"Wrote {len(listings)} listings to {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
