package com.log.record.core.function;

/**
 * @author bo
 */
public interface IFunctionService {

    String apply(String functionName, String args);

    boolean beforeFunction(String functionName);

}
