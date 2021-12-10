package com.github.jpidem.core;

/**
 * 重试执行器的后置处理器（用作增强）
 * @see RetryHandler
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@FunctionalInterface
public interface RetryHandlerPostProcessor<T, R> {

    RetryHandler<T, R> doPost(RetryHandler<T, R> retryHandler);
}