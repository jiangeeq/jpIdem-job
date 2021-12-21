package com.github.jpidem.spring4;

import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.core.RetryHandlerPostProcessor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RetryHandler 的注册表
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryHandlerRegistration {

    private static final Map<String, RetryHandler> retryHandlerMap = new ConcurrentHashMap<>();

    /**
     * 注册 RetryHandler
     *
     * @param retryHandler 重试执行器
     * @return void
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static void registry(RetryHandler retryHandler) {
        if (retryHandlerMap.containsKey(retryHandler.identity())) {
            throw new IllegalArgumentException("RetryHandler identity=" + retryHandler.identity() + " already exists");
        }

        retryHandlerMap.put(retryHandler.identity(), retryHandler);
    }

    /**
     * 注册RetryHandlerPostProcessor 包装的RetryHandler
     *
     * @param retryHandler 重试执行器
     * @param processor    重试执行器的后置处理器
     * @return void
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static void registry(RetryHandler retryHandler, RetryHandlerPostProcessor<Object, Object> processor) {
        if (retryHandlerMap.containsKey(retryHandler.identity())) {
            throw new IllegalArgumentException("RetryHandler identity=" + retryHandler.identity() + " already exists");
        }

        retryHandlerMap.put(retryHandler.identity(), processor.doPost(retryHandler));
    }
    /**
     * 根据任务表示获取对应的重试执行器
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     * @param identity 任务的唯一标识
     * @return Optional<RetryHandler>
     */
    public static Optional<RetryHandler> get(String identity) {
        return Optional.ofNullable(retryHandlerMap.get(identity));
    }
}