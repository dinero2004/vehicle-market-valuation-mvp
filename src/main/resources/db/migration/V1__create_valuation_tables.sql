CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    version VARCHAR(160),
    manufacture_year INTEGER NOT NULL,
    mileage INTEGER NOT NULL,
    fuel_type VARCHAR(60),
    transmission VARCHAR(60),
    power_kw INTEGER,
    vehicle_condition VARCHAR(40),
    accident_history VARCHAR(80),
    equipment VARCHAR(2000),
    location VARCHAR(160),
    purchase_price NUMERIC(12, 2),
    listed_price NUMERIC(12, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE market_snapshots (
    id BIGSERIAL PRIMARY KEY,
    source VARCHAR(120) NOT NULL,
    brand VARCHAR(80),
    model VARCHAR(80),
    version VARCHAR(160),
    search_url VARCHAR(2000),
    snapshot_date TIMESTAMP NOT NULL,
    total_listings INTEGER,
    notes VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicle_market_listings (
    id BIGSERIAL PRIMARY KEY,
    market_snapshot_id BIGINT REFERENCES market_snapshots(id),
    source VARCHAR(120) NOT NULL,
    brand VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    version VARCHAR(160),
    manufacture_year INTEGER NOT NULL,
    mileage INTEGER NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    location VARCHAR(160),
    listing_date DATE,
    url VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicle_valuations (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    valuation_date TIMESTAMP NOT NULL,
    average_market_price NUMERIC(12, 2) NOT NULL,
    estimated_market_value NUMERIC(12, 2) NOT NULL,
    trade_in_value NUMERIC(12, 2) NOT NULL,
    recommended_selling_price NUMERIC(12, 2) NOT NULL,
    confidence_score DOUBLE PRECISION NOT NULL,
    similar_listings_count INTEGER NOT NULL,
    mileage_adjustment NUMERIC(12, 2),
    age_adjustment NUMERIC(12, 2),
    condition_adjustment NUMERIC(12, 2),
    accident_adjustment NUMERIC(12, 2),
    explanation VARCHAR(2000)
);

CREATE INDEX idx_vehicles_brand_model_year_mileage
    ON vehicles (lower(brand), lower(model), manufacture_year, mileage);

CREATE INDEX idx_market_listings_brand_model_year_mileage
    ON vehicle_market_listings (lower(brand), lower(model), manufacture_year, mileage);

CREATE INDEX idx_market_listings_price
    ON vehicle_market_listings (price);

CREATE INDEX idx_vehicle_valuations_vehicle_date
    ON vehicle_valuations (vehicle_id, valuation_date DESC);
