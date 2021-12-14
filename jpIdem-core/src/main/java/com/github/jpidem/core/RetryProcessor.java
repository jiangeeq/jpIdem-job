package com.github.jpidem.core;

/**
 * 重试处理器
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public interface RetryProcessor {

    /**
     * 执行定时重试
     */
    void doRetry();
}
