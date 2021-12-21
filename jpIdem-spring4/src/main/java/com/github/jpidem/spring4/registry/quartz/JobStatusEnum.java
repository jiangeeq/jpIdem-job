package com.github.jpidem.spring4.registry.quartz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * job状态枚举
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 * @see RetrySchedulerFactoryBean
 */
@AllArgsConstructor
public enum JobStatusEnum {

    INIT("启动中"),
    PREPARE("未启动"),
    RUNNING("运行中"),
    STOPED("已停止");

    @Getter
    private String desc;
}