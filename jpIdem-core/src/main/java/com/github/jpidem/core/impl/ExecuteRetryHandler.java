package com.github.jpidem.core.impl;

import com.github.jpidem.core.RetryContext;
import com.github.jpidem.core.RetryTask;
import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.core.listener.QuietRetryListener;
import com.github.jpidem.core.listener.RetryListener;
import com.github.jpidem.core.support.GenericRetryHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * 重试执行器执行过程中的一些基本行为，大多数方法的通用逻辑实现都在这里
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
abstract class ExecuteRetryHandler implements GenericRetryHandler {
    /**
     * 重试执行器
     */
    protected GenericRetryHandler genericRetryHandler;
    /**
     * 数据库的抽象操作方法
     */
    protected RetryTaskMapper retryTaskMapper;
    /**
     * 重试监听器
     */
    private RetryListener delegateRetryListener;

    private Class<?> inputArgsType;

    public ExecuteRetryHandler(GenericRetryHandler genericRetryHandler, RetryTaskMapper retryTaskMapper) {
        this.genericRetryHandler = genericRetryHandler;
        this.retryTaskMapper = retryTaskMapper;
        this.inputArgsType = genericRetryHandler.getInputArgsType();
    }

    @Override
    public String name() {
        return genericRetryHandler.name();
    }

    @Override
    public String identity() {
        return genericRetryHandler.identity();
    }

    @Override
    public String cron() {
        return genericRetryHandler.cron();
    }

    @Override
    public int interval() {
        return genericRetryHandler.interval();
    }

    @Override
    public int maxRetryCount() {
        return genericRetryHandler.maxRetryCount();
    }

    public Class<?> getInputArgsType() {
        return inputArgsType;
    }

    protected synchronized RetryListener getRetryListener() {
        if (delegateRetryListener == null) {
            // 自己的delegateRetryListener未设置则使用重试执行器中的listener
            RetryListener retryListener = genericRetryHandler.retryListener();
            if (retryListener == null) {
                // 如果都没有就new一个什么都不做的listener
                delegateRetryListener = new RetryListener() {
                };
            } else {
                delegateRetryListener = new QuietRetryListener(retryListener);
            }
        }
        return delegateRetryListener;
    }

    protected void onBefore(RetryContext retryContext) {
        getRetryListener().onBefore(retryContext);
    }

    protected void onRetry(RetryContext retryContext) {
        getRetryListener().onRetry(retryContext);
    }

    protected void onError(RetryContext retryContext) {
        getRetryListener().onError(retryContext);
    }

    protected void onComplete(RetryContext retryContext) {
        getRetryListener().onComplete(retryContext);
    }

    protected int completeTask(RetryTask retryTask) {
        retryTask.setStatus(RetryTask.STATUS_SUCCESS);
        return retryTaskMapper.update(retryTask);
    }

    protected int failureTask(RetryTask retryTask, RetryContext retryContext) {
        retryTask.setStatus(RetryTask.STATUS_EXCEPTION);
        retryTask.setRemark(StringUtils.left(retryContext.getException().getMessage(), 1000));
        return retryTaskMapper.update(retryTask);
    }

    protected int update(RetryTask retryTask, RetryContext retryContext) {
        retryTask.setRetryCount(retryContext.getRetryCount());
        if (retryContext.getException() != null) {
            retryTask.setRemark(StringUtils.left(retryContext.getException().getMessage(), 1000));
        }
        return retryTaskMapper.update(retryTask);
    }

    protected int updateRemark(RetryTask retryTask, Throwable e) {
        retryTask.setRemark(StringUtils.left(e.getMessage(), 1000));
        return retryTaskMapper.update(retryTask);
    }
}
