package com.github.jpidem.spring4.aop;

import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.spring4.RetryHandlerRegistration;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;

/**
 * spring aop 拦截目标类的方法
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryHandlerClassInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) {
        RetryHandler retryHandler = (RetryHandler) invocation.getThis();
        Object[] args = invocation.getArguments();
        return RetryHandlerRegistration.get(retryHandler.identity()).map(rh -> rh.handle(ArrayUtils.isEmpty(args) ? null : args[0])).orElse(null);
    }
}