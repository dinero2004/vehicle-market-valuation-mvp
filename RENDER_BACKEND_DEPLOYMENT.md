# Render Backend Deployment

This backend is prepared for deployment as a Docker web service on Render.

## Why Render

- Render supports Docker-based Spring Boot services.
- Render still documents a `free` web-service plan as of June 26, 2026.
- Render web services should bind to the `PORT` environment variable, which this app now supports through [application.yml](</Users/lazarminkov/Documents/Data Scraping python/src/main/resources/application.yml>).

Official docs used:

- [Render Web Services](https://render.com/docs/web-services)
- [Deploy for Free](https://render.com/docs/free)
- [Docker on Render](https://render.com/docs/docker)
- [Blueprint YAML Reference](https://render.com/docs/blueprint-spec)

## Files Added

- [render.yaml](</Users/lazarminkov/Documents/Data Scraping python/render.yaml>)
- [Dockerfile](</Users/lazarminkov/Documents/Data Scraping python/Dockerfile>)

## Required Environment Variables

Set these in Render for the backend service:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db.qyakqyhnaycnauncdzkw.supabase.co:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<your-supabase-db-password>
APP_CORS_ALLOWED_ORIGINS=https://vehicle-market-valuation-frontend.vercel.app
```

For preview deployments you can also temporarily allow the preview URL:

```bash
APP_CORS_ALLOWED_ORIGINS=https://vehicle-market-valuation-frontend.vercel.app,https://vehicle-market-valuation-frontend-kwf49pq9u.vercel.app
```

## Deploy in Render

1. Push this repository to GitHub.
2. In Render, create a new Blueprint or Web Service from the repo.
3. If using the Blueprint flow, Render reads [render.yaml](</Users/lazarminkov/Documents/Data Scraping python/render.yaml>).
4. Add the required environment variables above.
5. Deploy the service.

After deployment, the backend root page should load at:

```text
https://your-render-service.onrender.com/
```

And the API should answer at:

```text
https://your-render-service.onrender.com/api/vehicles
```

## Connect Vercel Frontend

After the backend is live, set this in the Vercel frontend project:

```bash
BACKEND_API_BASE_URL=https://your-render-service.onrender.com
```

Then redeploy the Vercel frontend.
