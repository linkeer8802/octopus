package com.github.linkeer8802.octopus.spring.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标识为工厂的组件注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Factory {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
