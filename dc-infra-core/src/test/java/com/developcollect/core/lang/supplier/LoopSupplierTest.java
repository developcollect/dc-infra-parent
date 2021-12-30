package com.developcollect.core.lang.supplier;

import cn.hutool.core.date.StopWatch;
import com.developcollect.core.thread.ThreadUtil;
import org.junit.Test;
import org.junit.rules.Stopwatch;

import java.util.ArrayList;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LoopSupplierTest {

    @Test
    public void test3() {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("A");
        objects.add("B");
        objects.add("C");
        Supplier<String> stringLoopSupplier = new ConcurrentLoopSupplier<String>(objects);
        StopWatch stopWatch = StopWatch.create("33");
        stopWatch.start();
        for (int i = 0; i < 1000009999; i++) {
            stringLoopSupplier.get();
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());

    }
}