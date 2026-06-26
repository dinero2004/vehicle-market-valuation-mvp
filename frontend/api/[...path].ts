declare const process: {
  env: Record<string, string | undefined>;
};

const hopByHopHeaders = new Set([
  "connection",
  "content-length",
  "host",
  "keep-alive",
  "proxy-authenticate",
  "proxy-authorization",
  "te",
  "trailer",
  "transfer-encoding",
  "upgrade"
]);

function buildTargetUrl(requestUrl: URL, backendBaseUrl: string): URL {
  const backendUrl = new URL(backendBaseUrl);
  const basePath = backendUrl.pathname === "/"
    ? "/api/"
    : backendUrl.pathname.endsWith("/")
      ? backendUrl.pathname
      : `${backendUrl.pathname}/`;
  const requestPath = requestUrl.pathname.replace(/^\/api\/?/, "");

  backendUrl.pathname = `${basePath}${requestPath}`.replace(/\/{2,}/g, "/");
  backendUrl.search = requestUrl.search;

  return backendUrl;
}

function copyHeaders(sourceHeaders: Headers): Headers {
  const forwardedHeaders = new Headers();

  sourceHeaders.forEach((value, key) => {
    if (!hopByHopHeaders.has(key.toLowerCase())) {
      forwardedHeaders.set(key, value);
    }
  });

  return forwardedHeaders;
}

export default {
  async fetch(request: Request): Promise<Response> {
    const backendBaseUrl = process.env.BACKEND_API_BASE_URL;

    if (!backendBaseUrl) {
      return Response.json(
        {
          message: "Missing BACKEND_API_BASE_URL for the Vercel API proxy."
        },
        { status: 503 }
      );
    }

    const requestUrl = new URL(request.url);
    const targetUrl = buildTargetUrl(requestUrl, backendBaseUrl);
    const requestHeaders = copyHeaders(request.headers);

    const upstreamResponse = await fetch(targetUrl, {
      method: request.method,
      headers: requestHeaders,
      body: request.method === "GET" || request.method === "HEAD"
        ? undefined
        : await request.arrayBuffer(),
      redirect: "manual"
    });

    const responseHeaders = copyHeaders(upstreamResponse.headers);
    responseHeaders.set("x-proxied-by", "vercel");

    return new Response(upstreamResponse.body, {
      status: upstreamResponse.status,
      headers: responseHeaders
    });
  }
};
