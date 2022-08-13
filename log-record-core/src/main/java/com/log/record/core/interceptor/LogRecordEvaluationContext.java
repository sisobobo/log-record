package com.log.record.core.interceptor;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author bo
 */
class LogRecordEvaluationContext extends MethodBasedEvaluationContext {

    static final String RESULT = "_return";

    static final String ERROR_MSG = "_errorMsg";

    public LogRecordEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer, Object result, String errorMsg) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        Map<String, Object> variables = LogRecordContext.getVariables();
        if (!CollectionUtils.isEmpty(variables)) {
            setVariables(variables);
        }
        setVariable(RESULT, result);
        setVariable(ERROR_MSG, errorMsg);
    }
}
