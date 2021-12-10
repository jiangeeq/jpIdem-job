package com.github.jpidem.core;

import com.github.jpidem.core.listener.RetryListener;

/**
 * 重试执行器，要重试的Job可以实现该接口来实际执行处理
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@FunctionalInterface
public interface RetryHandler<T, R> {

    /**
     * 默认重试间隔时长5分钟
     */
    int DEFAULT_RETRY_INTERVAL = 300;

    /**
     * 默认最多重试次数5次
     */
    int DEFAULT_RETRY_MAX_COUNT = 5;

    /**
     * 默认延迟5分钟
     */
    int DEFAULT_INITIAL_DELAY = 300;

    /**
     * 忽略错误继续执行下一个任务
     */
    boolean DEFAULT_IGNORE_EXCEPTION = true;

    /**
     * 定时重试自动启动
     */
    boolean DEFAULT_AUTO_STARTUP = true;

    /**
     * 任务名称
     *
     * @return 自己定义的任务名
     */
    default String name() {
        return null;
    }

    /**
     * 唯一标识，不能重复
     *
     * @return 实现了该类的类名
     */
    default String identity() {
        return this.getClass().getName();
    }

    /**
     * 任务处理
     *
     * @param arg 参数，参数类型需要满足如下条件
     *            方法的参数不能是Object等无法被JSON序列化和反序列化的类型、方法的参数不能是Collection，Map等带泛型的类型
     * @return 期望的结果类型
     */
    R handle(T arg);

    /**
     * 任务监听器。可以在任务重试、任务完成、任务失败时进行回调
     *
     * @return 任务监听器实例
     */
    default RetryListener retryListener() {
        return null;
    }

    /**
     * 重试cron表达式，为空则使用interval()进行重试
     *
     * @return cron表达式
     */
    default String cron() {
        return null;
    }

    /**
     * 重试间隔时长。单位：秒
     *
     * @retur 时长：秒
     */
    default int interval() {
        return DEFAULT_RETRY_INTERVAL;
    }

    /**
     * 最多重试次数
     *
     * @return 最大重试数
     */
    default int maxRetryCount() {
        return DEFAULT_RETRY_MAX_COUNT;
    }

    /**
     * 延迟时长。单位：秒
     * 任务失败之后，多久开始重试
     *
     * @return 初始重试延迟时长
     */
    default int initialDelay() {
        return DEFAULT_INITIAL_DELAY;
    }

    /**
     * 重试的时候，是否忽略错误继续执行
     *
     * @return 是否
     */
    default boolean ignoreException() {
        return DEFAULT_IGNORE_EXCEPTION;
    }

    /**
     * 定时重试是否自动启动
     *
     * @return 是否
     */
    default boolean autoStartup() {
        return DEFAULT_AUTO_STARTUP;
    }
}