package com.developcollect.core.lang.supplier;

import org.junit.Test;

import java.util.List;

public class ElementsSupplierTest {


    @Test
    public void test() {
        ElementsSupplier<String> supplier = new ArrayLoopSupplier<>(new String[]{"A"});
        List<String> elements = supplier.elements();

        FallbackChainSupplierWrapper<String> supplier1 = new FallbackChainSupplierWrapper<>(supplier);
//        ElementsSupplier<String> es2 = supplier1;  编译错误
        ElementsSupplier<List<String>> es3 = supplier1;
        List<List<String>> elements2 = es3.elements();
        List<String> elements1 = supplier1.elements();

    }

}