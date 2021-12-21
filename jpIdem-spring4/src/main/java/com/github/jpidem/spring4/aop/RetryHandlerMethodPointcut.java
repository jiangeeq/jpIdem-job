package com.github.jpidem.spring4.aop;

import com.github.jpidem.core.util.RetryHandlerUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcher;

import java.lang.reflect.Method;

/**
 * 对所有方法上面有RetryFunction注解的进行连接点代理
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryHandlerMethodPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    /**
     * 静态方法匹配器的抽象超类，它不关心运行时的参数。
     *
     * @return @see org.springframework.aop.support.StaticMethodMatcher
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return new StaticMethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return RetryHandlerUtils.isRetryFunctionMethod(method);
            }
        };
    }
}
