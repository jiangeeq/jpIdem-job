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
 * @author yuni[mn960mn@163.com]
 */
public class RetryAutoConfiguration {

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

    @Bean
    @RetryConditional(missingBeanType = RetryRegistry.class)
    public QuartzRetryRegistry defaultRetryRegistry() {
        return new QuartzRetryRegistry();
    }

    @Bean
    public RetryAnnotationBeanPostProcessor retryAnnotationBeanPostProcessor() {
        return new RetryAnnotationBeanPostProcessor();
    }
}
