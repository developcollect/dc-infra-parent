package com.developcollect.extra.javacc;

import cn.hutool.core.util.StrUtil;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 */

class CcInnerUtil {


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

    public static List<String> genImplClassMethodWithArgs(Method[] methods) {
        List<String> methodInfoList = new ArrayList<>(methods.length);
        for (Method method : methods) {
            String methodName = method.getName();
            // 忽略"<init>"和"<clinit>"方法
            if (!methodName.startsWith("<") && method.isPublic() && !method.isAbstract() && !method.isStatic()) {
                methodInfoList.add(methodName + CcInnerUtil.argumentList(method.getArgumentTypes()));
            }
        }
        return methodInfoList;
    }

    public static List<String> genInterfaceAbstractMethodWithArgs(Method[] methods) {
        List<String> methodInfoList = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (method.isAbstract()) {
                methodInfoList.add(method.getName() + CcInnerUtil.argumentList(method.getArgumentTypes()));
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
                return CcInnerUtil.getFuncStartSourceLine(method);
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

    private CcInnerUtil() {
        throw new IllegalStateException("illegal");
    }

    public static void printMethod(MethodGen mg, InvokeInstruction i, int sourceLine) {
        ConstantPoolGen cp = mg.getConstantPool();
        String referenceTypeName = i.getReferenceType(cp).toString();
        String invokeMethodName = i.getMethodName(cp);
        String argumentList = CcInnerUtil.argumentList(i.getArgumentTypes(cp));

        String className = mg.getClassName();
        String name = mg.getName();
        Type[] argumentTypes = mg.getArgumentTypes();

        System.out.printf("%d  %s#%s%s  ==>  %s#%s%s%n",
                sourceLine,
                className, name, CcInnerUtil.argumentList(argumentTypes),
                referenceTypeName, invokeMethodName, argumentList);
    }


    static String getMethodSig(String className, String methodName, Type... argTypes) {
        String argumentList = CcInnerUtil.argumentList(argTypes);
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

    public static void printCallInfo(CallInfo callInfo) {
        Stack<DeepWrapper<CallInfo>> stack = new Stack<>();
        stack.push(new DeepWrapper<>(1, callInfo));
        Map<String, Integer> printCount = new HashMap<>();

        while (!stack.isEmpty()) {
            DeepWrapper<CallInfo> tree = stack.pop();
            CallInfo ci = tree.value;
            String methodSig = ci.getCaller().getMethodInfo().getMethodSig();
            printCount.put(methodSig, printCount.getOrDefault(ci.getCaller().getMethodInfo().getMethodSig(), 0) + 1);

            //先往栈中压入右节点，再压左节点，这样出栈就是先左节点后右节点了。
            if (printCount.getOrDefault(methodSig, 0) < 2) {
                for (int i = ci.getCalleeList().size() - 1; i >= 0; i--) {
                    CallInfo child = ci.getCalleeList().get(i);
                    stack.push(new DeepWrapper<>(tree.deep + 1, child));
                }
            }


            // print
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < tree.deep; i++) {
                sb.append("    ");
            }
            System.out.printf("%5d %s%s\n", ci.getCaller().getLineNumber(), sb, ci.getCaller().getMethodInfo().getMethodSig());
        }
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



    private static class DeepWrapper<T> {
        int deep;
        T value;

        DeepWrapper(int deep, T value) {
            this.deep = deep;
            this.value = value;
        }
    }

}