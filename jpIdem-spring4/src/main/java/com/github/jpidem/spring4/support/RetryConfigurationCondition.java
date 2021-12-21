package com.github.jpidem.spring4.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 判断带有@Configuration注解的配置Class是否满足condition条件
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryConfigurationCondition implements ConfigurationCondition {

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        // 该条件不会阻止 @Configuration添加类，在评估条件时，所有@Configurations都将被解析。
        return ConfigurationPhase.REGISTER_BEAN;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> map = metadata.getAnnotationAttributes(RetryConditional.class.getName());
        Class<?> missingBeanType = (Class<?>) map.get("missingBeanType");
        if (!Void.class.equals(missingBeanType)) {
            // 判断容器中是否没有 missingBeanType实例
            try {
                return !hasBean(context.getBeanFactory(), missingBeanType);
            } catch (NoSuchBeanDefinitionException e) {
                return true;
            }
        }
        Class<?> hasBeanType = (Class<?>) map.get("hasBeanType");
        if (!Void.class.equals(hasBeanType)) {
            // 判断容器中是否有hasBeanType实例
            try {
                return hasBean(context.getBeanFactory(), hasBeanType);
            } catch (NoUniqueBeanDefinitionException e) {
                return true;
            } catch (NoSuchBeanDefinitionException e) {
                return false;
            }
        }
        throw new IllegalArgumentException("至少要有一个Condition条件");
    }

    private boolean hasBean(ConfigurableListableBeanFactory beanFactory, Class<?> clazz) {
        if (beanFactory == null) {
            return false;
        }
        if (!beanFactory.getBeansOfType(clazz).isEmpty()) {
            return true;
        }

        BeanFactory parent = beanFactory.getParentBeanFactory();
        if (parent instanceof ConfigurableListableBeanFactory) {
            return hasBean((ConfigurableListableBeanFactory) parent, clazz);
        }

        return false;
    }
}