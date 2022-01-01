package com.developcollect.core.lang.supplier;

import com.developcollect.core.Statistic;
import org.junit.Test;

public class WeightRandomSupplierTest {

    @Test
    public void test() {
        WeightRandomSupplier<String> weightRandomSupplier = new WeightRandomSupplier<>();
        weightRandomSupplier.addElement(0.1, "A");
        weightRandomSupplier.addElement(0.2, "B");
        weightRandomSupplier.addElement(0.2, "C");
        weightRandomSupplier.addElement(0.4, "D");
        weightRandomSupplier.addElement(0.1, "E");

        Statistic statistic = new Statistic();
        statistic.start();
        for (int i = 0; i < 1000000; i++) {
            statistic.add(weightRandomSupplier.get());
        }
        statistic.stop();

        System.out.println(statistic.report());
    }
}