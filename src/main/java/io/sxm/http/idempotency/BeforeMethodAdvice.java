package io.sxm.http.idempotency;

import io.sxm.http.idempotency.annotation.Idempotent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BeforeMethodAdvice implements MethodInterceptor {

    private final IdempotencyHeaderStorageService headerStorageService;
    private final HttpHeaderExtractor headerExtractor;
    private final HttpRequestExtractor requestExtractor;

    private final Map<Method, String> headerNames = new ConcurrentHashMap<>();
    private InheritableThreadLocal<Serializable> ctx = null;

    public BeforeMethodAdvice(
            IdempotencyHeaderStorageService headerStorageService,
            HttpHeaderExtractor headerExtractor,
            HttpRequestExtractor requestExtractor
    ) {
        Assert.notNull(headerExtractor, ()-> "HttpHeaderExtractor instance must not be null");
        Assert.notNull(requestExtractor, ()-> "HttpRequestExtractor instance must not be null");
        Assert.notNull(headerStorageService, ()-> "IdempotencyHeaderStorageService instance must not be null");

        this.headerExtractor = headerExtractor;
        this.requestExtractor = requestExtractor;
        this.headerStorageService = headerStorageService;

        log.info("{} initialized", this.getClass().getSimpleName());
    }

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        if (ctx == null) {
            HttpServletRequest httpServletRequest = requestExtractor.extractHttpRequest();
            if (httpServletRequest == null) {
                return invocation.proceed();
            }

            Method method = invocation.getMethod();
            if (headerNames.get(method) == null) {
                Idempotent annotation = invocation.getMethod().getAnnotation(Idempotent.class);
                headerNames.put(method, annotation.value());
            }

            Serializable idempotencyKeyFromHeader = headerExtractor.extractHeader(httpServletRequest, headerNames.get(method));

            headerStorageService.checkIfNotExist(idempotencyKeyFromHeader);
            headerStorageService.storeIdempotencyKey(idempotencyKeyFromHeader);

            ctx = new InheritableThreadLocal<>();
            ctx.set(idempotencyKeyFromHeader);
        }
        try {
            return invocation.proceed();
        } finally {
            if (ctx != null) {
                ctx.remove();
                ctx = null;
            }
        }
    }
}
