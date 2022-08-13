package com.log.record.core.annotation;

import java.lang.annotation.*;

/**
 * @author bo
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecords {

    LogRecord[] value();

}
