package com.github.jpidem.core;

import java.util.List;

/**
 * 对数据库表的sql操作方法
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public interface RetryTaskMapper {
    /**
     * @param retryTask 重试任务实体类
     * @return int
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    int insert(RetryTask retryTask);

    /**
     * @param retryTask 重试任务实体类
     * @return int
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    int update(RetryTask retryTask);

    /**
     * @param identity     任务的唯一标识
     * @param retryCount   重试次数
     * @param initialDelay 初始重试延迟时长
     * @return List<RetryTask>
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    List<RetryTask> queryNeedRetryTaskList(String identity, int retryCount, int initialDelay);

    /**
     * @param identity 任务的唯一标识
     * @return int
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    int queryRetryTotal(String identity);
}
