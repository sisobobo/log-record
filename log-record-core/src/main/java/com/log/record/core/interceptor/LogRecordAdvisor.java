package com.log.record.core.interceptor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.Nullable;

/**
 * @author bo
 */
public class LogRecordAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final LogRecordPointcut pointcut;

    public LogRecordAdvisor(@Nullable LogRecordOperationSource logRecordOperationSource) {
        this.pointcut = new LogRecordPointcut(logRecordOperationSource);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
