package io.sxm.http.idempotency;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class DefaultUuidHttpHeaderExtractor implements HttpHeaderExtractor{

    @Override
    public Serializable extractHeader(HttpServletRequest request, @NonNull String headerName) {

        Assert.notNull(headerName, ()-> headerName +" header must be presented");
        Assert.notNull(request, () -> "HttpServletRequest should not be null");
        var header = Optional.ofNullable(request.getHeader(headerName)).orElseThrow(
                () -> new IllegalArgumentException(headerName + " header not present"));
        try {
            return UUID.fromString(header);
        } catch (Exception e) {
            throw new IllegalArgumentException(headerName + " must be in UUID v4 format");
        }
    }
}
