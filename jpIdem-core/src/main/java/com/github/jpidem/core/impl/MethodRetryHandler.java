package com.github.jpidem.core.impl;

import com.github.jpidem.core.RetryFunction;
import com.github.jpidem.core.listener.RetryListener;
import com.github.jpidem.core.support.GenericRetryHandler;
import com.github.jpidem.core.util.RetryHandlerUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * 方法级别的重试执行器，用于方法上有 @RetryFunction 的操作重试执行
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @see RetryFunction
 */
public class MethodRetryHandler implements GenericRetryHandler {
    /**
     * 目标对象
     */
    private Object targetObject;
    /**
     * 目标方法
     */
    private Method method;
    /**
     * RetryFunction注解对象
     */
    private RetryFunction annRetryFun;
    /**
     * 重试监听器
     */
    private Supplier<RetryListener> retryListenerSupplier;

    public MethodRetryHandler(Object targetObject, Method method, RetryFunction annRetryFun, Supplier<RetryListener> retryListenerSupplier) {
        this.targetObject = targetObject;
        this.method = method;
        this.annRetryFun = annRetryFun;
        this.retryListenerSupplier = retryListenerSupplier;
    }

    @Override
    public String name() {
        return annRetryFun.name();
    }

    @Override
    public String identity() {
        String identity = annRetryFun.identity();
        // RetryFunction.identity如果为空，则使用拼接方法类名+方法名组成identity
        return StringUtils.isBlank(identity) ? RetryHandlerUtils.getMethodIdentity(method) : identity;
    }

    @Override
    public Object handle(Object arg) {
        try {
            return method.getParameterCount() > 0 ? method.invoke(targetObject, arg) : method.invoke(targetObject);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getCause().getMessage(), e.getCause());
        }
    }

    @Override
    public RetryListener retryListener() {
        return retryListenerSupplier.get();
    }

    @Override
    public String cron() {
        return annRetryFun.cron();
    }

    @Override
    public int interval() {
        return annRetryFun.interval();
    }

    @Override
    public int maxRetryCount() {
        return annRetryFun.maxRetryCount();
    }

    @Override
    public int initialDelay() {
        return annRetryFun.initialDelay();
    }

    @Override
    public boolean ignoreException() {
        return annRetryFun.ignoreException();
    }

    @Override
    public boolean autoStartup() {
        return annRetryFun.autoStartup();
    }

    @Override
    public Class<?> getInputArgsType() {
        // todo 方法目前只支持一个参数对象，后期优化为多个
        return method.getParameterCount() == 1 ? method.getParameterTypes()[0] : null;
    }
}