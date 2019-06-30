package com.tongxiaoya.auditlog.log;

import java.lang.annotation.*;

/**
 * 参数日志
 *
 * @since 1.8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamLog {
    /**
     * 业务名称
     */
    String value();

    /**
     * 日志级别
     */
    Level level() default Level.DEBUG;

    /**
     * 代码定位支持
     */
    Position position() default Position.DEFAULT;
}
