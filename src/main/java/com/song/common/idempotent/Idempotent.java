package com.song.common.idempotent;

import java.lang.annotation.*;

/**
 * Idempotent annotation
 *
 * controller注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    int timeout() default 3000;
}
