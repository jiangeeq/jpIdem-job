package com.github.jpidem.core.impl;

import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.core.RetrySerializer;
import com.github.jpidem.core.RetryTask;
import com.github.jpidem.core.RetryTaskFactory;
import com.github.jpidem.core.util.ServiceLoaderUtils;

import java.time.LocalDateTime;

/**
 * 默认的RetryTask对象创建工厂
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class DefaultRetryTaskFactory implements RetryTaskFactory {
    /**
     * 序列化操作对象
     */
    private RetrySerializer retrySerializer;

    public DefaultRetryTaskFactory() {
        this(ServiceLoaderUtils.loadService(RetrySerializer.class));
    }

    public DefaultRetryTaskFactory(RetrySerializer retrySerializer) {
        this.retrySerializer = retrySerializer;
    }

    @Override
    public RetryTask create(RetryHandler<?, ?> retryHandler, Object params) {
        RetryTask task = new RetryTask();
        task.setIdentity(retryHandler.identity());
        if (params != null) {
            task.setParams(retrySerializer.serialize(params));
        }
        task.setStatus(RetryTask.STATUS_INIT);
        task.setRetryCount(0);
        task.setCreateDate(LocalDateTime.now());
        return task;
    }
}
