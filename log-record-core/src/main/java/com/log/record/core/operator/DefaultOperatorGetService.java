package com.log.record.core.operator;

import java.util.Optional;

/**
 * @author bo
 */
public class DefaultOperatorGetService implements IOperatorGetService{

    @Override
    public Operator getOperator() {
        return Optional.ofNullable(UserContext.get())
                .map(a -> new Operator(a.getOperatorId(), a.getOperatorName()))
                .orElseThrow(()->new IllegalArgumentException("user is null"));
    }
}
