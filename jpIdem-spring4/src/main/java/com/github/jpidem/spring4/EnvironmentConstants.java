package com.github.jpidem.spring4;

/**
 * 系统参数名称
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class EnvironmentConstants {
    /**
     * 是否开启使用jpIdem
     */
    public static final String RETRY_ENABLED = "retry.enabled";
    /**
     * 是否开启job web界面
     */
    public static final String RETRY_WEB_ENABLED = "retry.web.enabled";

    public static final String RETRY_SQLMAPPING_FILEPATH_KEY = "retry.sqlMapping.filepath";
    /**
     * 是否开启执行任务之前保存任务入库
     */
    public static final String RETRY_BEFORETASK = "retry.beforeTask";
}