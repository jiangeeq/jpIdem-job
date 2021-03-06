package com.github.jpidem.spring4.registry.quartz;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * 可以对{@link  RetrySchedulerFactoryBean}对象做后置处理
 *
 * @author yuni[mn960mn@163.com]
 * @see QuartzRetryRegistry
 */
@FunctionalInterface
public interface RetryBeanDefinitionBuilderCustomizer {

    /**
     * @param identity              RetryHandler的唯一标识
     * @param beanDefinitionBuilder BeanDefinitionBuilder.rootBeanDefinition(RetrySchedulerFactoryBean.class);
     */
    void customize(String identity, BeanDefinitionBuilder beanDefinitionBuilder);
}