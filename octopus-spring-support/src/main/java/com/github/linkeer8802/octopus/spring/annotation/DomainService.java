package com.github.linkeer8802.octopus.spring.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 标识领域服务组件的注解
 * @author wrd
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface DomainService {
    @AliasFor(annotation = Service.class)
    String value() default "";
}
