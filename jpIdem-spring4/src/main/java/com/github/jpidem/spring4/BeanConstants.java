package com.github.jpidem.spring4;

/**
 * bean名称常量目录，方便检索自己的bean
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class BeanConstants {

    /**
     * 自定义retry数据源的bean名称
     */
    public static final String DEFAULT_DATASOURCE = "defaultRetryHandlerDataSource";

    /**
     * 自定义retry quartz job线程池的bean名称
     */
    public static final String DEFAULT_RETRY_TASKEXECUTOR = "defaultRetryTaskExecutor";
}