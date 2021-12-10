package com.github.jpidem.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 重试任务的上下文信息
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@Setter
@Getter
public class RetryContext {

    private RetryHandler retryHandler;

    /**
     * 首次执行，值为0
     */
    private int retryCount;

    /**
     * 方法执行的参数
     */
    private Object args;

    /**
     * 方法执行的结果
     */
    private Object result;

    /**
     * 方法执行的异常
     */
    private Exception exception;

    public RetryContext(RetryHandler retryHandler, Object args) {
        this.retryHandler = retryHandler;
        this.args = args;
    }

    public RetryContext(RetryHandler retryHandler, Object args, int retryCount) {
        this.retryHandler = retryHandler;
        this.retryCount = retryCount;
        this.args = args;
    }
}
