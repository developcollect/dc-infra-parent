package com.developcollect.extra.javacc;


import com.developcollect.core.utils.CollUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * 调用链解析器
 */
@Slf4j
public class CallChainParser implements ICallChainParser {

    private final ListableClassPathRepository repository;
    @Setter
    private SubClassScanner subClassScanner;
    private List<Predicate<CallInfo>> parseFilters;

    private final ThreadLocal<Map<String, CallInfo>> callInfoCacheThreadLocal = new ThreadLocal<>();


    public CallChainParser(ListableClassPathRepository repository) {
        this.repository = repository;
        this.parseFilters = new ArrayList<>();
        this.subClassScanner = defaultSubClassScanner();
    }

    @Override
    public CallInfo parse(String className, String methodName, Type... argTypes) {
        return doParse(CallInfo.of(className, methodName, argTypes));
    }

    @Override
    public CallInfo parse(JavaClass javaClass, Method method) {
        return doParse(CallInfo.of(javaClass, method));
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

                if (!doParseFilter(tree)) {
                    continue;
                }

                parseCallInfo(tree, callInfoMap);

                for (CallInfo child : tree.getCalleeList()) {
                    // 如果是接口，并且CalleeList是空的(没有找到实现类)，直接忽略
                    if (child.isInterface()) {
                        if (CollUtil.isEmpty(child.getCalleeList())) {
                            continue;
                        }
                    }
                    // 如果不是接口，并且CalleeList不是空的(已经解析过了)，直接忽略
                    else {
                        if (CollUtil.isNotEmpty(child.getCalleeList())) {
                            continue;
                        }
                    }

                    queue.offer(child);
                }

                callInfoMap.put(tree.getCallerSignature(), tree);
            }
        } finally {
            callInfoCacheThreadLocal.remove();
        }
        return ci;
    }

    private boolean doParseFilter(CallInfo callInfo) {
        if (parseFilters.isEmpty()) {
            return true;
        }
        for (Predicate<CallInfo> filter : parseFilters) {
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
            Method method = CcSupport.findMethod(javaClass, callerMethodInfo.getMethodName(), callerMethodInfo.getArgumentTypes());

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


        CallInfo interCallInfo = CallInfo.of(new CallInfo.Call(sourceLine, methodInfo));
        interCallInfo.setInterface(true);
        ci.addCallee(interCallInfo);

        // 识别接口调用
        // 查找实现类，获取方法实现
        JavaClass referenceJavaClass = repository.loadClass(referenceTypeName);
        List<JavaClass> implClassList = subClassScanner.scan(repository, referenceJavaClass);
        if (CollUtil.isNotEmpty(implClassList)) {
            for (JavaClass implClass : implClassList) {
                CallInfo implCallInfo = CallInfo.of(implClass.getClassName(), invokeMethodName, argumentTypes);
                implCallInfo.getCaller().setLineNumber(sourceLine);
                interCallInfo.addCallee(implCallInfo);
            }
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
        BootstrapMethod bootstrapMethod = CcSupport.getBootstrapMethod(javaClass, cid.getBootstrapMethodAttrIndex());
        if (bootstrapMethod == null) {
            throw new RuntimeException("### 无法找到bootstrapMethod " + cid.getBootstrapMethodAttrIndex());
        }

        // 获得BootstrapMethod的方法信息
        MethodInfo bootstrapMethodMethod = CcSupport.getBootstrapMethodMethod(bootstrapMethod, javaClass);
        if (bootstrapMethodMethod == null) {
            throw new RuntimeException("### 无法找到bootstrapMethod的方法信息 " + javaClass.getClassName() + " " + bootstrapMethod);
        }


        ci.addCallee(CallInfo.of(new CallInfo.Call(sourceLine, bootstrapMethodMethod)));
    }

    private void addDefaultCallee(CallInfo ci, int sourceLine, JavaClass javaClass, MethodGen mg, InvokeInstruction ii) throws ClassNotFoundException {
        ConstantPoolGen cp = mg.getConstantPool();
        String referenceTypeName = ii.getReferenceType(cp).toString();
        String invokeMethodName = ii.getMethodName(cp);
        Type[] argumentTypes = ii.getArgumentTypes(cp);
        MethodInfo methodInfo = new MethodInfo(referenceTypeName, invokeMethodName, argumentTypes);
        boolean skipRawMethodInfo = false;

        if (CcSupport.METHOD_NAME_INIT.equals(invokeMethodName)) {
            // 如果是匿名内部类
            if (CcSupport.isInnerAnonymousClass(referenceTypeName)) {
                // 把调用加到当前的信息中
                JavaClass iaClass = repository.loadClass(referenceTypeName);
                for (Method method : iaClass.getMethods()) {
                    CallInfo info = CallInfo.of(iaClass, method);
                    // todo 拿方法真正定义的行号
                    info.getCaller().setLineNumber(sourceLine);
                    ci.addCallee(info);
                    skipRawMethodInfo = true;
                }
            }
        }

        if (!skipRawMethodInfo) {
            ci.addCallee(CallInfo.of(new CallInfo.Call(sourceLine, methodInfo)));
        }
    }

    public void addParseFilter(Predicate<CallInfo> filter) {
        if (filter != null) {
            this.parseFilters.add(filter);
        }
    }


    private static SubClassScanner defaultSubClassScanner() {
        return ListableClassPathRepository::getSubClassList;
    }

    @FunctionalInterface
    public interface SubClassScanner {
        List<JavaClass> scan(ListableClassPathRepository repository, JavaClass superClass);
    }
}
