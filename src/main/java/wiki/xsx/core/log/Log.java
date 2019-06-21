package wiki.xsx.core.log;

import java.lang.annotation.*;

/**
 * 综合日志
 * @author xsx
 * @date 2019/6/18
 * @since 1.8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 业务名称
     * @return
     */
    String value();

    /**
     * 日志级别
     * @return
     */
    Level level() default Level.DEBUG;

    /**
     * 代码定位支持
     * @return
     */
    Position position() default Position.DEFAULT;
}
