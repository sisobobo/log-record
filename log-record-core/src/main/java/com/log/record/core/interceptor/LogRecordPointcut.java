package com.log.record.core.interceptor;

import com.log.record.core.annotation.LogRecord;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author bo
 */
class LogRecordPointcut extends StaticMethodMatcherPointcut implements Serializable {

    private final LogRecordOperationSource logRecordOperationSource;

    LogRecordPointcut(@NonNull LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Collection<LogRecordOperator> logRecordOps = logRecordOperationSource.getLogRecordOperations(method, targetClass);
        return !CollectionUtils.isEmpty(logRecordOps);
    }

}
