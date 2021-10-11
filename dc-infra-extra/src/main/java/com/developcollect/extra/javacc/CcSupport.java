package com.developcollect.extra.javacc;

import cn.hutool.core.util.StrUtil;
import com.developcollect.core.utils.CollUtil;
import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassQueue;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

/**
 *
 */

class CcSupport {


    public static final String FLAG_LAMBDA = "lambda$";

    public static final int FLAG_LAMBDA_LENGTH = FLAG_LAMBDA.length();

    public static final int DEFAULT_LINE_NUMBER = 0;
    public static final int NONE_LINE_NUMBER = -1;

    public static final String METHOD_NAME_INIT = "<init>";

    public static final String METHOD_NAME_START = "start";

    public static final String NEW_LINE = "\n";


    public static boolean isInnerAnonymousClass(String className) {
        if (!className.contains("$")) {
            return false;
        }

        String[] array = className.split("\\$");
        if (array.length != 2) {
            return false;
        }

        if (!isNumStr(array[1])) {
            return false;
        }
        return true;
    }

    public static boolean isNumStr(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        char[] charArray = str.toCharArray();
        for (char ch : charArray) {
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    public static String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder("(");
        for (Type argument : arguments) {
            sb.append(argument.toString()).append(", ");
        }
        if (sb.length() > 2) {
            sb.replace(sb.length() - 2, sb.length(), ")");
        } else {
            sb.append(")");
        }
        return sb.toString();
    }

    public static String methodSignature(Method method) {
        return method.getName() + argumentList(method.getArgumentTypes());
    }

    public static List<String> genImplClassMethodWithArgs(Method[] methods) {
        List<String> methodInfoList = new ArrayList<>(methods.length);
        for (Method method : methods) {
            String methodName = method.getName();
            // 忽略"<init>"和"<clinit>"方法
            if (!methodName.startsWith("<") && method.isPublic() && !method.isAbstract() && !method.isStatic()) {
                methodInfoList.add(methodName + CcSupport.argumentList(method.getArgumentTypes()));
            }
        }
        return methodInfoList;
    }

    public static List<String> genInterfaceAbstractMethodWithArgs(Method[] methods) {
        List<String> methodInfoList = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (method.isAbstract()) {
                methodInfoList.add(method.getName() + CcSupport.argumentList(method.getArgumentTypes()));
            }
        }
        return methodInfoList;
    }

    public static String getLambdaOrigMethod(String lambdaMethod) {
        int indexLastLambda = lambdaMethod.lastIndexOf(FLAG_LAMBDA);
        String tmpString = lambdaMethod.substring(indexLastLambda + FLAG_LAMBDA_LENGTH);
        int indexDollar = tmpString.indexOf('$');
        return tmpString.substring(0, indexDollar);
    }

    public static int getFuncStartSourceLine(Method method) {
        LineNumberTable lineNumberTable = method.getLineNumberTable();
        if (lineNumberTable == null || lineNumberTable.getLineNumberTable() == null) {
            return DEFAULT_LINE_NUMBER;
        }

        return lineNumberTable.getLineNumberTable()[0].getLineNumber();
    }

    private static int getInitFuncStartSourceLine(JavaClass javaClass) {
        Method[] methods = javaClass.getMethods();
        if (methods == null) {
            return DEFAULT_LINE_NUMBER;
        }

        for (Method method : methods) {
            if (METHOD_NAME_INIT.equals(method.getName())) {
                return CcSupport.getFuncStartSourceLine(method);
            }
        }

        return DEFAULT_LINE_NUMBER;
    }

    public static String getCanonicalPath(String filePath) {
        try {
            return new File(filePath).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得JavaClass中指定下标的BootstrapMethod
     *
     * @param javaClass
     * @param index
     * @return
     */
    public static BootstrapMethod getBootstrapMethod(JavaClass javaClass, int index) {
        Attribute[] attributes = javaClass.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute instanceof BootstrapMethods) {
                BootstrapMethods bootstrapMethods = (BootstrapMethods) attribute;
                BootstrapMethod[] bootstrapMethodArray = bootstrapMethods.getBootstrapMethods();
                if (bootstrapMethodArray != null && bootstrapMethodArray.length > index) {
                    return bootstrapMethodArray[index];
                }
            }
        }

        return null;
    }

    /**
     * 获得BootstrapMethod的方法信息
     *
     * @param bootstrapMethod
     * @param javaClass
     * @return
     */
    public static MethodInfo getBootstrapMethodMethod(BootstrapMethod bootstrapMethod, JavaClass javaClass) {
        for (int argIndex : bootstrapMethod.getBootstrapArguments()) {
            Constant constantArg = javaClass.getConstantPool().getConstant(argIndex);
            if (!(constantArg instanceof ConstantMethodHandle)) {
                continue;
            }

            ConstantMethodHandle constantMethodHandle = (ConstantMethodHandle) constantArg;
            MethodInfo methodInfo = getMethodFromConstantMethodHandle(constantMethodHandle, javaClass);
            if (methodInfo != null) {
                return methodInfo;
            }
        }

        return null;
    }

