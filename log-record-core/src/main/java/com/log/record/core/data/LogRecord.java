package com.log.record.core.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author bo
 */
@Data
public final class LogRecord implements Serializable {
    /**
     * 日志唯一ID
     */
    private String logId;
    /**
     * 业务ID
     */
    private String bizNo;
    /**
     * 内容
     */
    private String content;
    /**
     * 日志的种类
     */
    private String category;
    /**
     * 扩展信息
     */
    private String detail;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 方法结果
     */
    private Object result;
    /**
     * 日志操作时间
     */
    private Date operateDate;
    /**
     * 方法执行时间（单位：毫秒）
     */
    private Long executionTime;
    /**
     * 执行人ID
     */
    private String operatorId;
    /**
     * 执行人名称
     */
    private String operatorName;

}
