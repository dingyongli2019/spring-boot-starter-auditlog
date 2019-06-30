package com.tongxiaoya.auditlog.config;

import com.tongxiaoya.auditlog.log.LogProcessor;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志自动装配
 *
 * @since 1.8
 */
@Configuration
@ConditionalOnClass({Logger.class})
public class LogAutoConfiguration {

    @Bean
    public LogProcessor logProcessor() {
        return new LogProcessor();
    }
}
