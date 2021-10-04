package com.developcollect.extra.javacc;


import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * 调用链解析器
 */
@Slf4j
public class CallChainParser {


    private BcelClassLoader bcelClassLoader;

    private Map<String, CallInfo> callInfoMap = new HashMap<>();

    private List<Predicate<CallInfo>> filters;


    public CallChainParser(BcelClassLoader bcelClassLoader) {
        this.bcelClassLoader = bcelClassLoader;
        this.filters = new ArrayList<>();
    }

    public CallInfo parse(String className, String methodName, Type... argTypes) {
        return doParse(new CallInfo(CallInfo.Call.of(className, methodName, argTypes)));
    }

    public CallInfo parse(JavaClass javaClass, Method method) {
        return doParse(new CallInfo(CallInfo.Call.of(javaClass, method)));
    }

    private CallInfo doParse(CallInfo ci) {
        Queue<CallInfo> queue = new LinkedList<>();
        queue.offer(ci);

        while (!queue.isEmpty()) {
            CallInfo tree = queue.poll();

            if (!filter(tree)) {
                continue;
            }

            parseCallInfo(tree);

            for (CallInfo child : tree.getCalleeList()) {
                if (callInfoMap.containsKey(child.getCaller().getMethodInfo().getMethodSignature())) {
                    continue;
                }
                queue.offer(child);
            }

            callInfoMap.put(tree.getCaller().getMethodInfo().getMethodSignature(), tree);
        }

        return ci;
    }

    private boolean filter(CallInfo callInfo) {
        for (Predicate<CallInfo> filter : filters) {
            if (filter.test(callInfo)) {
                return true;
            }
        }
        return false;
    }

    private void parseCallInfo(CallInfo ci) {
        CallInfo existCallInfo = callInfoMap.get(ci.getCaller().getMethodInfo().getMethodSignature());
        if (existCallInfo != null) {
            ci.setCalleeList(existCallInfo.getCalleeList());
            return;
        }


        CallInfo.Call caller = ci.getCaller();
        MethodInfo callerMethodInfo = caller.getMethodInfo();
        String callerClassName = callerMethodInfo.getClassName();

        try {
            JavaClass javaClass = bcelClassLoader.getJavaClass(callerClassName);
            Method method = CcInnerUtil.findMethod(javaClass, callerMethodInfo.getMethodName(), callerMethodInfo.getArgumentTypes());

            parseCallInfo(ci, javaClass, method);
        } catch (Exception e) {
            // 类文件不存在时忽略
        }
    }



    /**
     * 解析指定方法的调用信息，不下钻解析
     * @param javaClass 类型信息
     * @param method 方法信息
     * @return 调用信息
     */
    private void parseCallInfo(CallInfo ci, JavaClass javaClass, Method method) {
        ConstantPoolGen constantPool = new ConstantPoolGen(javaClass.getConstantPool());
        MethodGen mg = new MethodGen(method, javaClass.getClassName(), constantPool);
        if (mg.isAbstract() || mg.isNative()) {
            return;
        }

        LineNumberGen[] lineNumbers = mg.getLineNumbers();
        for (int i = 0; i < lineNumbers.length; i++) {
            LineNumberGen lineNumber = lineNumbers[i];
            int sourceLine = lineNumber.getSourceLine();
            InstructionHandle endInstruction = i + 1 == lineNumbers.length
                    ? null :
                    lineNumbers[i + 1].getInstruction();

            for (InstructionHandle ih = lineNumber.getInstruction(); ih != endInstruction; ih = ih.getNext()) {
                Instruction instruction = ih.getInstruction();
                if (instruction instanceof InvokeInstruction) {
                    InvokeInstruction ii = (InvokeInstruction) instruction;
                    addCallee(ci, sourceLine, javaClass, mg, ii);
                }
            }
        }
    }

    private void addCallee(CallInfo ci, int sourceLine, JavaClass javaClass, MethodGen mg, InvokeInstruction ii) {
        if (ii instanceof INVOKEDYNAMIC) {
            addDynamicCallee(ci, sourceLine, javaClass, mg, (INVOKEDYNAMIC) ii);
        } else {
            if (ii instanceof INVOKEINTERFACE) {
                // todo 识别接口调用

                System.out.println("INVOKEINTERFACE");
            }

            ConstantPoolGen cp = mg.getConstantPool();
            String referenceTypeName = ii.getReferenceType(cp).toString();
            String invokeMethodName = ii.getMethodName(cp);
            Type[] argumentTypes = ii.getArgumentTypes(cp);
            MethodInfo methodInfo = new MethodInfo(referenceTypeName, invokeMethodName, argumentTypes);

            ci.addCallee(new CallInfo(new CallInfo.Call(sourceLine, methodInfo)));
        }
    }

    private void addDynamicCallee(CallInfo ci, int sourceLine, JavaClass javaClass, MethodGen mg, INVOKEDYNAMIC invokeDynamic) {
        ConstantPoolGen cp = mg.getConstantPool();

        Constant constantId = cp.getConstant(invokeDynamic.getIndex());
        if (!(constantId instanceof ConstantInvokeDynamic)) {
            return;
        }

        // 处理Lambda表达式
        ConstantInvokeDynamic cid = (ConstantInvokeDynamic) constantId;
        // 获得JavaClass中指定下标的BootstrapMethod
        BootstrapMethod bootstrapMethod = CcInnerUtil.getBootstrapMethod(javaClass, cid.getBootstrapMethodAttrIndex());
        if (bootstrapMethod == null) {
            throw new RuntimeException("### 无法找到bootstrapMethod " + cid.getBootstrapMethodAttrIndex());
        }

        // 获得BootstrapMethod的方法信息
        MethodInfo bootstrapMethodMethod = CcInnerUtil.getBootstrapMethodMethod(bootstrapMethod, javaClass);
        if (bootstrapMethodMethod == null) {
            throw new RuntimeException("### 无法找到bootstrapMethod的方法信息 " + javaClass.getClassName() + " " + bootstrapMethod);
        }

        ci.addCallee(new CallInfo(new CallInfo.Call(sourceLine, bootstrapMethodMethod)));
    }


    public void addFilter(Predicate<CallInfo> filter) {
        this.filters.add(filter);
    }
}
