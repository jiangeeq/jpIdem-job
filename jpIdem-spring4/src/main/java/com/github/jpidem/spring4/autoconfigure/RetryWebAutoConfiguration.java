package com.github.jpidem.spring4.autoconfigure;

import com.github.jpidem.spring4.registry.quartz.QuartzRetryRegistry;
import com.github.jpidem.spring4.registry.quartz.web.AdminController;
import com.github.jpidem.spring4.support.RetryConditional;
import org.springframework.context.annotation.Bean;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryWebAutoConfiguration {

    @Bean
    @RetryConditional(hasBeanType = QuartzRetryRegistry.class)
    public AdminController retryAdminController() {
        return new AdminController();
    }
}