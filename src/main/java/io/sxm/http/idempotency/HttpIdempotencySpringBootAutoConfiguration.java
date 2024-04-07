package io.sxm.http.idempotency;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(DefaultInMemoryIdempotencyKeyStorage.DefaultInMemoryIdempotencyKeyStorageConfiguration.class)
public class HttpIdempotencySpringBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdempotencyKeyStorage.class)
    IdempotencyKeyStorage idempotencyKeyStorage(DefaultInMemoryIdempotencyKeyStorage.DefaultInMemoryIdempotencyKeyStorageConfiguration configuration) {
        return new DefaultInMemoryIdempotencyKeyStorage(configuration);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyHeaderStorageService.class)
    IdempotencyHeaderStorageService idempotencyHeaderStorageService(IdempotencyKeyStorage idempotencyKeyStorage) {
        return new DefaultIdempotencyHeaderStorageService(idempotencyKeyStorage);
    }

    @Bean
    @ConditionalOnMissingBean(HttpRequestExtractor.class)
    HttpRequestExtractor httpRequestExtractor() {
        return new DefaultHttpRequestExtractor();
    }

    @Bean
    @ConditionalOnMissingBean(HttpHeaderExtractor.class)
    HttpHeaderExtractor headerExtractor() {
        return new DefaultUuidHttpHeaderExtractor();
    }

    @Bean
    public IdempotencyBeanPostProcessor idempotencyBeanPostProcessor(
            HttpHeaderExtractor headerExtractor,
            HttpRequestExtractor requestExtractor,
            IdempotencyHeaderStorageService idempotencyHeaderStorageService
    ) {
        BeforeMethodAdvice beforeMethodAdvice = new BeforeMethodAdvice(idempotencyHeaderStorageService, headerExtractor, requestExtractor);
        return new IdempotencyBeanPostProcessor(beforeMethodAdvice);
    }
}
