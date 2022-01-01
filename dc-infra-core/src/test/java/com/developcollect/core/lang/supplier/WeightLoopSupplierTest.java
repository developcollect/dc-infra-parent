package com.developcollect.core.lang.supplier;

import org.junit.Test;

public class WeightLoopSupplierTest {

    @Test
    public void test() {
        WeightLoopSupplier<String> weightLoopSupplier = new WeightLoopSupplier<>();
        weightLoopSupplier.addElement(1, "A");
        weightLoopSupplier.addElement(5, "E");
        weightLoopSupplier.addElement(3, "C");
        weightLoopSupplier.addElement(4, "D");
        weightLoopSupplier.addElement(2, "B");


        for (int i = 0; i < 32; i++) {
            System.out.println(weightLoopSupplier.get());
        }
    }

}