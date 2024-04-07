package io.sxm.http.idempotency;

import io.sxm.http.idempotency.annotation.Idempotent;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IdempotencyBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor implements InitializingBean, DisposableBean {

    private final BeforeMethodAdvice beforeMethodAdvice;

    private Map<String, Set<Method>> methodsToBeIntercepted = new HashMap<>();

    @Lazy
    public IdempotencyBeanPostProcessor(BeforeMethodAdvice beforeMethodAdvice) {
        this.beforeMethodAdvice = beforeMethodAdvice;
    }

    @Override
    public void destroy() {
        methodsToBeIntercepted.replaceAll((s, v) -> null);
        methodsToBeIntercepted = null;
    }

    @Override
    public void afterPropertiesSet() {
        Pointcut pointcut = new AnnotationMatchingPointcut(Idempotent.class, Idempotent.class, true);
        this.advisor = new DefaultPointcutAdvisor(pointcut, beforeMethodAdvice);
    }

}
