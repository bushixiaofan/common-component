package com.song.common.datasoure.annotation;

import java.lang.annotation.*;

/**
 * dynamic data source anno
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    String value() default "defaultSource";
}
