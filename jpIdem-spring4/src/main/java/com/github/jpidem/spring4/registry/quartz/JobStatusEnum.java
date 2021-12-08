package com.github.jpidem.spring4.registry.quartz;

/**
 * job状态枚举
 *
 * @see RetrySchedulerFactoryBean
 *
 * @author yuni[mn960mn@163.com]
 */
public enum JobStatusEnum {

    INIT("启动中"), PREPARE("未启动"), RUNNING("运行中"), STOPED("已停止");

    private String desc;

    JobStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}