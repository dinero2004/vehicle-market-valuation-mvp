# Vehicle Market Valuation Backend

This Spring Boot MVP provides standalone software for estimating the current market value of a used vehicle. It is not intended to be a full dealer management system.

## Main Tables

- `vehicles`: valuation target vehicles.
- `market_snapshots`: one market collection run, for example BMW M4 Switzerland on a specific date.
- `vehicle_market_listings`: comparable cars collected from market sources.
- `catalog_prices`: original catalog/new prices used for catalog depreciation.
- `depreciation_profiles`: explainable depreciation settings per brand/model segment.
- `vehicle_valuations`: stored valuation results for a vehicle.

## API Endpoints

List valuation input vehicles:

```bash
curl http://localhost:8080/api/vehicles
```

Calculate and store a valuation:

```bash
curl -X POST http://localhost:8080/api/valuations/calculate \
  -H "Content-Type: application/json" \
  -d '{"vehicleId":1,"yearRange":2,"mileageRange":30000}'
```

Get latest valuation for a vehicle:

```bash
curl http://localhost:8080/api/valuations/1
```

Get similar market listings:

```bash
curl http://localhost:8080/api/market-listings/similar/1
```

## Rule-Based Calculation

The valuation calculator:

1. Finds similar listings with the same brand/model, within a configurable year and mileage range.
2. Calculates the average asking price of those market listings.
3. Adjusts for mileage at CHF 0.08 per km above/below the comparable average.
4. Adjusts for age at CHF 1,200 per year above/below the comparable average.
5. Applies condition adjustment:
   - Excellent: +5%
   - Good: 0%
   - Fair: -7%
   - Poor: -15%
6. Applies accident adjustment:
   - None: 0%
   - Minor repaired: -5%
   - Major/yes: -12%
   - Unknown: -3%
7. Looks up a catalog price and depreciation profile.
8. Calculates:
   - estimated live market value from comparable listings
   - catalog depreciation value from new price, age, mileage, manufacturer retention, condition, and accident history
   - market/catalog gap
   - dealer purchase range at CHF 3,000-4,000 below market value
   - trade-in reference at 88% of market value
   - recommended selling price equal to the estimated market value
   - confidence score based on sample size and price variance

The current MVP intentionally keeps the formula simple and explainable. The dealer purchase range is the value a dealer could offer while leaving room for reconditioning, negotiation, and margin.

## PostgreSQL

Create a local database:

```bash
createdb dealer_mgmt
```

Run the backend:

```bash
mvn spring-boot:run
```

Override database settings if needed:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dealer_mgmt \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
mvn spring-boot:run
```

Flyway creates the tables and inserts seed data automatically, including the first catalog-price and depreciation-profile records.

## Frontend

The visual tester is in [frontend](</Users/lazarminkov/Documents/Data Scraping python/frontend>).

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Start Spring Boot:

```bash
mvn spring-boot:run
```

Start React:

```bash
cd frontend
npm install
npm run dev
```

Open:

```text
http://127.0.0.1:5173/
```

The Vite dev server proxies `/api` requests to `http://localhost:8080`.

## Later Python ML Replacement

Keep the Spring Boot API stable and replace only the valuation engine:

- Spring Boot continues to own valuation input vehicles, market listings, valuation results, and public API contracts.
- Python receives a valuation request plus comparable listings.
- Python returns `estimatedMarketValue`, `catalogDepreciationValue`, `marketCatalogGap`, `dealerPurchasePriceMin`, `dealerPurchasePriceMax`, `recommendedSellingPrice`, `confidenceScore`, and explanation fields.

Possible integration options:

- HTTP microservice: Spring calls a Python FastAPI model service.
- Batch scoring: Python writes predictions back into PostgreSQL.
- Hybrid: rule-based fallback in Java when the ML service is unavailable.

The current rule-based calculator can become the fallback and benchmark for the ML model.
