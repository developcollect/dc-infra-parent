package com.developcollect.extra.javacc;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.util.Objects;

@Data
@AllArgsConstructor
public class ClassAndMethod {

    private JavaClass javaClass;
    private Method method;


    @Override
    public int hashCode() {
        return Objects.hash(javaClass, method);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassAndMethod that = (ClassAndMethod) o;
        return Objects.equals(javaClass, that.javaClass)
                && Objects.equals(method, that.method);
    }

    @Override
    public String toString() {
        return String.format("%s#%s%s", javaClass.getClassName(), method.getName(), CcSupport.argumentList(method.getArgumentTypes()));
    }


}
