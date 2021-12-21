package com.github.jpidem.spring4.registry;

import com.github.jpidem.core.RetryRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 抽象Job spring注册器
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public abstract class AbstractRetryRegistry implements RetryRegistry, BeanFactoryAware, EnvironmentAware {
    // 以list集合的方式操作bean
    protected DefaultListableBeanFactory defaultListableBeanFactory;

    protected Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
