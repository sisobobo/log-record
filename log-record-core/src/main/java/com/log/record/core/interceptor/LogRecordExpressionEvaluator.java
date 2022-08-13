package com.log.record.core.interceptor;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bo
 */
class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {

    /**
     * 缓存方法、表达式和 SpEL 的 Expression 的对应关系
     * 让方法注解上添加的 SpEL 表达式只解析一次
     */
    private Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);
    /**
     * 缓存传入到 Expression 表达式的 Object
     */
    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

    /**
     * 创建spel上下文信息
     *
     * @param method
     * @param args
     * @param targetClass
     * @param result
     * @param errorMsg
     * @param beanFactory
     * @return
     */
    public LogRecordEvaluationContext createEvaluationContext(Method method, Object[] args, Class<?> targetClass,
                                                              Object result, String errorMsg, BeanFactory beanFactory) {
        Method targetMethod = getTargetMethod(targetClass, method);
        LogRecordEvaluationContext evaluationContext = new LogRecordEvaluationContext(null, targetMethod
                , args, getParameterNameDiscoverer(), result, errorMsg);
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    public String parseExpression(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        Object parseValue = getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evalContext, Object.class);
        String value = parseValue instanceof String ? (String) parseValue : parseValue.toString();
        return value;
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = this.targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            this.targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }


}
