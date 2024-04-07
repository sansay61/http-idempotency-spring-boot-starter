package io.sxm.http.idempotency.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    String DEFAULT_HEADER_NAME = "X-IDEMPOTENCY-KEY";

    @AliasFor(value = "value")
    String key() default DEFAULT_HEADER_NAME;

    @AliasFor(value = "key")
    String value() default DEFAULT_HEADER_NAME;
}
