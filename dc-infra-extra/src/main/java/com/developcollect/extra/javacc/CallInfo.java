package com.developcollect.extra.javacc;

import lombok.Data;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import java.util.LinkedList;
import java.util.List;


@Data
public class CallInfo {

    /**
     * 调用者
     */
    private Call caller;

    /**
     * 被调用者
     */
    private List<CallInfo> calleeList = new LinkedList<>();


    public CallInfo(Call caller) {
        this.caller = caller;
    }

    public void addCallee(CallInfo callee) {
        this.calleeList.add(callee);
    }




    @Data
    public static class Call {
        /**
         * 调用位置
         */
        private int lineNumber;

        /**
         * 方法信息
         */
        private MethodInfo methodInfo;

        public Call(MethodInfo methodInfo) {
            this.methodInfo = methodInfo;
            this.lineNumber = -1;
        }

        public Call(int lineNumber, MethodInfo methodInfo) {
            this.lineNumber = lineNumber;
            this.methodInfo = methodInfo;
        }


        public static Call of(JavaClass javaClass, Method method) {
            Call call = new Call(MethodInfo.of(javaClass, method));
            return call;
        }

        public static Call of(String className, String methodName, Type... argTypes) {
            Call call = new Call(new MethodInfo(className, methodName, argTypes));
            return call;
        }
    }
}
