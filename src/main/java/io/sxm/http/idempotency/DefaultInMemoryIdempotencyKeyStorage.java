package io.sxm.http.idempotency;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.sxm.http.idempotency.DefaultInMemoryIdempotencyKeyStorage.DefaultInMemoryIdempotencyKeyStorageConfiguration.PREFIX;

public class DefaultInMemoryIdempotencyKeyStorage implements IdempotencyKeyStorage {
    private final Cache<Serializable, Boolean> cache;

    public DefaultInMemoryIdempotencyKeyStorage(DefaultInMemoryIdempotencyKeyStorageConfiguration configuration) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(configuration.getTimeout(), TimeUnit.SECONDS)
                .maximumSize(configuration.getMaxSize())
                .build();
    }

    @Override
    public boolean existsByKey(Serializable idempotencyKey) {
        return Optional.ofNullable(cache.getIfPresent(idempotencyKey)).isPresent();

    }

    @Override
    public void save(Serializable idempotencyKey) {
        this.cache.put(idempotencyKey, true);
    }

    @ConfigurationProperties(prefix = PREFIX)
    @Getter
    @Setter
    public static class DefaultInMemoryIdempotencyKeyStorageConfiguration {
        static final String PREFIX = "http.idempotency.storage.default";

        int timeout = 60;
        int maxSize = 10000;
    }

}
