package com.log.record.core.operator;


/**
 * @author bo
 */
public class UserContext {

    private static final InheritableThreadLocal<Operator> OPERATOR = new InheritableThreadLocal<>();

    public static void set(Operator operator) {
        OPERATOR.set(operator);
    }

    public static Operator get() {
        return OPERATOR.get();
    }

    public static void clear() {
        OPERATOR.remove();
    }


}
