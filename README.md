# AutoScout24 Vehicle Scraper

Python scraper for AutoScout24 search result pages using `requests` and `BeautifulSoup`.

It extracts listing ID, title, subtitle, price, make/model, first registration, mileage, fuel type, power, VIN number, type approval, location, image URL, and source page.

## Setup

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Scrape a Live Search URL

Build a search on AutoScout24, copy the result URL, then run:

```bash
python autoscout_scraper.py \
  --url "https://www.autoscout24.com/lst/tesla?atype=C&cy=D&damaged_listing=exclude&desc=0&sort=standard" \
  --pages 2 \
  --output vehicles.csv
```

JSON output is also supported:

```bash
python autoscout_scraper.py \
  --url "https://www.autoscout24.com/lst/bmw?atype=C&desc=0&sort=standard" \
  --pages 1 \
  --output vehicles.json
```

## BMW M4 Swiss Market

The BMW M4 Swiss market preset points to:

```text
https://www.autoscout24.ch/de/s/mo-m4/mk-bmw
```

Try a live fetch with:

```bash
python autoscout_scraper.py \
  --market bmw-m4-ch \
  --pages 1 \
  --output bmw_m4_ch.csv
```

During testing on 2026-06-05, `autoscout24.ch` returned a Cloudflare challenge to plain Python requests. If that happens, use browser-saved HTML:

```bash
python autoscout_scraper.py \
  --input-html bmw_m4_ch.html \
  --base-url "https://www.autoscout24.ch" \
  --output bmw_m4_ch.csv
```

For the database roadmap, see [DATABASE_STRATEGY.md](</Users/lazarminkov/Documents/Data Scraping python/DATABASE_STRATEGY.md>) and [schema.sql](</Users/lazarminkov/Documents/Data Scraping python/schema.sql>).

## Parse Browser-Saved HTML

Some AutoScout markets, including `autoscout24.ch` during testing on 2026-06-05, return bot-protection challenges to plain Python requests. This scraper does not bypass those protections.

For those markets:

1. Open the search results in your browser.
2. Save the page HTML.
3. Parse the saved file:

```bash
python autoscout_scraper.py \
  --input-html saved_autoscout_results.html \
  --base-url "https://www.autoscout24.ch" \
  --output vehicles.csv
```

## Notes

- Keep request volume low and respect AutoScout24 terms and `robots.txt`.
- The parser is designed around current AutoScout24 listing cards with `data-testid="list-item"` and `data-*` vehicle attributes.
- `type_approval` is the English column name for German `Typengenehmigung` / `Typengenehmigungsnummer`.
- If the site changes its markup, update `parse_card()` in `autoscout_scraper.py`.
