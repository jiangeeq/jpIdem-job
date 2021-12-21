package com.github.jpidem.spring4.aop;

import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.core.util.RetryHandlerUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcher;

import java.lang.reflect.Method;

/**
 * 对所有RetryHandler接口的实例进行 AOP Joinpoint 连接点扫描
 * <p>
 * Pointcut接口为SpringAop中对AOP的最顶层抽象，主要负责对系统的相应的RetryHandler接口的实例进行捕捉，
 * 并且提供了一个TruePointcut实例，当Pointcut为TruePointcut类型时，则会忽略所有的匹配条件，对系统中所有的对象进行Joinpoint所定义的规则进行匹配
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryHandlerClassPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return RetryHandler.class::isAssignableFrom;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new StaticMethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return RetryHandlerUtils.isRetryHandlerMethod(targetClass, method);
            }
        };
    }
}
