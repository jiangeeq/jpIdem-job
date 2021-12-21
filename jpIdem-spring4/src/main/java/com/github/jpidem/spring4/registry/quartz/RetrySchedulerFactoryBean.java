package com.github.jpidem.spring4.registry.quartz;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @see SchedulerFactoryBean 是Quartz框架最重要的一环，它主导着定时任务的初始化与执行顺序
 */
@Setter
@Getter
@Slf4j
public class RetrySchedulerFactoryBean extends SchedulerFactoryBean {

    protected String jobName;

    protected String jobIdentity;

    protected String jobPeriod;

    protected String jobGroup;

    protected JobStatusEnum jobStatusEnum = JobStatusEnum.INIT;

    protected Trigger trigger;

    @Override
    public void setTriggers(Trigger... triggers) {
        super.setTriggers(triggers);
        this.trigger = triggers[0];
    }

    @Override
    public void stop() throws SchedulingException {
        super.stop();
        jobStatusEnum = JobStatusEnum.STOPED;
    }

    /**
     * 立即启动
     */
    public void startNow() {
        try {
            this.getObject().start();
        } catch (SchedulerException e) {
            throw new SchedulingException("Could not start Quartz Scheduler", e);
        }
        jobStatusEnum = JobStatusEnum.RUNNING;
    }

    /**
     * 立即执行
     */
    public void runAsync() {
        new Thread(() -> {
            try {
                this.getObject().triggerJob(trigger.getJobKey());
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                throw new SchedulingException("Could not execute Quartz Scheduler", e);
            }
        }).start();
    }

    @Override
    protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
        if (startupDelay <= 0) {
            log.debug("no async method to start Quartz Scheduler...");
            scheduler.start();
        } else {
            Thread schedulerThread = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(startupDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                try {
                    scheduler.start();
                } catch (SchedulerException ex) {
                    log.error(ex.getMessage(), ex);
                    throw new SchedulingException("Could not start Quartz Scheduler after delay", ex);
                }
                jobStatusEnum = JobStatusEnum.RUNNING;
            });
            schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
            schedulerThread.setDaemon(true);
            schedulerThread.start();
        }
    }

    public LocalDateTime getNextTime() {
        // 创建一个GroupMatcher来匹配以给定字符串结尾的组
        GroupMatcher<TriggerKey> triggerKey = GroupMatcher.groupEndsWith(jobGroup);
        // 获取当前的调度器
        Scheduler sch = this.getScheduler();
        try {
            // 获取给定组中所有触发器的名称
            Set<TriggerKey> tks = sch.getTriggerKeys(triggerKey);
            if (!tks.isEmpty()) {
                // 返回计划触发触发器的下一次时间。如果触发器不再触发，则返回null。
                return toLocalDateTime(sch.getTrigger(tks.iterator().next()).getNextFireTime());
            }
        } catch (SchedulerException e) {
            throw new SchedulingException(e.getMessage(), e);
        }
        return null;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