    /**
     * 根据ConstantMethodHandle获得Method对象
     *
     * @param constantMethodHandle
     * @param javaClass
     * @return
     */
    public static MethodInfo getMethodFromConstantMethodHandle(ConstantMethodHandle constantMethodHandle, JavaClass javaClass) {
        ConstantPool constantPool = javaClass.getConstantPool();

        Constant constantCP = constantPool.getConstant(constantMethodHandle.getReferenceIndex());
        if (!(constantCP instanceof ConstantCP)) {
            System.err.println("### 不满足instanceof ConstantCP " + constantCP.getClass().getName());
            return null;
        }

        ConstantCP constantClassAndMethod = (ConstantCP) constantCP;
        String className = constantPool.getConstantString(constantClassAndMethod.getClassIndex(), Const.CONSTANT_Class);
        className = Utility.compactClassName(className, false);

        Constant constantNAT = constantPool.getConstant(constantClassAndMethod.getNameAndTypeIndex());
        if (!(constantNAT instanceof ConstantNameAndType)) {
            System.err.println("### 不满足instanceof ConstantNameAndType " + constantNAT.getClass().getName());
            return null;
        }
        ConstantNameAndType constantNameAndType = (ConstantNameAndType) constantNAT;
        String methodName = constantPool.constantToString(constantNameAndType.getNameIndex(), Const.CONSTANT_Utf8);
        String methodArgs = constantPool.constantToString(constantNameAndType.getSignatureIndex(), Const.CONSTANT_Utf8);

        if (methodName != null && methodArgs != null) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setClassName(className);
            methodInfo.setMethodName(methodName);
            Type[] types = Type.getArgumentTypes(methodArgs);
            methodInfo.setArgumentTypes(types);
            return methodInfo;
        }

