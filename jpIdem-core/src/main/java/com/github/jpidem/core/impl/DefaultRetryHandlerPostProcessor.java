package com.github.jpidem.core.impl;

import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.core.RetryHandlerPostProcessor;
import com.github.jpidem.core.RetryTaskFactory;
import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.core.support.DefaultRetryHandler;
import com.github.jpidem.core.support.GenericRetryHandler;

/**
 * 重试执行器的后置处理器
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class DefaultRetryHandlerPostProcessor implements RetryHandlerPostProcessor<Object, Object> {
    /**
     * RetryTask对象创建工厂
     */
    private RetryTaskFactory retryTaskFactory;
    /**
     * 数据库的抽象操作方法
     */
    private RetryTaskMapper retryTaskMapper;
    /**
     * 之前的任务
     */
    private boolean beforeTask;

    public DefaultRetryHandlerPostProcessor(RetryTaskMapper retryTaskMapper, boolean beforeTask) {
        this(new DefaultRetryTaskFactory(), retryTaskMapper, beforeTask);
    }

    public DefaultRetryHandlerPostProcessor(RetryTaskFactory retryTaskFactory, RetryTaskMapper retryTaskMapper, boolean beforeTask) {
        this.retryTaskFactory = retryTaskFactory;
        this.retryTaskMapper = retryTaskMapper;
        this.beforeTask = beforeTask;
    }

    @Override
    public RetryHandler<Object, Object> doPost(RetryHandler<Object, Object> retryHandler) {
        if (retryHandler instanceof GenericRetryHandler) {
            return new ImmediatelyRetryHandler((GenericRetryHandler) retryHandler, retryTaskFactory, retryTaskMapper, beforeTask);
        }
        return new ImmediatelyRetryHandler(new DefaultRetryHandler(retryHandler), retryTaskFactory, retryTaskMapper, beforeTask);
    }
}
