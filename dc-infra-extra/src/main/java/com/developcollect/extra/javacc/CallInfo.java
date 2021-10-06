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
    private List<CallInfo> calleeList;

    /**
     * 标记是否是接口，如果是接口的话
     * 那么 calleeList 就不是被调用者，而是不同的实现类
     */
    private boolean isInterface;

    private CallInfo(Call caller) {
        this.caller = caller;
        this.isInterface = false;
        this.calleeList = new LinkedList<>();
    }

    public void addCallee(CallInfo callee) {
        this.calleeList.add(callee);
    }


    public String getCallerSignature() {
        return getCaller().getMethodInfo().getMethodSignature();
    }


    public static CallInfo of(JavaClass javaClass, Method method) {
        return of(javaClass.getClassName(), method.getName(), method.getArgumentTypes());
    }

    public static CallInfo of(String className, String methodName, Type... argTypes) {
        return of(Call.of(className, methodName, argTypes));
    }

    public static CallInfo of(Call call) {
        return new CallInfo(call);
    }


    @Data
    public static class Call {
        /**
         * 调用位置
         */
        private int lineNumber;

        /**
         * 调用类型
         */
        private String callType;

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


        static Call of(String className, String methodName, Type... argTypes) {
            return new Call(new MethodInfo(className, methodName, argTypes));
        }


        @Override
        public String toString() {
            return "[" + lineNumber + "]  " + methodInfo;
        }
    }
}
