# Supabase Deployment

## Project

- Project name: `vehicle-market-valuation`
- Project ref: `qyakqyhnaycnauncdzkw`
- Region: `eu-central-1`
- Database host: `db.qyakqyhnaycnauncdzkw.supabase.co`
- Postgres version: `17`
- Cost checked during creation: `0` monthly

Dashboard:

```text
https://supabase.com/dashboard/project/qyakqyhnaycnauncdzkw
```

## Deployed Tables

- `vehicles`
- `market_snapshots`
- `vehicle_market_listings`
- `vehicle_valuations`
- `catalog_prices`
- `depreciation_profiles`

## Seed Data

- 5 target valuation vehicles
- 1 market snapshot
- 50 market listing samples
- 6 catalog price records
- 6 depreciation profiles

## Security

Row Level Security is enabled on every deployed public table.

The tables are not opened to anonymous or authenticated browser clients. This matches the current MVP architecture, where the existing React frontend calls the Spring Boot backend, and the backend should be the only layer that talks to the database.

No database password, service role key, or API secret is stored in this repository.

## Backend Integration Note

To point the Spring Boot backend at Supabase later, set the datasource values through environment variables instead of committing secrets:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db.qyakqyhnaycnauncdzkw.supabase.co:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<supabase-database-password>
```

The password should be copied from the Supabase dashboard and kept outside git.
