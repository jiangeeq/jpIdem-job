package com.github.jpidem.core;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 重试任务实体类
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
@Data
public class RetryTask {
    /**
     * 初始化
     */
    public static final int STATUS_INIT = 1;
    /**
     * 执行完成
     */
    public static final int STATUS_SUCCESS = 2;
    /**
     * 异常
     */
    public static final int STATUS_EXCEPTION = 3;
    /**
     * 任务的ID
     */
    private Long taskId;
    /**
     * 任务的唯一标识
     */
    private String identity;
    /**
     * 参数
     */
    private String params;
    /**
     * 状态
     */
    private int status;
    /**
     * 重试次数
     */
    private int retryCount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 修改时间
     */
    private LocalDateTime editDate;
}