        System.err.println("### 获取方法信息失败 " + javaClass.getClassName() + " " + className + " " + methodName + " " + methodArgs);
        return null;
    }

    private CcSupport() {
        throw new IllegalStateException("illegal");
    }

    public static void printMethod(MethodGen mg, InvokeInstruction i, int sourceLine) {
        ConstantPoolGen cp = mg.getConstantPool();
        String referenceTypeName = i.getReferenceType(cp).toString();
        String invokeMethodName = i.getMethodName(cp);
        String argumentList = CcSupport.argumentList(i.getArgumentTypes(cp));

        String className = mg.getClassName();
        String name = mg.getName();
        Type[] argumentTypes = mg.getArgumentTypes();

        System.out.printf("%d  %s#%s%s  ==>  %s#%s%s%n",
                sourceLine,
                className, name, CcSupport.argumentList(argumentTypes),
                referenceTypeName, invokeMethodName, argumentList);
    }


    static String getMethodSig(String className, String methodName, Type... argTypes) {
        String argumentList = CcSupport.argumentList(argTypes);
        return String.format("%s#%s%s", className, methodName, argumentList);
    }

    static Type[] convertType(Class[] argTypes) {
        // 转换到bcel的type
        return Arrays.stream(argTypes).map(Type::getType).toArray(Type[]::new);
    }

    static Method findMethod(JavaClass javaClass, String methodName, Class... argTypes) throws NoSuchMethodException {
        return findMethod(javaClass, methodName, convertType(argTypes));
    }

    static Method findMethod(JavaClass javaClass, String methodName, Type... argTypes) throws NoSuchMethodException {
        Method[] methods = javaClass.getMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Type[] argumentTypes = method.getArgumentTypes();
                if (Arrays.deepEquals(argTypes, argumentTypes)) {
                    return method;
                }
            }
        }

        throw new NoSuchMethodException("方法不存在");
    }

    public static void printChainMap(Map<ClassAndMethod, CallInfo> chainMap) {
        printChainMap(chainMap, ci -> !isEmptyInitCallInfo(ci));
    }

    public static void printChainMap(Map<ClassAndMethod, CallInfo> chainMap, Predicate<CallInfo> printFilter) {
        for (Map.Entry<ClassAndMethod, CallInfo> entry : chainMap.entrySet()) {
            printCallInfo(entry.getValue(), printFilter);
            System.out.println("\n\n");
        }
    }

    public static void printCallInfo(CallInfo callInfo) {
        printCallInfo(callInfo, ci -> !isEmptyInitCallInfo(ci));
    }

    public static void printCallInfo(CallInfo callInfo, Predicate<CallInfo> printFilter) {
        Stack<DeepWrapper<CallInfo>> stack = new Stack<>();
        stack.push(new DeepWrapper<>(1, callInfo));
        Map<String, Integer> printCount = new HashMap<>();

        while (!stack.isEmpty()) {
            DeepWrapper<CallInfo> tree = stack.pop();
            CallInfo ci = tree.value;

            String methodSig = ci.getCallerSignature();
            printCount.put(methodSig, printCount.getOrDefault(ci.getCaller().getMethodInfo().getMethodSignature(), 0) + 1);

            //先往栈中压入右节点，再压左节点，这样出栈就是先左节点后右节点了。
            if (printCount.getOrDefault(methodSig, 0) < 2) {
                for (int i = ci.getCalleeList().size() - 1; i >= 0; i--) {
                    CallInfo child = ci.getCalleeList().get(i);
                    if (!printFilter.test(child)) {
                        continue;
                    }
                    stack.push(new DeepWrapper<>(tree.deep + 1, child));
                }
            }


            // print
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < tree.deep; i++) {
                sb.append("    ");
            }
            System.out.printf("%5d %s%s\n", ci.getCaller().getLineNumber(), sb, ci.getCaller().getMethodInfo().getMethodSignature());
        }
    }

    public static boolean isEmptyInitCallInfo(CallInfo callInfo) {
        if (METHOD_NAME_INIT.equals(callInfo.getCaller().getMethodInfo().getMethodName())) {
            List<CallInfo> calleeList = callInfo.getCalleeList();
            if (CollUtil.isEmpty(calleeList)) {
                return true;
            } else {
                for (CallInfo info : calleeList) {
                    if (!isEmptyInitCallInfo(info)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    static String fixClassFileName(String className) {
        String classFileName;
        if (StrUtil.endWithIgnoreCase(className, ".class")) {
            classFileName = className;
        } else {
            classFileName = className.replaceAll("\\.", "/") + ".class";
        }
        return classFileName;
    }

    public static List<JavaClass> getImplClassList(ListableClassPathRepository repository, String interfaceName) throws ClassNotFoundException {
        JavaClass javaClass = repository.loadClass(interfaceName);
        return repository.scanClasses(jc -> {
            // 排除自身
            if (jc.getClassName().equals(interfaceName)) {
                return false;
            }

            // 获取所有实现的接口，进行比对
            try {
                return jc.implementationOf(javaClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    public static Method getMethod(JavaClass javaClass, String methodName, Type[] argumentTypes) {
        for (Method m : javaClass.getMethods()) {
            if (m.getName().equals(methodName) && argumentList(argumentTypes).equals(argumentList(m.getArgumentTypes()))) {
                return m;
            }
        }

        return null;
    }

    public static boolean implementationOf(JavaClass jc, JavaClass inter) {
        if (!inter.isInterface()) {
            throw new IllegalArgumentException(inter.getClassName() + " is no interface");
        }
        if (inter.equals(jc)) {
            return true;
        }

        final JavaClass[] super_interfaces = getAllInterfaces(jc);
        for (final JavaClass super_interface : super_interfaces) {
            if (super_interface.equals(inter)) {
                return true;
            }
        }

        return false;
    }

    private static JavaClass[] getAllInterfaces(JavaClass jc) {
        final ClassQueue queue = new ClassQueue();
        final Set<JavaClass> allInterfaces = new TreeSet<>();
        queue.enqueue(jc);
        while (!queue.empty()) {
            final JavaClass clazz = queue.dequeue();
            if (clazz == null) {
                continue;
            }
            final JavaClass souper = getSuperClass(clazz);
            final JavaClass[] _interfaces = getInterfaces(clazz);
            if (clazz.isInterface()) {
                allInterfaces.add(clazz);
            } else {
                if (souper != null) {
                    queue.enqueue(souper);
                }
            }
            for (final JavaClass _interface : _interfaces) {
                queue.enqueue(_interface);
            }
        }
        return allInterfaces.toArray(new JavaClass[allInterfaces.size()]);
    }

    private static  JavaClass getSuperClass(JavaClass jc) {
        if ("java.lang.Object".equals(jc.getClassName())) {
            return null;
        }

        return loadClassMayNull(jc.getRepository(), jc.getSuperclassName());

    }

    public static JavaClass[] getInterfaces(JavaClass jc)  {
        final String[] _interfaces = jc.getInterfaceNames();
        final JavaClass[] classes = new JavaClass[_interfaces.length];
        for (int i = 0; i < _interfaces.length; i++) {
            classes[i] = loadClassMayNull(jc.getRepository(), _interfaces[i]);
        }
        return classes;
    }


    public static JavaClass loadClassMayNull(org.apache.bcel.util.Repository repository, String classname) {
        try {
            return repository.loadClass(classname);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static class DeepWrapper<T> {
        int deep;
        T value;

        DeepWrapper(int deep, T value) {
            this.deep = deep;
            this.value = value;
        }
    }

}