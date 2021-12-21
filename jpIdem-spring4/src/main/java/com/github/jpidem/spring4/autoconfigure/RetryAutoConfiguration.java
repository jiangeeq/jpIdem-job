package com.github.jpidem.spring4.autoconfigure;

import com.github.jpidem.core.RetryRegistry;
import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.spring4.BeanConstants;
import com.github.jpidem.spring4.JdbcRetryTaskMapper;
import com.github.jpidem.spring4.RetryAnnotationBeanPostProcessor;
import com.github.jpidem.spring4.registry.quartz.QuartzRetryRegistry;
import com.github.jpidem.spring4.support.RetryConditional;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * jpIdem job相关自动配置
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryAutoConfiguration {
    /**
     * @param beanFactory spring的beanFactory
     * @return {@link com.github.jpidem.core.RetryTaskMapper} 对数据库表的sql操作类
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */

    @Bean
    @RetryConditional(missingBeanType = RetryTaskMapper.class)
    public RetryTaskMapper defaultRetryTaskMapper(BeanFactory beanFactory) {
        //优先取自定义的DataSource，否则从容器中获取一个DataSource
        DataSource dataSource;
        if (beanFactory.containsBean(BeanConstants.DEFAULT_DATASOURCE)) {
            dataSource = beanFactory.getBean(BeanConstants.DEFAULT_DATASOURCE, DataSource.class);
        } else {
            dataSource = beanFactory.getBean(DataSource.class);
        }
        return new JdbcRetryTaskMapper(dataSource);
    }

    /**
     * 用于注册RetryHandler相关类实例
     *
     * @return 任务注册器
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    @Bean
    @RetryConditional(missingBeanType = RetryRegistry.class)
    public QuartzRetryRegistry defaultRetryRegistry() {
        return new QuartzRetryRegistry();
    }

    /**
     * @return
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    @Bean
    public RetryAnnotationBeanPostProcessor retryAnnotationBeanPostProcessor() {
        return new RetryAnnotationBeanPostProcessor();
    }
}
