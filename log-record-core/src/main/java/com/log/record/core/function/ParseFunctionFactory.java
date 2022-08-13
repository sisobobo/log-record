package com.log.record.core.function;



import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义函数工厂
 *
 * @author bo
 */
class ParseFunctionFactory {

    private Map<String, IParseFunction> allFunctionMap;

    ParseFunctionFactory(List<IParseFunction> parseFunctions) {
        if (!CollectionUtils.isEmpty(parseFunctions)) {
            allFunctionMap = parseFunctions.stream()
                    .filter(parseFunction -> StringUtils.hasText(parseFunction.functionName()))
                    .collect(Collectors.toMap(IParseFunction::functionName, Function.identity()));
        }
    }

    IParseFunction getFunction(String functionName) {
        return allFunctionMap.get(functionName);
    }

    boolean isBeforeFunction(String functionName) {
        return Objects.nonNull(allFunctionMap.get(functionName)) && allFunctionMap.get(functionName).executeBefore();
    }


}
