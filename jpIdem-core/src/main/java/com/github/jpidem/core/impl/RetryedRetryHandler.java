package com.github.jpidem.core.impl;

import com.github.jpidem.core.NoRetryException;
import com.github.jpidem.core.RetryContext;
import com.github.jpidem.core.RetrySerializer;
import com.github.jpidem.core.RetryTask;
import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.core.support.GenericRetryHandler;
import com.github.jpidem.core.util.ServiceLoaderUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 该handle方法会在异步重试的时候被触发，handle方法的参数来自于数据库保存的
 *
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
class RetryedRetryHandler extends ExecuteRetryHandler {

    private RetrySerializer retrySerializer;

    @Setter
    private RetryTask retryTask;

    public RetryedRetryHandler(GenericRetryHandler genericRetryHandler, RetryTaskMapper retryTaskMapper) {
        this(genericRetryHandler, retryTaskMapper, ServiceLoaderUtils.loadService(RetrySerializer.class));
    }

    public RetryedRetryHandler(GenericRetryHandler genericRetryHandler, RetryTaskMapper retryTaskMapper, RetrySerializer retrySerializer) {
        super(genericRetryHandler, retryTaskMapper);
        this.retrySerializer = retrySerializer;
    }

    public Object parseArgsAndhandle(String json) {
        return handle(retrySerializer.deserialize(json, getInputArgsType()));
    }

    @Override
    public Object handle(Object arg) {
        retryTask.setRetryCount(retryTask.getRetryCount() + 1);
        RetryContext retryContext = new RetryContext(genericRetryHandler, arg, retryTask.getRetryCount());
        Object result;
        try {
            result = genericRetryHandler.handle(arg);
            retryContext.setResult(result);
            completeTask(retryTask);
            onRetry(retryContext);
            onComplete(retryContext);
        } catch (NoRetryException e) {
            retryContext.setException(e);

            failureTask(retryTask, retryContext);
            onRetry(retryContext);
            onError(retryContext);
            throw e;
        } catch (RuntimeException e) {
            retryContext.setException(e);

            if (retryTask.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                failureTask(retryTask, retryContext);
            } else {
                update(retryTask, retryContext);
            }

            onRetry(retryContext);

            if (retryContext.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                //重试次数达到最大，触发失败回调
                onError(retryContext);
            }

            throw e;
        }

        return result;
    }
}
