package com.github.jpidem.core.impl;

import com.github.jpidem.core.RetryHandler;
import com.github.jpidem.core.RetryProcessor;
import com.github.jpidem.core.RetrySerializer;
import com.github.jpidem.core.RetryTask;
import com.github.jpidem.core.RetryTaskMapper;
import com.github.jpidem.core.support.DefaultRetryHandler;
import com.github.jpidem.core.support.GenericRetryHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 默认的重试处理器
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@Slf4j
public class DefaultRetryProcessor implements RetryProcessor {
    /**
     * 重试执行器
     */
    private GenericRetryHandler genericRetryHandler;
    /**
     * 数据库表的操作对象
     */
    private RetryTaskMapper retryTaskMapper;
    /**
     * 重试执行器
     */
    private RetryedRetryHandler retryedRetryHandler;

    public DefaultRetryProcessor(RetryHandler<Object, Object> retryHandler, RetryTaskMapper retryTaskMapper, RetrySerializer retrySerializer) {
        if (retryHandler instanceof GenericRetryHandler) {
            this.genericRetryHandler = (GenericRetryHandler) retryHandler;
        } else {
            this.genericRetryHandler = new DefaultRetryHandler(retryHandler);
        }
        this.retryTaskMapper = retryTaskMapper;

        if (retrySerializer == null) {
            this.retryedRetryHandler = new RetryedRetryHandler(genericRetryHandler, retryTaskMapper);
        } else {
            this.retryedRetryHandler = new RetryedRetryHandler(genericRetryHandler, retryTaskMapper, retrySerializer);
        }
    }

    @Override
    public void doRetry() {
        log.info("开始执行Identity={}的重试，maxRetryCount={}, initialDelay={}", genericRetryHandler.identity(), genericRetryHandler.maxRetryCount(), genericRetryHandler.initialDelay());
        List<RetryTask> tasks = retryTaskMapper.queryNeedRetryTaskList(genericRetryHandler.identity(), genericRetryHandler.maxRetryCount(), genericRetryHandler.initialDelay());
        if (tasks == null) {
            return;
        }
        log.info("Identity={}当前有{}个任务准备重试", genericRetryHandler.identity(), tasks.size());
        if (genericRetryHandler.ignoreException()) {
            tasks.forEach(this::doRetryWithIgnoreException);
        } else {
            tasks.forEach(this::doRetry);
        }
    }

    private void doRetryWithIgnoreException(RetryTask retryTask) {
        log.info("开始重试Identity={}，TaskId={}的任务", retryTask.getIdentity(), retryTask.getTaskId());
        retryedRetryHandler.setRetryTask(retryTask);
        String json = retryTask.getParams();
        try {
            if (StringUtils.isBlank(json)) {
                retryedRetryHandler.handle(null);
            } else {
                retryedRetryHandler.parseArgsAndHandle(json);
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doRetry(RetryTask retryTask) {
        log.info("开始重试Identity={}，Id={}的任务", retryTask.getIdentity(), retryTask.getTaskId());
        retryedRetryHandler.setRetryTask(retryTask);
        String json = retryTask.getParams();
        if (StringUtils.isBlank(json)) {
            retryedRetryHandler.handle(null);
        } else {
            retryedRetryHandler.parseArgsAndHandle(json);
        }
    }
}
