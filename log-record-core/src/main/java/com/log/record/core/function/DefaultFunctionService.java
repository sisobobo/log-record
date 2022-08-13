package com.log.record.core.function;

import java.util.List;
import java.util.Objects;

/**
 * @author bo
 */
public class DefaultFunctionService implements IFunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    public DefaultFunctionService(List<IParseFunction> parseFunctions) {
        this.parseFunctionFactory = new ParseFunctionFactory(parseFunctions);
    }

    @Override
    public String apply(String functionName, String args) {
        IParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (Objects.isNull(function)) {
            return args;
        }
        return function.apply(args);
    }

    @Override
    public boolean beforeFunction(String functionName) {
        return parseFunctionFactory.isBeforeFunction(functionName);
    }
}
