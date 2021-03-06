package com.github.jpidem.core.impl;

import com.github.jpidem.core.NoRetryException;
import com.github.jpidem.core.RetryContext;
import com.github.jpidem.core.RetryTask;
import com.github.jpidem.core.RetryTaskFactory;
import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.core.support.GenericRetryHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 用于在一个重试任务首次执行时触发。执行任务时，先把参数序列化并保存到数据库
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@Slf4j
class ImmediatelyRetryHandler extends ExecuteRetryHandler {
    /**
     * RetryTask对象创建工厂
     */
    private RetryTaskFactory retryTaskFactory;
    /**
     * 之前的任务
     */
    private boolean beforeTask;

    public ImmediatelyRetryHandler(GenericRetryHandler genericRetryHandler, RetryTaskFactory retryTaskFactory, RetryTaskMapper retryTaskMapper, boolean beforeTask) {
        super(genericRetryHandler, retryTaskMapper);
        this.retryTaskFactory = retryTaskFactory;
        this.beforeTask = beforeTask;
    }

    @Override
    public Object handle(Object arg) {
        RetryContext retryContext = new RetryContext(genericRetryHandler, arg);
        Object result;
        RetryTask retryTask = retryTaskFactory.create(genericRetryHandler, arg);

        if (beforeTask) {
            retryTaskMapper.insert(retryTask);
            try {
                onBefore(retryContext);
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

                if (retryContext.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                    //只有最大可重试次数为0，才会执行到这里
                    failureTask(retryTask, retryContext);

                    onRetry(retryContext);
                    onError(retryContext);
                } else {
                    updateRemark(retryTask, e);
                    onRetry(retryContext);
                }

                throw e;
            }
            return result;
        } else {
            try {
                onBefore(retryContext);
                result = genericRetryHandler.handle(arg);
                retryContext.setResult(result);
                onRetry(retryContext);
                onComplete(retryContext);
            } catch (NoRetryException e) {
                retryContext.setException(e);

                onRetry(retryContext);
                onError(retryContext);

                throw e;
            } catch (RuntimeException e) {
                retryContext.setException(e);
                retryTask.setRemark(StringUtils.left(e.getMessage(), 1000));
                retryTaskMapper.insert(retryTask);

                if (retryContext.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                    //达到最大重试次数
                    onRetry(retryContext);
                    onError(retryContext);
                } else {
                    //继续重试
                    onRetry(retryContext);
                }

                throw e;
            }
        }
        return result;
    }
}
