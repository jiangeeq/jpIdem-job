package com.github.jpidem.spring4.registry.quartz.web;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yuni[mn960mn@163.com]
 */
@Setter
@Getter
public class JobDetail {

    private String name;

    private String identity;

    private String period;

    private String status;

    private Integer count;
    /**
     * 下次执行时间
     */
    private String nextTime;
}
