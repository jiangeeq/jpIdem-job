package com.github.jpidem.spring4.registry.quartz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * JobFactory负责生成Job类的实例。
 * 对于那些希望自己的应用程序通过某些特殊机制生成Job实例(比如提供依赖注入的机会)的人来说，这个接口可能有用。
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryJobFactory implements JobFactory {

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        return (Job) bundle.getJobDetail().getJobDataMap().get(JobConstant.JOB_INSTANCE_KEY);
    }
}