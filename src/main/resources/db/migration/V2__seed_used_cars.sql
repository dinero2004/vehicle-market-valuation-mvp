INSERT INTO vehicles (
    brand, model, version, manufacture_year, mileage, fuel_type, transmission, power_kw,
    vehicle_condition, accident_history, equipment, location, purchase_price, listed_price
) VALUES
    ('BMW', 'M4', 'Competition Coupe', 2021, 42000, 'Petrol', 'Automatic', 375, 'Good', 'None', 'Carbon roof, adaptive suspension, head-up display, parking camera', 'Zurich', 62500.00, 71900.00),
    ('BMW', 'M4', 'Convertible xDrive', 2022, 26000, 'Petrol', 'Automatic', 375, 'Excellent', 'None', 'xDrive, leather, laser lights, Harman Kardon, driving assistant', 'Geneva', 76000.00, 84900.00),
    ('BMW', 'M3', 'Competition Touring xDrive', 2023, 19000, 'Petrol', 'Automatic', 375, 'Excellent', 'None', 'xDrive, panoramic roof, carbon trim, adaptive cruise', 'Basel', 85000.00, 94900.00),
    ('Audi', 'RS5', 'Sportback quattro', 2021, 45000, 'Petrol', 'Automatic', 331, 'Good', 'None', 'Quattro, sport exhaust, Matrix LED, navigation plus', 'Bern', 59000.00, 67500.00),
    ('Mercedes-Benz', 'C63 AMG', 'S Coupe', 2019, 56000, 'Petrol', 'Automatic', 375, 'Good', 'Minor repaired', 'AMG performance seats, Burmester, panoramic roof', 'Lucerne', 54500.00, 62900.00);

INSERT INTO market_snapshots (
    source, brand, model, version, search_url, snapshot_date, total_listings, notes
) VALUES (
    'Seed data',
    'BMW',
    'M4',
    'Swiss used performance cars',
    'https://www.autoscout24.ch/de/s/mo-m4/mk-bmw',
    '2026-06-15 12:00:00',
    50,
    'Bachelor MVP sample data with realistic asking prices for valuation tests.'
);

WITH snapshot AS (
    SELECT id FROM market_snapshots WHERE source = 'Seed data' ORDER BY id DESC LIMIT 1
)
INSERT INTO vehicle_market_listings (
    market_snapshot_id, source, brand, model, version, manufacture_year, mileage, price, location, listing_date, url
)
SELECT snapshot.id, data.source, data.brand, data.model, data.version, data.manufacture_year,
       data.mileage, data.price, data.location, data.listing_date, data.url
