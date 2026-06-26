package com.example.dealership;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Vehicle Market Valuation API</title>
                    <style>
                      body {
                        margin: 0;
                        font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
                        background: #eef2f4;
                        color: #172026;
                      }
                      main {
                        max-width: 880px;
                        margin: 48px auto;
                        padding: 0 24px;
                      }
                      section {
                        background: #fff;
                        border: 1px solid #d8e0e5;
                        border-radius: 8px;
                        padding: 28px;
                        box-shadow: 0 12px 34px rgba(23, 32, 38, 0.07);
                      }
                      h1 {
                        margin: 0 0 10px;
                        font-size: 2rem;
                      }
                      p {
                        color: #4f5f6a;
                        line-height: 1.55;
                      }
                      a {
                        color: #175f78;
                        font-weight: 700;
                      }
                      ul {
                        line-height: 1.9;
                        padding-left: 20px;
                      }
                      code {
                        background: #edf2f4;
                        border-radius: 5px;
                        padding: 3px 6px;
                      }
                    </style>
                  </head>
                  <body>
                    <main>
                      <section>
                        <h1>Vehicle Market Valuation API</h1>
                        <p>
                          This is the Spring Boot backend. The visual React frontend runs separately with Vite.
                        </p>
                        <ul>
                          <li>Frontend: <a href="http://127.0.0.1:5173/">http://127.0.0.1:5173/</a></li>
                          <li>Vehicles API: <a href="/api/vehicles"><code>GET /api/vehicles</code></a></li>
                          <li>Similar listings: <code>GET /api/market-listings/similar/{vehicleId}</code></li>
                          <li>Latest valuation: <code>GET /api/valuations/{vehicleId}</code></li>
                          <li>Calculate valuation: <code>POST /api/valuations/calculate</code></li>
                        </ul>
                      </section>
                    </main>
                  </body>
                </html>
                """;
    }
}
