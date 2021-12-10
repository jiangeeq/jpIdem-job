package com.github.jpidem.core.support;

import com.github.jpidem.core.IllegalRetryException;
import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.core.listener.RetryListener;
import com.github.jpidem.core.util.RetryHandlerUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * 默认的重试执行器实现
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class DefaultRetryHandler implements GenericRetryHandler {
    /**
     * 重试执行器RetryHandler的实现对象实例
     */
    private RetryHandler delegate;
    /**
     * handle方法请求参数的泛型类型
     */
    private Class<?> inputArgsType;

    public DefaultRetryHandler(RetryHandler delegate) {
        this.delegate = delegate;
        this.inputArgsType = parseInputArgsType();
    }

    /**
     * 解析实例对象delegate中接口的Type类型
     *
     * @param
     * @return Class<?>
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    protected Class<?> parseInputArgsType() {
        Class<?> current = delegate.getClass();
        Type interfaceType = RetryHandlerUtils.getRetryHandlerGenericInterface(current);
        if (interfaceType != null && interfaceType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
            if (RetryHandler.class.equals(parameterizedType.getRawType())) {
                Type targetType = parameterizedType.getActualTypeArguments()[0];
                if (targetType instanceof Class) {
                    Class<?> targetClazz = (Class<?>) targetType;
                    if (Object.class.equals(targetClazz)) {
                        throw new IllegalRetryException(delegate.getClass().getName() + ".handle方法输入参数的泛型类型不能是Object等无法序列化和反序列化的类型");
                    } else if (Collection.class.isAssignableFrom(targetClazz) || Map.class.isAssignableFrom(targetClazz)) {
                        throw new IllegalRetryException("重试方法的参数类型[" + targetClazz + "]不能是集合类等带泛型的");
                    }
                    return targetClazz;
                }
                throw new IllegalRetryException("重试方法的参数类型不能是集合类等带泛型的");
            }
        }
        throw new IllegalRetryException("无法获取到" + delegate.getClass().getName() + ".handle方法输入参数的泛型类型");
    }

    @Override
    public Class<?> getInputArgsType() {
        return this.inputArgsType;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public String identity() {
        return delegate.identity();
    }

    @Override
    public Object handle(Object arg) {
        return delegate.handle(arg);
    }

    @Override
    public RetryListener retryListener() {
        return delegate.retryListener();
    }

    @Override
    public String cron() {
        return delegate.cron();
    }

    @Override
    public int interval() {
        return delegate.interval();
    }

    @Override
    public int maxRetryCount() {
        return delegate.maxRetryCount();
    }

    @Override
    public int initialDelay() {
        return delegate.initialDelay();
    }

    @Override
    public boolean ignoreException() {
        return delegate.ignoreException();
    }

    @Override
    public boolean autoStartup() {
        return delegate.autoStartup();
    }
}
