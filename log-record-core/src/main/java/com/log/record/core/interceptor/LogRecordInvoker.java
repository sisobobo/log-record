package com.log.record.core.interceptor;

import lombok.Getter;

/**
 * @author bo
 */
interface LogRecordInvoker {

    InvokerResult invoke() throws Throwable;

    @Getter
    class InvokerResult {
        private boolean success;
        private String errorMsg;
        private Object result;
        private Throwable throwable;

        public static InvokerResult isSuccess(Object result) {
            InvokerResult invokerResult = new InvokerResult();
            invokerResult.success = true;
            invokerResult.result = result;
            return invokerResult;
        }

        public static InvokerResult isFail(Exception e) {
            InvokerResult invokerResult = new InvokerResult();
            invokerResult.success = false;
            invokerResult.throwable = e;
            invokerResult.errorMsg = e.getMessage();
            return invokerResult;
        }
    }

}
