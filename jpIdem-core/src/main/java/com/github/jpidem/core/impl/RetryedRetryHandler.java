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
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@Slf4j
class RetryedRetryHandler extends ExecuteRetryHandler {
    /**
     * 对象序列化器
     */
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

    /**
     * 解析入参内容并执行 handle
     *
     * @param json 参数来自于数据库保存的
     * @return Object 结果
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public Object parseArgsAndHandle(String json) {
        return handle(retrySerializer.deserialize(json, getInputArgsType()));
    }

    @Override
    public Object handle(Object arg) {
        // 重试次数+1
        retryTask.setRetryCount(retryTask.getRetryCount() + 1);
        RetryContext retryContext = new RetryContext(genericRetryHandler, arg, retryTask.getRetryCount());
        Object result;
        try {
            onBefore(retryContext);
            // 重试后方法执行的结果
            result = genericRetryHandler.handle(arg);
            retryContext.setResult(result);
            // 修改数据库任务信息为成功
            completeTask(retryTask);
            // 重试执行完后的其他操作
            onRetry(retryContext);
            // 任务完成时的操作
            onComplete(retryContext);
        } catch (NoRetryException e) {
            retryContext.setException(e);
            // 修改数据库任务信息为失败
            failureTask(retryTask, retryContext);
            // 重试执行完后的其他操作
            onRetry(retryContext);
            // 重试失败时操作
            onError(retryContext);
            throw e;
        } catch (RuntimeException e) {
            // 任务运行时发生异常
            retryContext.setException(e);
            if (retryTask.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                // 1.数据库中该任务已达最大重试次数
                failureTask(retryTask, retryContext);
            } else {
                // 2.更新该任务数据：重试次数+1并记录错误信息
                update(retryTask, retryContext);
            }
            // 重试执行完后的其他操作
            onRetry(retryContext);
            //重试次数达到最大，触发失败回调
            if (retryContext.getRetryCount() == genericRetryHandler.maxRetryCount()) {
                onError(retryContext);
            }

            throw e;
        }
        return result;
    }
}
