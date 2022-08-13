package com.log.record.core.function;

/**
 * 自定义函数
 *
 * @author bo
 */
public interface IParseFunction {

    default boolean executeBefore(){
        return false;
    }

    String functionName();

    String apply(String value);

}
