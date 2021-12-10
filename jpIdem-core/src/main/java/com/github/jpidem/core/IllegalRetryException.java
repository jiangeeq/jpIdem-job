package com.github.jpidem.core;

/**
 * 非法重试的调用异常
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class IllegalRetryException extends RuntimeException {

    public IllegalRetryException(String message) {
        super(message);
    }
}