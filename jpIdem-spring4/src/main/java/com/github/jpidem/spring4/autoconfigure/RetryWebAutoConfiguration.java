package com.github.jpidem.spring4.autoconfigure;

import com.github.jpidem.spring4.registry.quartz.QuartzRetryRegistry;
import com.github.jpidem.spring4.registry.quartz.web.AdminController;
import com.github.jpidem.spring4.support.RetryConditional;
import org.springframework.context.annotation.Bean;

/**
 * jpIdem job web 相关自动配置
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryWebAutoConfiguration {

    @Bean
    @RetryConditional(hasBeanType = QuartzRetryRegistry.class)
    public AdminController retryAdminController() {
        return new AdminController();
    }
}