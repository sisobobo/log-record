package com.log.record.core.interceptor;

import com.log.record.core.annotation.LogRecord;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bo
 */
@Slf4j
public class LogRecordOperationSource {
    private static final Collection<LogRecordOperator> NULL_CACHING_ATTRIBUTE = Collections.emptyList();
    private final Map<MethodClassKey, Collection<LogRecordOperator>> attributeCache = new ConcurrentHashMap(1024);

    public Collection<LogRecordOperator> getLogRecordOperations(Method method, Class<?> targetClass) {
        //判断是否是equals toString wait等对象
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        MethodClassKey cacheKey = this.getCacheKey(method, targetClass);
        Collection<LogRecordOperator> cacheOps = this.attributeCache.get(cacheKey);
        if (Objects.nonNull(cacheOps)) {
            return cacheOps != NULL_CACHING_ATTRIBUTE ? cacheOps : null;
        } else {
            Collection<LogRecordOperator> logRecordOperations = this.computeLogRecordOperations(method, targetClass);
            if (Objects.nonNull(logRecordOperations)) {
                this.attributeCache.put(cacheKey, logRecordOperations);
            } else {
                this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
            }
            return logRecordOperations;
        }
    }

    private Collection<LogRecordOperator> computeLogRecordOperations(@NonNull Method method, @NonNull Class<?> targetClass) {
        if (this.allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        //可能是桥接方法
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        Collection<LogRecordOperator> logRecordOps = this.findCacheOperations(specificMethod);
        return logRecordOps;
    }

    private Collection<LogRecordOperator> findCacheOperations(Method method) {
        LogRecord[] logRecords = method.getAnnotationsByType(LogRecord.class);
        if (Objects.isNull(logRecords) && logRecords.length == 0) {
            return null;
        }
        Collection<LogRecordOperator> collection = new ArrayList<>(logRecords.length);
        for (LogRecord logRecord : logRecords) {
            validateLogRecord(logRecord, method);
            collection.add(convertToOperator(logRecord));
        }
        return collection;
    }

    private LogRecordOperator convertToOperator(LogRecord logRecord) {
        LogRecordOperator logRecordOperator = LogRecordOperator.builder()
                .success(logRecord.success())
                .fail(logRecord.fail())
                .operator(logRecord.operator())
                .bizNo(logRecord.bizNo())
                .category(logRecord.category())
                .detail(logRecord.detail())
                .condition(logRecord.condition())
                .build();
        return logRecordOperator;
    }

    private void validateLogRecord(LogRecord LogRecord, Method method) {
        Assert.hasText(LogRecord.bizNo(), method.getName() + "中注解@LogRecord的bizNo不能为空");
        boolean action = (StringUtils.hasText(LogRecord.success()) || StringUtils.hasText(LogRecord.fail()));
        Assert.isTrue(action, method.getName() + "中注解@LogRecord的success和fail不能都为空");
    }

    private MethodClassKey getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    protected boolean allowPublicMethodsOnly() {
        return false;
    }

}
