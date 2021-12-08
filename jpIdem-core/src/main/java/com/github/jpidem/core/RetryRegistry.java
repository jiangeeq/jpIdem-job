package com.github.jpidem.core;

/**
 * RetryHandler 注册器
 */
@FunctionalInterface
public interface RetryRegistry {

    void register(RetryHandler retryHandler, RetryProcessor retryProcessor);
}