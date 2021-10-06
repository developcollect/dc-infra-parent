package com.developcollect.extra.javacc;


import com.developcollect.core.utils.CollUtil;
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

    private ListableClassPathRepository repository;
    private List<Predicate<CallInfo>> filters;

    private ThreadLocal<Map<String, CallInfo>> callInfoCacheThreadLocal = new ThreadLocal<>();


    public CallChainParser(ListableClassPathRepository repository) {
        this.repository = repository;
        this.filters = new ArrayList<>();
    }

    public CallInfo parse(String className, String methodName, Type... argTypes) {
        return doParse(new CallInfo(CallInfo.Call.of(className, methodName, argTypes)));
    }

    public CallInfo parse(JavaClass javaClass, Method method) {
        return doParse(new CallInfo(CallInfo.Call.of(javaClass, method)));
    }

    private CallInfo doParse(CallInfo ci) {
        try {
            Map<String, CallInfo> callInfoMap = callInfoCacheThreadLocal.get();
            // 这个map用来识别递归
            if (callInfoMap == null) {
                callInfoMap = new HashMap<>(128);
                callInfoCacheThreadLocal.set(callInfoMap);
            }


            Queue<CallInfo> queue = new LinkedList<>();
            queue.offer(ci);

            while (!queue.isEmpty()) {
                CallInfo tree = queue.poll();

                if (!filter(tree)) {
                    continue;
                }

                parseCallInfo(tree, callInfoMap);

                for (CallInfo child : tree.getCalleeList()) {
                    // 不为空，说明已经解析过了
                    if (CollUtil.isNotEmpty(child.getCalleeList())) {
                        continue;
                    }
                    queue.offer(child);
                }

                callInfoMap.put(tree.getCaller().getMethodInfo().getMethodSignature(), tree);
            }
        } finally {
            callInfoCacheThreadLocal.remove();
        }
        return ci;
    }

    private boolean filter(CallInfo callInfo) {
        if (filters.isEmpty()) {
            return true;
        }
        for (Predicate<CallInfo> filter : filters) {
            if (filter.test(callInfo)) {
                return true;
            }
        }
        return false;
    }

    private void parseCallInfo(CallInfo ci, Map<String, CallInfo> callInfoMap) {
        CallInfo existCallInfo = callInfoMap.get(ci.getCaller().getMethodInfo().getMethodSignature());
        if (existCallInfo != null) {
            ci.setCalleeList(existCallInfo.getCalleeList());
            return;
        }


        CallInfo.Call caller = ci.getCaller();
        MethodInfo callerMethodInfo = caller.getMethodInfo();
        String callerClassName = callerMethodInfo.getClassName();

        try {
            JavaClass javaClass = repository.loadClass(callerClassName);
            Method method = CcInnerUtil.findMethod(javaClass, callerMethodInfo.getMethodName(), callerMethodInfo.getArgumentTypes());

            parseCallInfo(ci, javaClass, method);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // 类文件不存在时忽略
        }
    }


    /**
     * 解析指定方法的调用信息，不下钻解析
     *
     * @param javaClass 类型信息
     * @param method    方法信息
     * @return 调用信息
     */
    private void parseCallInfo(CallInfo ci, JavaClass javaClass, Method method) throws ClassNotFoundException {
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

    private void addCallee(CallInfo ci, int sourceLine, JavaClass javaClass, MethodGen mg, InvokeInstruction ii) throws ClassNotFoundException {
        if (ii instanceof INVOKEDYNAMIC) {
            addDynamicCallee(ci, sourceLine, javaClass, mg, (INVOKEDYNAMIC) ii);
        } else if (ii instanceof INVOKEINTERFACE) {
            addInterfaceCallee(ci, sourceLine, javaClass, mg, (INVOKEINTERFACE) ii);
        } else {
            addDefaultCallee(ci, sourceLine, javaClass, mg, ii);
        }
    }


    private void addInterfaceCallee(CallInfo ci, int sourceLine, JavaClass javaClass, MethodGen mg, INVOKEINTERFACE invokeInterface) throws ClassNotFoundException {
        ConstantPoolGen cp = mg.getConstantPool();
        String referenceTypeName = invokeInterface.getReferenceType(cp).toString();
        String invokeMethodName = invokeInterface.getMethodName(cp);
        Type[] argumentTypes = invokeInterface.getArgumentTypes(cp);
        MethodInfo methodInfo = new MethodInfo(referenceTypeName, invokeMethodName, argumentTypes);

        CallInfo callInfo = new CallInfo(new CallInfo.Call(sourceLine, methodInfo));
        ci.addCallee(callInfo);

        // todo 识别接口调用
        // 查找实现类，获取方法实现
        JavaClass referenceJavaClass = repository.loadClass(referenceTypeName);
        List<JavaClass> implClassList = repository.getSubClassList(referenceJavaClass);
        if (!implClassList.isEmpty()) {
            System.out.println("R ==> " + referenceTypeName);
            for (JavaClass aClass : implClassList) {
                System.out.println("IM  ==>  " + aClass.getClassName());
            }
            System.out.println();

            JavaClass implClass = implClassList.get(0);
            CallInfo implCallInfo = new CallInfo(CallInfo.Call.of(implClass.getClassName(), invokeMethodName, argumentTypes));
            implCallInfo.getCaller().setLineNumber(sourceLine);
            callInfo.addCallee(implCallInfo);
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

    private void addDefaultCallee(CallInfo ci, int sourceLine, JavaClass javaClass, MethodGen mg, InvokeInstruction ii) throws ClassNotFoundException {
        ConstantPoolGen cp = mg.getConstantPool();
        String referenceTypeName = ii.getReferenceType(cp).toString();
        String invokeMethodName = ii.getMethodName(cp);
        Type[] argumentTypes = ii.getArgumentTypes(cp);
        MethodInfo methodInfo = new MethodInfo(referenceTypeName, invokeMethodName, argumentTypes);
        boolean skipRawMethodInfo = false;

        if (CcInnerUtil.METHOD_NAME_INIT.equals(invokeMethodName)) {
            // 如果是匿名内部类
            if (CcInnerUtil.isInnerAnonymousClass(referenceTypeName)) {
                // 把调用加到当前的信息中
                JavaClass iaClass = repository.loadClass(referenceTypeName);
                for (Method method : iaClass.getMethods()) {
                    CallInfo info = new CallInfo(CallInfo.Call.of(iaClass, method));
                    // todo 拿方法真正定义的行号
                    info.getCaller().setLineNumber(sourceLine);
                    ci.addCallee(info);
                    skipRawMethodInfo = true;
                }
            }
        }

        if (!skipRawMethodInfo) {
            ci.addCallee(new CallInfo(new CallInfo.Call(sourceLine, methodInfo)));
        }
    }

    public void addFilter(Predicate<CallInfo> filter) {
        if (filter != null) {
            this.filters.add(filter);
        }
    }
}
