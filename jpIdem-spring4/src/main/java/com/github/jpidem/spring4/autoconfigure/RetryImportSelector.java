package com.github.jpidem.spring4.autoconfigure;

import com.github.jpidem.spring4.EnableRetrying;
import com.github.jpidem.spring4.EnvironmentConstants;
import com.github.jpidem.spring4.aop.RetryAdvisorAutoProxyCreator;
import com.github.jpidem.spring4.aop.RetryHandlerClassInterceptor;
import com.github.jpidem.spring4.aop.RetryHandlerClassPointcut;
import com.github.jpidem.spring4.aop.RetryHandlerMethodInterceptor;
import com.github.jpidem.spring4.aop.RetryHandlerMethodPointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自己编写一个注解，让Spring在启动时扫描指定目录下带有指定注解的的类创建并加载到Spring容器中
 * 使用{@link ImportBeanDefinitionRegistrar}动态创建自定义Bean到Spring中
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryImportSelector implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (retryEnabled()) {
            registerProxyCreator(importingClassMetadata, registry);
            // RootBeanDefinition可用于没有继承关系的Bean的创建
            registry.registerBeanDefinition(RetryAutoConfiguration.class.getName(), new RootBeanDefinition(RetryAutoConfiguration.class));

            if (retryWebEnabled()) {
                // RootBeanDefinition可用于没有继承关系的Bean的创建
                registry.registerBeanDefinition(RetryWebAutoConfiguration.class.getName(), new RootBeanDefinition(RetryWebAutoConfiguration.class));
            }
        }
    }

    protected void registerProxyCreator(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 检索启动时指定类型EnableRetrying的注释的属性
        Map<String, Object> annotationData = importingClassMetadata.getAnnotationAttributes(EnableRetrying.class.getName());
        boolean proxyTargetClass = (Boolean) annotationData.get("proxyTargetClass");
        boolean exposeProxy = (Boolean) annotationData.get("exposeProxy");
        Integer order = (Integer) annotationData.get("order");
        registerAdvisorAutoProxyCreator(registry, proxyTargetClass, exposeProxy, order);
    }

    protected void registerAdvisorAutoProxyCreator(BeanDefinitionRegistry registry, boolean proxyTargetClass, boolean exposeProxy, Integer order) {
        List<Advisor> retryAdvisors = new ArrayList<>();
        retryAdvisors.add(new DefaultPointcutAdvisor(new RetryHandlerMethodPointcut(), new RetryHandlerMethodInterceptor()));
        retryAdvisors.add(new DefaultPointcutAdvisor(new RetryHandlerClassPointcut(), new RetryHandlerClassInterceptor()));

        //创建一个新的用于构造RootBeanDefinition的BeanDefinitionBuilder, 在多继承体系中，RootBeanDefinition代表的是当前初始化类的父类的BeanDefinition; 在多继承体系中，RootBeanDefinition代表的是当前初始化类的父类的BeanDefinition
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(RetryAdvisorAutoProxyCreator.class);
        // 在给定的属性名下添加提供的属性值
        beanDefinitionBuilder.addPropertyValue("proxyTargetClass", proxyTargetClass);
        beanDefinitionBuilder.addPropertyValue("exposeProxy", exposeProxy);
        beanDefinitionBuilder.addPropertyValue("order", order);
        beanDefinitionBuilder.addPropertyValue("retryAdvisors", retryAdvisors);

        registry.registerBeanDefinition("retryAdvisorAutoProxyCreator", beanDefinitionBuilder.getBeanDefinition());
    }

    protected boolean retryEnabled() {
        return environment.getProperty(EnvironmentConstants.RETRY_ENABLED, Boolean.class, true);
    }

    protected boolean retryWebEnabled() {
        return environment.getProperty(EnvironmentConstants.RETRY_WEB_ENABLED, Boolean.class, true);
    }
}