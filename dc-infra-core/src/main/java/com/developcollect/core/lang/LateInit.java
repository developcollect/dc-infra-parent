package com.developcollect.core.lang;

import java.util.function.Supplier;

/**
 * 延迟创建对象
 * @param <T>
 */
public class LateInit<T> implements AutoCloseable {

    private final Supplier<T> supplier;
    private final Object lock = new Object();
    private volatile T ref;

    public LateInit(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (ref == null) {
            synchronized (lock) {
                if (ref == null) {
                    ref = supplier.get();
                    if (ref == null) {
                        throw new IllegalArgumentException("supplier return null!");
                    }
                }
            }
        }

        return ref;
    }

    /**
     * 是否已初始化
     */
    public boolean inited() {
        return ref != null;
    }

    @Override
    public void close() throws Exception {
        if (ref instanceof AutoCloseable) {
            ((AutoCloseable) ref).close();
        }
    }
}
