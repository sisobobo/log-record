package com.log.record.core.operator;

/**
 * @author bo
 */
public interface IOperatorGetService {

    /**
     * 可以在里面外部的获取当前登陆的用户，比如 UserContext.getCurrentUser()
     *
     * @return
     */
    Operator getOperator();

}
