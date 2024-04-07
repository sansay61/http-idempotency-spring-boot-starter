package io.sxm.http.idempotency;

import jakarta.servlet.http.HttpServletRequest;

public interface HttpRequestExtractor {
    HttpServletRequest extractHttpRequest();
}
