package com.developcollect.core.lang.supplier;


/**
 * 从给定的数组中提供元素，数组元素提供完了之后再从头开始，周而复始，无穷无尽
 *
 * @param <T> 元素类型
 */
public interface LoopSupplier<T> extends ResettableSupplier<T> {

    /**
     * 每次调用时从数组中按顺序获取一个元素
     *
     * @throws RoundOutOfBoundsException 如果指定了maxRounds，并且当前rounds超出了maxRounds便会抛出
     */
    @Override
    T get();

    /**
     * 当前循环的轮次，从1开始
     *
     * @return 循环的轮数
     */
    long rounds();

    /**
     * 设置一个最大循环轮次
     * 在最大轮次循环完毕后，在尝试获取元素，将会抛出异常
     * 如果不设置，则默认不限制最大轮次
     *
     * @param maxRounds
     */
    void setMaxRounds(long maxRounds);


    /**
     * 是否能继续提供元素
     */
    boolean hasNext();
}
