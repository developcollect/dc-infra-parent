package com.developcollect.core.lang.weight;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权重包装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeightObj<T> {

    private double weight;
    private T obj;


    public static <T> WeightObj<T> of(double weight, T obj) {
        return new WeightObj<>(weight, obj);
    }
}
