package com.github.jpidem.spring4;

import com.github.jpidem.core.*;
import com.github.jpidem.core.impl.DefaultRetryHandlerPostProcessor;
import com.github.jpidem.core.impl.DefaultRetryProcessor;
import com.github.jpidem.core.impl.DefaultRetryTaskFactory;
import com.github.jpidem.core.impl.MethodRetryHandler;
import com.github.jpidem.core.listener.RetryListener;
import com.github.jpidem.core.util.RetryHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 对所有com.github.jpidem.core.RetryHandler和带有@RetryFunction注解的方法进行注册
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @see SmartInitializingSingleton bean实例化完成(包括依赖注入完成,BeadPostProcess,InitializingBean,initMethod等等全部完成)后执行;可以理解为bean的收尾操作;
 * @see BeanPostProcessor bean初始化方法前后要添加一些自己逻辑处理
 */
@Slf4j
public class RetryAnnotationBeanPostProcessor implements BeanPostProcessor, SmartInitializingSingleton, EnvironmentAware, BeanFactoryAware {

    private DefaultListableBeanFactory defaultListableBeanFactory;

    private Environment environment;

    /**
     * 同一个类，如果在Spring容器中有多个bean，则会PostProcessor多次，这个Set就是为了防止多次注册的
     */
    private Set<Class<?>> postedClasseCache = new HashSet<>();

    private List<RetryHandler> retryHandlers = new ArrayList<>();

    private RetryRegistry retryRegistry;

    private RetrySerializer retrySerializer;

    private RetryTaskMapper retryTaskMapper;

    private RetryHandlerPostProcessor<Object, Object> retryHandlerPostProcessor;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean) {
            // 忽略AOP基础类 或例如限定范围的代理
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.postedClasseCache.contains(targetClass)) {
            Object targetObject = AopProxyUtils.getSingletonTarget(bean);
            if (RetryHandler.class.isAssignableFrom(targetClass)) {
                RetryHandlerUtils.validateRetryHandler(targetClass);
                log.info("发现RetryHandler的实例：{}，准备注册", targetClass);
                retryHandlers.add((RetryHandler) targetObject);
                return bean;
            }
            ReflectionUtils.MethodFilter methodFilter = method -> method.getAnnotation(RetryFunction.class) != null;
            Set<Method> methods = MethodIntrospector.selectMethods(targetClass, methodFilter);
            methods.forEach(method -> processRetryFunction(targetObject, method));

            postedClasseCache.add(targetClass);
        }
        return bean;
    }

    protected void processRetryFunction(Object bean, Method method) {
        log.info("发现@RetryFunction的实例：{}，准备注册", method.toString());
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
        RetryHandlerUtils.validateRetryFunction(method);

        RetryFunction retryFunction = method.getAnnotation(RetryFunction.class);
        Supplier<RetryListener> retryListenerSupplier = () -> {
            RetryListener retryListener = null;
            String retryListenerName = retryFunction.retryListener();
            if (StringUtils.isNotBlank(retryListenerName)) {
                retryListener = defaultListableBeanFactory.getBean(retryListenerName, RetryListener.class);
            }
            return retryListener;
        };
        retryHandlers.add(new MethodRetryHandler(bean, invocableMethod, retryFunction, retryListenerSupplier));
    }

    private RetrySerializer getRetrySerializerFromBeanFactory(BeanFactory beanFactory) {
        try {
            return beanFactory.getBean(RetrySerializer.class);
        } catch (NoUniqueBeanDefinitionException e) {
            throw e;
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        postedClasseCache.clear();

        this.retryTaskMapper = defaultListableBeanFactory.getBean(RetryTaskMapper.class);
        this.retryRegistry = defaultListableBeanFactory.getBean(RetryRegistry.class);

        boolean beforeTask = environment.getProperty(EnvironmentConstants.RETRY_BEFORETASK, Boolean.class, Boolean.TRUE);
        this.retrySerializer = getRetrySerializerFromBeanFactory(defaultListableBeanFactory);
        if (this.retrySerializer == null) {
            this.retryHandlerPostProcessor = new DefaultRetryHandlerPostProcessor(retryTaskMapper, beforeTask);
        } else {
            this.retryHandlerPostProcessor = new DefaultRetryHandlerPostProcessor(new DefaultRetryTaskFactory(retrySerializer), retryTaskMapper, beforeTask);
        }

        retryHandlers.forEach(this::registerJobBean);

        retryHandlers.clear();
    }

    protected void registerJobBean(RetryHandler retryHandler) {
        if (retryHandler.identity().length() > 50) {
            throw new IllegalArgumentException("identity=" + retryHandler.identity() + " is too long, it must be less than 50");
        }

        RetryHandler retryHandlerProxy = retryHandlerPostProcessor.doPost(retryHandler);
        RetryHandlerRegistration.registry(retryHandlerProxy);

        RetryProcessor retryProcessor = new DefaultRetryProcessor(retryHandler, retryTaskMapper, retrySerializer);

        retryRegistry.register(retryHandler, retryProcessor);
    }
}