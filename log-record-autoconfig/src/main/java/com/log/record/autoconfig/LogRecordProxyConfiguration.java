package com.log.record.autoconfig;

import com.log.record.core.data.DefaultLogRecordService;
import com.log.record.core.data.ILogRecordService;
import com.log.record.core.function.DefaultFunctionService;
import com.log.record.core.function.IFunctionService;
import com.log.record.core.function.IParseFunction;
import com.log.record.core.interceptor.LogRecordInterceptor;
import com.log.record.core.interceptor.LogRecordAdvisor;
import com.log.record.core.interceptor.LogRecordOperationSource;
import com.log.record.core.operator.DefaultOperatorGetService;
import com.log.record.core.operator.IOperatorGetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * 装配组件的核心类
 *
 * @author bo
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class LogRecordProxyConfiguration implements ImportAware {

    private AnnotationAttributes enableLogRecord;

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordAdvisor logRecordAdvisor(LogRecordOperationSource logRecordOperationSource, LogRecordInterceptor logRecordAdvice) {
        LogRecordAdvisor advisor = new LogRecordAdvisor(logRecordOperationSource);
        advisor.setAdvice(logRecordAdvice);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordOperationSource logRecordOperationSource() {
        return new LogRecordOperationSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogRecordInterceptor logRecordAdvice(LogRecordOperationSource logRecordOperationSource, IFunctionService functionService) {
        LogRecordInterceptor logRecordAdvice = new LogRecordInterceptor();
        logRecordAdvice.setLogRecordOperationSource(logRecordOperationSource);
        logRecordAdvice.setFunctionService(functionService);
        //    logRecordAdvice.setTenant(enableLogRecord.getString("tenant"));
        return logRecordAdvice;
    }

    @Bean
    @ConditionalOnMissingBean(IFunctionService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public IFunctionService functionService(@Autowired List<IParseFunction> parseFunctions) {
        return new DefaultFunctionService(parseFunctions);
    }

    @Bean
    @ConditionalOnMissingBean(IOperatorGetService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public IOperatorGetService operatorGetService() {
        return new DefaultOperatorGetService();
    }

    @Bean
    @ConditionalOnMissingBean(ILogRecordService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public ILogRecordService logRecordService() {
        return new DefaultLogRecordService();
    }


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableLogRecord = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableLogRecord.class.getName(), false));
        if (this.enableLogRecord == null) {
            log.info("@EnableCaching is not present on importing class");
        }
    }

}

