# Vehicle Market Valuation Frontend

React + TypeScript visual tester for the Spring Boot valuation API.

## Run

From the project root:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

From this folder:

```bash
npm install
npm run dev
```

Open:

```text
http://127.0.0.1:5173/
```

## What It Calls

- `GET /api/vehicles`
- `GET /api/market-listings/similar/{vehicleId}`
- `GET /api/valuations/{vehicleId}`
- `POST /api/valuations/calculate`
- `POST /api/valuations/manual`

## Deployment

For local development, the Vite dev server proxies `/api` to `http://localhost:8080`.

For a Vercel deployment, the frontend can stay on the same origin and call `/api`. A small Vercel serverless proxy in [frontend/api/[...path].ts](</Users/lazarminkov/Documents/Data Scraping python/frontend/api/[...path].ts>) forwards those requests to the Spring Boot backend.

Set this Vercel environment variable:

```bash
BACKEND_API_BASE_URL=https://your-backend-host
```

You can also point it to the API root directly:

```bash
BACKEND_API_BASE_URL=https://your-backend-host/api
```

The frontend does not talk to Supabase directly in this MVP. The Spring Boot backend should connect to Supabase and expose the valuation API, while the Vercel frontend calls the Vercel-hosted `/api` proxy.

Because the browser only talks to the Vercel domain, backend CORS is usually no longer needed for the production frontend. The backend still needs database access through the Supabase connection settings documented in [SUPABASE_DEPLOYMENT.md](</Users/lazarminkov/Documents/Data Scraping python/SUPABASE_DEPLOYMENT.md>).

## Backend Hosting

The repository now includes a production Dockerfile at [Dockerfile](</Users/lazarminkov/Documents/Data Scraping python/Dockerfile>) so the Spring Boot backend can be deployed to a Java-friendly host such as Render, Railway, or Fly.io.

Typical backend environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db.qyakqyhnaycnauncdzkw.supabase.co:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<supabase-database-password>
SERVER_PORT=8080
```
