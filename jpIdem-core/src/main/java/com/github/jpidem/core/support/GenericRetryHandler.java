package com.github.jpidem.core.support;

import com.github.jpidem.core.RetryHandler;

/**
 * 通用的重试执行器
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public interface GenericRetryHandler extends RetryHandler {

    /**
     * 获取 handle 方法请求参数的泛型类型
     *
     * @return Class
     */
    Class<?> getInputArgsType();
}
