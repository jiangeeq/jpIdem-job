package com.github.jpidem.spring4;

import com.github.jpidem.spring4.autoconfigure.RetryImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启JpIdem Job的注解启动器
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @see RetryImportSelector
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RetryImportSelector.class)
public @interface EnableRetrying {

    /**
     * 指示是否创建基于子类(CGLIB)的代理(IOC)到标准的Java接口代理
     */
    boolean proxyTargetClass() default false;

    /**
     * 指示该代理应该被AOP框架作为{@code ThreadLocal}公开。
     * 通过{@link org.springframework.aop.framework.AopContext}类来检索。
     * 默认情况下是关闭的，即不能保证{@link org.springframework.aop.framework.AopContext}访问是有效的。
     *
     * @since 1.3.6
     */
    boolean exposeProxy() default false;

    /**
     * 默认情况下是为了在所有其他的后处理器之后运行，这样它就可以向现有的代理添加一个建议器，而不是双代理。
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}