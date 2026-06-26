# Self-Learning Vehicle Valuation Roadmap

## Goal

Build a software system where the user enters a vehicle profile and the system returns:

1. `realTimeMarketValue`: value based on current comparable market listings.
2. `catalogDepreciationValue`: value based on catalog/new price and depreciation rules.
3. `marketCatalogGap`: difference between real market value and catalog depreciation value.
4. `dealerPurchasePrice`: recommended dealer buying price, currently CHF 3,000-4,000 below market selling price.
5. `confidenceScore`: how reliable the valuation is.

The MVP should stay rule-based and explainable first. The self-learning part is added through data collection, feedback, and later model training.

## Required Vehicle Input

The valuation form should collect the following fields:

### Core Identity

- `brand`
- `model`
- `version`
- `bodyType`
- `manufactureYear`
- `firstRegistrationDate`
- `fuelType`
- `transmission`
- `drivetrain`
- `powerKw`
- `engineSizeCcm`

### Usage And Condition

- `mileageKm`
- `conditionGrade`: excellent, good, fair, poor
- `accidentHistory`: none, minor repaired, major repaired, unknown
- `serviceHistory`: complete, partial, missing
- `numberOfOwners`
- `mfkDate`
- `warrantyMonths`

### Swiss/Vehicle-Specific Fields

- `vinNumber`
- `typeApproval`: Typengenehmigung
- `importVehicle`: yes/no
- `location`

### Commercial Fields

- `catalogPrice`
- `purchasePrice`
- `targetListedPrice`
- `dealerMarginMin`: default CHF 3,000
- `dealerMarginMax`: default CHF 4,000

### Equipment

- `equipment`: list of options/packages, for example carbon package, adaptive cruise, leather, panoramic roof, navigation, premium audio.

## Valuation Outputs

Example response:

```json
{
  "vehicleId": 1,
  "realTimeMarketValue": 75900,
  "catalogDepreciationValue": 70500,
  "marketCatalogGap": 5400,
  "dealerPurchasePriceMin": 71900,
  "dealerPurchasePriceMax": 72900,
  "recommendedSellingPrice": 75900,
  "confidenceScore": 0.8,
  "comparablesUsed": 17,
  "explanation": [
    "Market value is based on 17 comparable BMW M4 listings.",
    "Catalog depreciation value uses original catalog price and age/mileage depreciation.",
    "Dealer purchase range is CHF 3,000-4,000 below the estimated market selling price."
  ]
}
```

## Real-Time Market Value Logic

The system searches for comparable listings using:

- same brand,
- same model,
- similar version/trim,
- similar year,
- similar mileage,
- same fuel type,
- same transmission,
- similar power,
- same country/region if available.

The first MVP can use weighted averages:

```text
realTimeMarketValue =
  average comparable price
  + mileage adjustment
  + age adjustment
  + condition adjustment
  + accident adjustment
  + equipment adjustment
```

Later the weighted average can be replaced by a regression or machine learning model.

## Catalog Depreciation Logic

Catalog depreciation starts from the original new price:

```text
catalogDepreciationValue =
  catalogPrice
  * ageDepreciationFactor
  * mileageDepreciationFactor
  * manufacturerDepreciationFactor
  * conditionFactor
```

Example rule-based factors:

| Factor | Example |
|---|---|
| Age depreciation | car loses more value in the first years |
| Mileage depreciation | higher mileage reduces value |
| Manufacturer depreciation | Porsche/BMW M models may retain more value than mass-market models |
| Condition factor | excellent > good > fair > poor |
| Accident factor | repaired accident lowers value |

The system should store depreciation profiles per brand/model segment:

- `brand`
- `model`
- `segment`
- `year1Rate`
- `year2Rate`
- `year3Rate`
- `annualRateAfterYear3`
- `mileageRatePerKm`
- `marketRetentionFactor`

## Dealer Purchase Price Logic

For the first MVP:

```text
dealerPurchasePriceMin = realTimeMarketValue - 4000
dealerPurchasePriceMax = realTimeMarketValue - 3000
```

Later this should become dynamic:

```text
dealerMargin =
  base margin
  + reconditioning risk
  + days-to-sell risk
  + market volatility risk
  + stock desirability adjustment
```

## Self-Learning Data Loop

The system becomes self-learning only if it stores outcomes.

Store:

- every valuation request,
- all comparable listings used,
- calculated output,
- listed price chosen by the user,
- actual sale price if known,
- days until sale,
- whether the vehicle was not sold,
- later price reductions.

Learning loop:

1. Collect market snapshots daily/weekly.
2. Deduplicate listings across snapshots.
3. Track price changes and disappearance from the market.
4. Store valuation results and actual sale outcomes.
5. Compare prediction against real outcome.
6. Adjust depreciation factors.
7. Train ML model once enough data exists.

## Database Additions Needed

Add these tables later:

### `catalog_prices`

Stores original catalog/new prices.

- `brand`
- `model`
- `version`
- `year`
- `basePrice`
- `currency`
- `source`

### `depreciation_profiles`

Stores rule-based depreciation settings.

- `brand`
- `model`
- `segment`
- `manufacturerRetentionFactor`
- `annualDepreciationRates`
- `mileageRatePerKm`

### `valuation_requests`

Stores user input at valuation time.

- complete vehicle profile,
- catalog price,
- condition,
- requested date.

### `valuation_comparables`

Stores which market listings were used for each valuation.

- `valuationId`
- `marketListingId`
- similarity score

### `sales_outcomes`

Stores what happened after valuation.

- `valuationId`
- `finalListedPrice`
- `actualSalePrice`
- `daysToSell`
- `sold`
- `saleDate`

## Machine Learning Stage

Do not start with ML immediately. Start with rules, then collect enough data.

Minimum useful data:

- 1,000+ market listings for basic model,
- 5,000+ listings for stronger model,
- real sale prices are much more valuable than asking prices.

Good first ML model:

- Gradient boosting regression,
- random forest regression,
- or regularized linear regression for explainability.

Features:

- brand,
- model,
- version,
- age,
- mileage,
- fuel type,
- transmission,
- power,
- condition,
- accident history,
- equipment indicators,
- location,
- market supply count,
- average comparable price,
- catalog price,
- catalog depreciation value.

Spring Boot should keep the API and database. Python can run the ML model through FastAPI.

## Practical MVP Phases

### Phase 1: Current System

- PostgreSQL tables.
- Seed market data.
- Rule-based valuation.
- React UI.

### Phase 2: Add Catalog Depreciation

- Add catalog price field to vehicles.
- Add depreciation profile table.
- Return catalog depreciation value and market gap.

### Phase 3: Add Market Data Pipeline

- Import market listings from approved sources, CSV, saved HTML, or licensed APIs.
- Store market snapshots.
- Deduplicate repeated listings.

### Phase 4: Add Feedback

- Store actual listed price and sale outcome.
- Compare estimated value with real sale price.
- Adjust rules.

### Phase 5: Add Python ML

- Train Python model.
- Expose it as FastAPI.
- Spring Boot calls Python for prediction.
- Java rule-based calculator remains fallback.

## Data Access Note

Real-time market value requires reliable and permitted market data access. AutoScout24 Switzerland currently presents bot protection to plain automated requests, and its robots rules disallow many filtered search URL patterns. The preferred options are:

- licensed/approved marketplace data access,
- manual CSV import,
- browser-saved HTML import for MVP testing,
- other permitted data providers.

The system should not bypass CAPTCHA, bot protection, or access restrictions.