FROM snapshot
CROSS JOIN (
    VALUES
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2024, 8500, 104900.00, 'Zurich', DATE '2026-06-01', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-001'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition xDrive Coupe', 2023, 14500, 96900.00, 'Geneva', DATE '2026-06-02', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-002'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2023, 22000, 92500.00, 'Basel', DATE '2026-06-03', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-003'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition xDrive Convertible', 2023, 18000, 98900.00, 'Lausanne', DATE '2026-06-04', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-004'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2022, 26500, 84900.00, 'Winterthur', DATE '2026-06-05', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-005'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition xDrive Coupe', 2022, 31000, 82900.00, 'St. Gallen', DATE '2026-06-05', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-006'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Convertible', 2022, 24000, 87900.00, 'Lugano', DATE '2026-06-06', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-007'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2021, 39000, 73900.00, 'Zurich', DATE '2026-06-06', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-008'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2021, 45500, 71400.00, 'Bern', DATE '2026-06-07', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-009'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition xDrive Coupe', 2021, 33000, 77900.00, 'Aarau', DATE '2026-06-07', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-010'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2020, 52000, 66900.00, 'Thun', DATE '2026-06-08', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-011'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2020, 61000, 63900.00, 'Chur', DATE '2026-06-08', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-012'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Coupe', 2019, 58000, 58900.00, 'Fribourg', DATE '2026-06-09', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-013'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2019, 69000, 59900.00, 'Sion', DATE '2026-06-09', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-014'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Convertible', 2019, 72000, 57500.00, 'Neuchatel', DATE '2026-06-10', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-015'),
    ('AutoScout24 CH', 'BMW', 'M4', 'CS Coupe', 2018, 46000, 78900.00, 'Zurich', DATE '2026-06-10', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-016'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2018, 76000, 52900.00, 'Lucerne', DATE '2026-06-11', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-017'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Coupe', 2018, 82000, 49900.00, 'Biel', DATE '2026-06-11', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-018'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Convertible', 2018, 66000, 54900.00, 'Geneva', DATE '2026-06-12', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-019'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Coupe', 2017, 91000, 45500.00, 'Basel', DATE '2026-06-12', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-020'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2017, 88000, 48900.00, 'Schaffhausen', DATE '2026-06-13', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-021'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Convertible', 2017, 94000, 45900.00, 'Lausanne', DATE '2026-06-13', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-022'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Coupe', 2016, 98000, 41900.00, 'Lugano', DATE '2026-06-14', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-023'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2016, 103000, 43900.00, 'Solothurn', DATE '2026-06-14', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-024'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Coupe', 2015, 112000, 38900.00, 'St. Gallen', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-025'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Convertible', 2015, 119000, 37900.00, 'Bern', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-026'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2024, 12000, 101500.00, 'Zug', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-027'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2022, 38500, 79900.00, 'Uster', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-028'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Coupe', 2021, 51500, 68900.00, 'Kloten', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-029'),
    ('AutoScout24 CH', 'BMW', 'M4', 'Competition Convertible', 2020, 57500, 67900.00, 'Montreux', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/bmw-m4-seed-030'),
    ('AutoScout24 CH', 'BMW', 'M3', 'Competition Limousine', 2023, 21000, 88900.00, 'Zurich', DATE '2026-06-02', 'https://www.autoscout24.ch/de/d/bmw-m3-seed-031'),
    ('AutoScout24 CH', 'BMW', 'M3', 'Competition Touring xDrive', 2024, 15000, 108900.00, 'Bern', DATE '2026-06-03', 'https://www.autoscout24.ch/de/d/bmw-m3-seed-032'),
    ('AutoScout24 CH', 'BMW', 'M3', 'Competition Limousine', 2021, 47000, 73900.00, 'Basel', DATE '2026-06-04', 'https://www.autoscout24.ch/de/d/bmw-m3-seed-033'),
    ('AutoScout24 CH', 'Audi', 'RS5', 'Sportback quattro', 2023, 18000, 84900.00, 'Geneva', DATE '2026-06-05', 'https://www.autoscout24.ch/de/d/audi-rs5-seed-034'),
    ('AutoScout24 CH', 'Audi', 'RS5', 'Coupe quattro', 2021, 42000, 66900.00, 'Zurich', DATE '2026-06-06', 'https://www.autoscout24.ch/de/d/audi-rs5-seed-035'),
    ('AutoScout24 CH', 'Audi', 'RS5', 'Sportback quattro', 2020, 59000, 61900.00, 'Lucerne', DATE '2026-06-07', 'https://www.autoscout24.ch/de/d/audi-rs5-seed-036'),
    ('AutoScout24 CH', 'Mercedes-Benz', 'C63 AMG', 'S Coupe', 2020, 53000, 69900.00, 'Lausanne', DATE '2026-06-08', 'https://www.autoscout24.ch/de/d/mercedes-c63-seed-037'),
    ('AutoScout24 CH', 'Mercedes-Benz', 'C63 AMG', 'S Limousine', 2019, 67000, 62500.00, 'Basel', DATE '2026-06-09', 'https://www.autoscout24.ch/de/d/mercedes-c63-seed-038'),
    ('AutoScout24 CH', 'Mercedes-Benz', 'C63 AMG', 'S T-Modell', 2018, 81000, 57900.00, 'St. Gallen', DATE '2026-06-10', 'https://www.autoscout24.ch/de/d/mercedes-c63-seed-039'),
    ('AutoScout24 CH', 'Porsche', '911', 'Carrera S Coupe', 2020, 41000, 112900.00, 'Zurich', DATE '2026-06-11', 'https://www.autoscout24.ch/de/d/porsche-911-seed-040'),
    ('AutoScout24 CH', 'Porsche', '911', 'Carrera 4S Cabriolet', 2021, 36000, 129900.00, 'Geneva', DATE '2026-06-11', 'https://www.autoscout24.ch/de/d/porsche-911-seed-041'),
    ('AutoScout24 CH', 'Porsche', '911', 'Carrera Coupe', 2018, 65000, 89900.00, 'Bern', DATE '2026-06-12', 'https://www.autoscout24.ch/de/d/porsche-911-seed-042'),
    ('AutoScout24 CH', 'BMW', 'M2', 'Competition Coupe', 2020, 54000, 51900.00, 'Winterthur', DATE '2026-06-12', 'https://www.autoscout24.ch/de/d/bmw-m2-seed-043'),
    ('AutoScout24 CH', 'BMW', 'M2', 'Coupe', 2023, 17500, 72900.00, 'Zug', DATE '2026-06-13', 'https://www.autoscout24.ch/de/d/bmw-m2-seed-044'),
    ('AutoScout24 CH', 'BMW', 'M5', 'Competition Limousine', 2020, 62000, 75900.00, 'Lugano', DATE '2026-06-13', 'https://www.autoscout24.ch/de/d/bmw-m5-seed-045'),
    ('AutoScout24 CH', 'BMW', 'M5', 'Competition Limousine', 2022, 31000, 96900.00, 'Zurich', DATE '2026-06-14', 'https://www.autoscout24.ch/de/d/bmw-m5-seed-046'),
    ('AutoScout24 CH', 'Alfa Romeo', 'Giulia', 'Quadrifoglio', 2021, 39000, 63900.00, 'Lausanne', DATE '2026-06-14', 'https://www.autoscout24.ch/de/d/alfa-giulia-seed-047'),
    ('AutoScout24 CH', 'Maserati', 'Ghibli', 'Trofeo', 2021, 45000, 71900.00, 'Basel', DATE '2026-06-14', 'https://www.autoscout24.ch/de/d/maserati-ghibli-seed-048'),
    ('AutoScout24 CH', 'Jaguar', 'F-Type', 'R Coupe AWD', 2020, 52000, 69900.00, 'Geneva', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/jaguar-ftype-seed-049'),
    ('AutoScout24 CH', 'Lexus', 'RC F', 'Performance Package', 2019, 61000, 59900.00, 'Zurich', DATE '2026-06-15', 'https://www.autoscout24.ch/de/d/lexus-rcf-seed-050')
) AS data(source, brand, model, version, manufacture_year, mileage, price, location, listing_date, url);
