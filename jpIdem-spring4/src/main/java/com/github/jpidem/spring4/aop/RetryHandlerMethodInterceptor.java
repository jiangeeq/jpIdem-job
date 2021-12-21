package com.github.jpidem.spring4.aop;

import com.github.jpidem.core.RetryFunction;
import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.spring4.RetryHandlerRegistration;
import com.github.jpidem.core.util.RetryHandlerUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * spring aop 拦截目标注解的方法
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryHandlerMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) {
        RetryFunction retryFunction = invocation.getMethod().getAnnotation(RetryFunction.class);
        Object[] args = invocation.getArguments();
        String identity = retryFunction.identity();
        if (StringUtils.isBlank(identity)) {
            identity = RetryHandlerUtils.getMethodIdentity(invocation.getMethod());
        }
        // 从登记表中获取对应的RetryHandler实例
        Optional<RetryHandler> optional = RetryHandlerRegistration.get(identity);
        if (optional.isPresent()) {
            return optional.get().handle(ArrayUtils.isEmpty(args) ? null : args[0]);
        }
        throw new IllegalArgumentException("找不到对应的RetryHandler代理，identity=" + identity);
    }
}