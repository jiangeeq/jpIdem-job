package com.github.jpidem.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打上 RetryFunction 注解的方法，会被系统代理，一旦方法执行报错，将会定时重试
 * <p>
 * 打上 RetryFunction 注解的方法，需要注意
 * 1：方法必须要有参数，且只能有一个参数
 * 2：方法的参数不能是Object等无法被JSON序列化和反序列化的类型
 * 3：方法的参数不能是Collection，Map等带泛型的类型
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @date 2021/12/11 12:41 上午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface RetryFunction {

    /**
     * 任务名称
     */
    String name() default "";

    /**
     * 唯一标识，不能重复
     * 默认为类的全名称+方法名称
     */
    String identity() default "";

    /**
     * 重试cron表达式，为空则使用interval()进行重试
     */
    String cron() default "";

    /**
     * 任务监听器。可以在任务重试、任务完成、任务失败时进行回调
     * <p>
     * 如果是spring环境，则这里是 {@link com.github.jpidem.core.listener.RetryListener} 的bean name
     * 如果是非spring环境，则这里是 {@link com.github.jpidem.core.listener.RetryListener} 类的全限定名
     */
    String retryListener() default "";

    /**
     * 重试的时候，是否忽略错误继续执行
     * 当重试任务有多个的时候，上一个重试报错，是否忽略错误继续执行下一个任务
     */
    boolean ignoreException() default RetryHandler.DEFAULT_IGNORE_EXCEPTION;

    /**
     * 重试间隔时长。单位：秒
     */
    int interval() default RetryHandler.DEFAULT_RETRY_INTERVAL;

    /**
     * 最多重试次数
     */
    int maxRetryCount() default RetryHandler.DEFAULT_RETRY_MAX_COUNT;

    /**
     * 延迟时长。单位：秒
     * 任务失败之后，多久开始重试
     */
    int initialDelay() default RetryHandler.DEFAULT_INITIAL_DELAY;

    /**
     * 定时重试是否自动启动
     */
    boolean autoStartup() default RetryHandler.DEFAULT_AUTO_STARTUP;
}
