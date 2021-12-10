package com.github.jpidem.core;

import com.github.jpidem.core.impl.DefaultRetryTaskFactory;

/**
 * 用于序列化、反序列化handle的请求参数
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @see DefaultRetryTaskFactory
 */
public interface RetrySerializer {

    /**
     * 序列化
     *
     * @param object
     * @return
     */
    String serialize(Object object);

    /**
     * 反序列化
     *
     * @param value
     * @param clazz
     * @return
     */
    Object deserialize(String value, Class<?> clazz);
}
