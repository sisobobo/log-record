package com.log.record.core.data;


import java.util.Collection;

/**
 * @author bo
 */
public interface ILogRecordService {
    /**
     * 保存日志
     * @param logRecords
     */
    void record(Collection<LogRecord> logRecords);

}
