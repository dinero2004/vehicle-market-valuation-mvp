CREATE TABLE IF NOT EXISTS scrape_runs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    market_key TEXT NOT NULL,
    source_url TEXT NOT NULL,
    started_at TEXT NOT NULL,
    finished_at TEXT,
    pages_requested INTEGER,
    listings_found INTEGER,
    notes TEXT
);

CREATE TABLE IF NOT EXISTS listings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    listing_id TEXT,
    market_key TEXT NOT NULL,
    url TEXT,
    vin_number TEXT,
    type_approval TEXT,
    make TEXT,
    model TEXT,
    title TEXT,
    subtitle TEXT,
    first_seen_at TEXT NOT NULL,
    last_seen_at TEXT NOT NULL,
    UNIQUE (market_key, listing_id),
    UNIQUE (market_key, url)
);

CREATE TABLE IF NOT EXISTS listing_observations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    run_id INTEGER NOT NULL,
    listing_row_id INTEGER NOT NULL,
    observed_at TEXT NOT NULL,
    price TEXT,
    currency TEXT,
    price_amount INTEGER,
    first_registration TEXT,
    mileage_km INTEGER,
    fuel_type TEXT,
    power TEXT,
    seller_type TEXT,
    listing_country TEXT,
    listing_zip_code TEXT,
    image_url TEXT,
    source_url TEXT,
    FOREIGN KEY (run_id) REFERENCES scrape_runs (id),
    FOREIGN KEY (listing_row_id) REFERENCES listings (id)
);

CREATE TABLE IF NOT EXISTS market_segments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    market_key TEXT NOT NULL,
    segment_name TEXT NOT NULL,
    min_year INTEGER,
    max_year INTEGER,
    min_mileage_km INTEGER,
    max_mileage_km INTEGER,
    variant_keyword TEXT,
    UNIQUE (market_key, segment_name)
);

CREATE INDEX IF NOT EXISTS idx_observations_price ON listing_observations (price_amount);
CREATE INDEX IF NOT EXISTS idx_observations_mileage ON listing_observations (mileage_km);
CREATE INDEX IF NOT EXISTS idx_observations_run ON listing_observations (run_id);
CREATE INDEX IF NOT EXISTS idx_listings_market ON listings (market_key);
