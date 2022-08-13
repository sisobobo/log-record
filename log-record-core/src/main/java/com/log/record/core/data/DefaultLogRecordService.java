package com.log.record.core.data;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author bo
 */
@Slf4j
public class DefaultLogRecordService implements ILogRecordService {

    @Override
    public void record(Collection<LogRecord> logRecords) {
        logRecords.forEach(logRecord -> log.info("【logRecord】log={}", logRecord));
    }

}
