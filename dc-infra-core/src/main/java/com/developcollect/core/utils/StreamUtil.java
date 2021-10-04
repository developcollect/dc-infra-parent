package com.developcollect.core.utils;


import java.util.Enumeration;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil extends cn.hutool.core.stream.StreamUtil {


    /**
     * 转化Enumeration到Stream
     * 代码来自 https://stackoverflow.com/a/33243700/1441122
     * @param e Enumeration对象
     * @param <T> 泛型类型
     * @return Stream
     */
    public static <T> Stream<T> of(Enumeration<T> e) {
        return StreamSupport.stream(
                new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
                    @Override
                    public boolean tryAdvance(Consumer<? super T> action) {
                        if (e.hasMoreElements()) {
                            action.accept(e.nextElement());
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void forEachRemaining(Consumer<? super T> action) {
                        while (e.hasMoreElements()) {
                            action.accept(e.nextElement());
                        }
                    }
                }, false);
    }



}
