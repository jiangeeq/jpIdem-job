package com.github.jpidem.core.support;

import com.github.jpidem.core.RetryHandler;

/**
 * @author yuni[mn960mn@163.com]
 */
public interface GenericRetryHandler extends RetryHandler {

    /**
     * 获取handle方法请求参数的泛型类型
     *
     * @return
     */
    Class<?> getInputArgsType();
}
