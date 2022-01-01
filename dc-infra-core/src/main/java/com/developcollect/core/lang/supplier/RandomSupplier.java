package com.developcollect.core.lang.supplier;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSupplier<T> implements ElementsSupplier<T> {

    protected List<T> elements;
    protected Random random = ThreadLocalRandom.current();


    public RandomSupplier(List<T> elements) {
        this.elements = elements;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public List<T> elements() {
        return null;
    }

    @Override
    public T get() {
        if (elements.isEmpty()) {
            return null;
        }
        return elements.get(random.nextInt(elements.size()));
    }
}
