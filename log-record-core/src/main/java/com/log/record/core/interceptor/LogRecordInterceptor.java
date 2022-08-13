package com.log.record.core.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.CollectionUtils;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

/**
 * @author bo
 */
@Slf4j
public class LogRecordInterceptor extends LogRecordValueParser implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        LogRecordInvoker invoker = () -> {
            LogRecordInvoker.InvokerResult result;
            try {
                Object ret = invocation.proceed();
                result = LogRecordInvoker.InvokerResult.isSuccess(ret);
            } catch (Exception e) {
                result = LogRecordInvoker.InvokerResult.isFail(e);
            }
            return result;
        };
        return this.execute(invoker, invocation.getThis(), method, invocation.getArguments());
    }

    private Object execute(LogRecordInvoker invoker, Object target, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = this.getTargetClass(target);
        LogRecordContext.putEmptySpan();
        Collection<LogRecordOperator> logRecordOperations = null;
        try {
            logRecordOperations = this.processTemplate(true, null, method, args, targetClass, null, null);
        } catch (Exception e) {
            log.error("LogRecord解析前置函数异常", e);
        }
        //执行业务方法
        LogRecordInvoker.InvokerResult invokerResult = invoker.invoke();
        try {
            if (!CollectionUtils.isEmpty(logRecordOperations)) {
                logRecordOperations = this.processTemplate(false, logRecordOperations, method, args, targetClass, invokerResult.getResult(), invokerResult.getErrorMsg());
                logRecordOperations.forEach(operator -> log.info("[LogRecord][{}]", operator));
            }
        } catch (Exception t) {
            //记录日志错误不要影响业务
            log.error("LogRecord解析异常", t);
        } finally {
            LogRecordContext.clear();
        }
        if (Objects.nonNull(invokerResult.getThrowable())) {
            throw invokerResult.getThrowable();
        }
        return invokerResult.getResult();
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }


}
