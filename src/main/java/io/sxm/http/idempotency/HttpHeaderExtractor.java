package io.sxm.http.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;

import java.io.Serializable;

public interface HttpHeaderExtractor {
    Serializable extractHeader(HttpServletRequest request, @NonNull String headerName);
}
