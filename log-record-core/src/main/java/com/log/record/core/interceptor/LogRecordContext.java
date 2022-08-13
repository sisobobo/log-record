package com.log.record.core.interceptor;

import org.springframework.util.CollectionUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * @author bo
 * 这里使用了 InheritableThreadLocal，所以在线程池的场景下使用 LogRecordContext 会出现问题，
 */
public class LogRecordContext {

    private static final InheritableThreadLocal<Stack<Map<String, Object>>> MAP_STACK = new InheritableThreadLocal<>();

    public static void putVariable(String key, Object value) {
        MAP_STACK.get().peek().put(key, value);
    }

    static Map<String, Object> getVariables() {
        return MAP_STACK.get().peek();
    }

    static void clear() {
        if (!CollectionUtils.isEmpty(MAP_STACK.get())) {
            MAP_STACK.get().pop();
        }
    }

    static void putEmptySpan() {
        Stack<Map<String, Object>> stack = MAP_STACK.get();
        if (Objects.isNull(stack)) {
            stack = new Stack<>();
            MAP_STACK.set(stack);
        }
        stack.push(new HashMap<>(8));
    }

}
