package com.log.record.autoconfig;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 需要导入到 spring 容器中的类
 * @author bo
 */
public class LogRecordConfigureSelector extends AdviceModeImportSelector<EnableLogRecord> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return this.getProxyImports();
            default:
                return null;
        }
    }

    private String[] getProxyImports() {
        List<String> result = new ArrayList(2);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(LogRecordProxyConfiguration.class.getName());
        return StringUtils.toStringArray(result);
    }
}
