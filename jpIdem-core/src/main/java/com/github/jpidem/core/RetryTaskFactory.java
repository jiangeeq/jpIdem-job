package com.github.jpidem.core;

/**
 * RetryTask对象工程
 * 通过RetryHandler的相关实现类，来创建RetryTask对象实例
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@FunctionalInterface
public interface RetryTaskFactory {
    /**
     * @param retryHandler 重试执行器
     * @param params       参数内容
     * @return RetryTask 重试任务对象实例
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    RetryTask create(RetryHandler<?, ?> retryHandler, Object params);
}
