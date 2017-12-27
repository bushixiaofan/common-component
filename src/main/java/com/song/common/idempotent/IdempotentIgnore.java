package com.song.common.idempotent;

import java.lang.annotation.*;

/**
 * 幂等器忽略参数注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Documented
public @interface IdempotentIgnore {
}
