package com.github.jpidem.spring4.registry.quartz;

import com.github.jpidem.core.RetryProcessor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 这些类表示要执行的“作业”。Job的实例必须有一个公共的无参数构造函数。
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryJob implements Job {

    private RetryProcessor retryProcessor;

    public RetryJob() {
    }

    public RetryJob(RetryProcessor retryProcessor) {
        this.retryProcessor = retryProcessor;
    }

    /**
     * 当与作业关联的触发器触发时，由execute调用。
     * 可以在这个方法退出之前在JobExecutionContext上设置一个结果对象。结果本身对于Quartz是没有意义的，但是对于监视任务执行的JobListeners或TriggerListeners可能是有需要的。
     *
     * @param context
     * @return void
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    @Override
    public void execute(JobExecutionContext context) {
        retryProcessor.doRetry();
    }
}
