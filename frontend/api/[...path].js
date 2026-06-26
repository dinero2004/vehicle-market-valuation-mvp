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

function buildTargetUrl(requestUrl, backendBaseUrl) {
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

function copyRequestHeaders(sourceHeaders) {
  const forwardedHeaders = new Headers();

  Object.entries(sourceHeaders).forEach(([key, value]) => {
    if (hopByHopHeaders.has(key.toLowerCase()) || value == null) {
      return;
    }

    if (Array.isArray(value)) {
      forwardedHeaders.set(key, value.join(", "));
      return;
    }

    forwardedHeaders.set(key, value);
  });

  return forwardedHeaders;
}

function buildRequestBody(request) {
  if (request.method === "GET" || request.method === "HEAD") {
    return undefined;
  }

  if (request.body == null) {
    return undefined;
  }

  if (typeof request.body === "string" || Buffer.isBuffer(request.body)) {
    return request.body;
  }

  return JSON.stringify(request.body);
}

export default async function handler(request, response) {
  const backendBaseUrl = process.env.BACKEND_API_BASE_URL;

  if (!backendBaseUrl) {
    response.status(503).json({
      message: "Missing BACKEND_API_BASE_URL for the Vercel API proxy."
    });
    return;
  }

  const host = request.headers.host ?? "localhost";
  const protocol = request.headers["x-forwarded-proto"] ?? "https";
  const requestUrl = new URL(request.url ?? "/", `${protocol}://${host}`);
  const targetUrl = buildTargetUrl(requestUrl, backendBaseUrl);
  const requestHeaders = copyRequestHeaders(request.headers);

  const upstreamResponse = await fetch(targetUrl, {
    method: request.method,
    headers: requestHeaders,
    body: buildRequestBody(request),
    redirect: "manual"
  });

  upstreamResponse.headers.forEach((value, key) => {
    if (!hopByHopHeaders.has(key.toLowerCase())) {
      response.setHeader(key, value);
    }
  });

  response.setHeader("x-proxied-by", "vercel");
  response.status(upstreamResponse.status);

  if (request.method === "HEAD" || upstreamResponse.status === 204 || upstreamResponse.status === 304) {
    response.end();
    return;
  }

  const bodyBuffer = Buffer.from(await upstreamResponse.arrayBuffer());
  response.send(bodyBuffer);
}
