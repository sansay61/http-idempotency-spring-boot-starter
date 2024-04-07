package io.sxm.http.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class DefaultHttpRequestExtractor implements HttpRequestExtractor {

    @Override
    public HttpServletRequest extractHttpRequest() {
        Optional<HttpServletRequest> httpServletRequest = Optional.ofNullable(
                        RequestContextHolder.getRequestAttributes()
                )
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
        return httpServletRequest.orElse(null);

    }

}
