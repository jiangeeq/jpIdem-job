package com.github.jpidem.core;

/**
 * 重试注册器，用于注册RetryHandler相关类实例
 */
@FunctionalInterface
public interface RetryRegistry {

    void register(RetryHandler retryHandler, RetryProcessor retryProcessor);
}