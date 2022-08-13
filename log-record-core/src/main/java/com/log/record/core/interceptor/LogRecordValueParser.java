package com.log.record.core.interceptor;

import com.log.record.core.function.IFunctionService;
import com.log.record.core.operator.Operator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ParserContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author bo
 */
class LogRecordValueParser implements BeanFactoryAware {

    private static final String RESULT = "#" + LogRecordEvaluationContext.RESULT;
    private static final String ERROR_MSG = "#" + LogRecordEvaluationContext.ERROR_MSG;
    private static final String FUN_PREFIX = "fun_";
    private static final Pattern FUN_PATTERN = Pattern.compile(FUN_PREFIX + "(\\w*)\\((.*?)\\)");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("#\\{\\s*(.*?)\\s*}");

    private final LogRecordExpressionEvaluator evaluator = new LogRecordExpressionEvaluator();

    private BeanFactory beanFactory;

    private IFunctionService functionService;

    private LogRecordOperationSource logRecordOperationSource;

    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }

    public void setFunctionService(IFunctionService functionService) {
        this.functionService = functionService;
    }


    protected Collection<LogRecordOperator> processTemplate(boolean before, Collection<LogRecordOperator> operators, Method method, Object[] args, Class<?> targetClass, Object result, String errorMsg) {
        if (null == operators) {
            operators = logRecordOperationSource.getLogRecordOperations(method, targetClass);
        }
        AnnotatedElementKey annotatedElementKey = this.createAnnotatedElementKey(method, targetClass);
        EvaluationContext evaluationContext = this.evaluator.createEvaluationContext(method, args, targetClass, result, errorMsg, this.beanFactory);
        return operators.stream()
                .map(operator -> this.parseLogRecordOperator(before, operator, annotatedElementKey, evaluationContext))
                .collect(Collectors.toList());
    }

    private LogRecordOperator parseLogRecordOperator(boolean before, LogRecordOperator operator, AnnotatedElementKey annotatedElementKey, EvaluationContext context) {
        LogRecordOperator logRecordOperator = LogRecordOperator.builder().build();
        Class<?> clazz = operator.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String value = parseTemplate(before , (String) field.get(operator) , annotatedElementKey , context);
                field.set(logRecordOperator, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return logRecordOperator;
    }

    /**
     * 解析模板
     * @param before
     * @param template
     * @param annotatedElementKey
     * @param context
     * @return
     */
    private String parseTemplate(boolean before, String template , AnnotatedElementKey annotatedElementKey, EvaluationContext context){
        if (StringUtils.hasText(template)) {
            if (template.contains(FUN_PREFIX)) {
                template = parseFunction(before, template, annotatedElementKey, context);
            }
            if (!before && template.contains(ParserContext.TEMPLATE_EXPRESSION.getExpressionPrefix())) {
                template = parseExpression(template, annotatedElementKey, context);
            }
        }
        return  template ;
    }

    /**
     * 解析自定义方法
     *
     * @param before
     * @param template
     * @param annotatedElementKey
     * @param context
     * @return
     */
    private String parseFunction(boolean before, String template, AnnotatedElementKey annotatedElementKey, EvaluationContext context) {
        Matcher matcher = FUN_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String expression = matcher.group(2);
            boolean hasInvokerValue = expression.contains(LogRecordValueParser.RESULT) || expression.contains(LogRecordValueParser.ERROR_MSG);
            if (before && hasInvokerValue) {
                continue;
            }
            String functionName = matcher.group(1);
            if (functionService.beforeFunction(functionName) || !before) {
                String args = "";
                if (StringUtils.hasText(expression)) {
                    args = evaluator.parseExpression(expression, annotatedElementKey, context);
                }
                String value = functionService.apply(functionName, args);
                matcher.appendReplacement(buffer, value);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 解析Spring El表达式
     *
     * @param template
     * @param annotatedElementKey
     * @param context
     * @return
     */
    private String parseExpression(String template, AnnotatedElementKey annotatedElementKey, EvaluationContext context) {
        if (template.contains(ParserContext.TEMPLATE_EXPRESSION.getExpressionPrefix())) {
            Matcher matcher = EXPRESSION_PATTERN.matcher(template);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                String expression = matcher.group(1);
                String value = evaluator.parseExpression(expression, annotatedElementKey, context);
                matcher.appendReplacement(buffer, value);
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        } else {
            return evaluator.parseExpression(template, annotatedElementKey, context);
        }
    }

    private AnnotatedElementKey createAnnotatedElementKey(Method method, Class<?> targetClass) {
        return new AnnotatedElementKey(method, targetClass);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}

