CREATE TABLE catalog_prices (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    version VARCHAR(160),
    manufacture_year INTEGER NOT NULL,
    catalog_price NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'CHF',
    source VARCHAR(160),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE depreciation_profiles (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(80),
    model VARCHAR(80),
    segment VARCHAR(120),
    year_one_rate NUMERIC(6, 4) NOT NULL,
    year_two_rate NUMERIC(6, 4) NOT NULL,
    year_three_rate NUMERIC(6, 4) NOT NULL,
    annual_rate_after_year_three NUMERIC(6, 4) NOT NULL,
    mileage_rate_per_km NUMERIC(10, 8) NOT NULL,
    manufacturer_retention_factor NUMERIC(6, 4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE vehicle_valuations
    ADD COLUMN catalog_price NUMERIC(12, 2),
    ADD COLUMN catalog_depreciation_value NUMERIC(12, 2),
    ADD COLUMN market_catalog_gap NUMERIC(12, 2),
    ADD COLUMN dealer_purchase_price_min NUMERIC(12, 2),
    ADD COLUMN dealer_purchase_price_max NUMERIC(12, 2);

CREATE INDEX idx_catalog_prices_brand_model_year
    ON catalog_prices (lower(brand), lower(model), manufacture_year);

CREATE INDEX idx_depreciation_profiles_brand_model
    ON depreciation_profiles (lower(brand), lower(model));

INSERT INTO depreciation_profiles (
    brand, model, segment, year_one_rate, year_two_rate, year_three_rate,
    annual_rate_after_year_three, mileage_rate_per_km, manufacturer_retention_factor
) VALUES
    ('BMW', 'M4', 'Performance coupe', 0.1500, 0.1100, 0.0800, 0.0550, 0.000004, 1.0600),
    ('BMW', 'M3', 'Performance sedan/touring', 0.1400, 0.1000, 0.0750, 0.0525, 0.000004, 1.0700),
    ('Audi', 'RS5', 'Performance coupe/sportback', 0.1600, 0.1150, 0.0850, 0.0600, 0.000004, 1.0200),
    ('Mercedes-Benz', 'C63 AMG', 'Performance sedan/coupe', 0.1700, 0.1200, 0.0900, 0.0625, 0.000004, 1.0000),
    ('Porsche', '911', 'Sports car', 0.1000, 0.0750, 0.0550, 0.0350, 0.000003, 1.1800),
    (NULL, NULL, 'Default used car', 0.1800, 0.1300, 0.1000, 0.0700, 0.000005, 1.0000);

INSERT INTO catalog_prices (
    brand, model, version, manufacture_year, catalog_price, currency, source
) VALUES
    ('BMW', 'M4', 'Competition Coupe', 2021, 119900.00, 'CHF', 'Seed catalog estimate'),
    ('BMW', 'M4', 'Convertible xDrive', 2022, 132900.00, 'CHF', 'Seed catalog estimate'),
    ('BMW', 'M3', 'Competition Touring xDrive', 2023, 126900.00, 'CHF', 'Seed catalog estimate'),
    ('Audi', 'RS5', 'Sportback quattro', 2021, 112500.00, 'CHF', 'Seed catalog estimate'),
    ('Mercedes-Benz', 'C63 AMG', 'S Coupe', 2019, 124900.00, 'CHF', 'Seed catalog estimate'),
    ('Porsche', '911', 'Carrera S Coupe', 2020, 157900.00, 'CHF', 'Seed catalog estimate');
