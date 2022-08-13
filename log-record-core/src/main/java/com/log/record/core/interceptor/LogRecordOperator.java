package com.log.record.core.interceptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bo
 */
@Data
@Builder
public class LogRecordOperator {

    private String success;

    private String fail;

    private String bizNo;

    private String operator;

    private String category;

    private String detail;

    private String condition;

}
