package com.tongxiaoya.auditlog.log;

import java.lang.annotation.*;

/**
 * 异常日志
 *
 * @since 1.8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThrowingLog {

    /**
     * 业务名称
     */
    String value();
}
