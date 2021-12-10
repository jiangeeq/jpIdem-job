package com.github.jpidem.core;

/**
 * 抛出此异常，系统不会进行重试。直接当失败处理
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class NoRetryException extends RuntimeException {

    public NoRetryException(String message) {
        super(message);
    }

    public NoRetryException(String message, Throwable cause) {
        super(message, cause);
    }
}