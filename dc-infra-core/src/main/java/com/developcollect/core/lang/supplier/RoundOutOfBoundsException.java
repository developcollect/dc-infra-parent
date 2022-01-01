package com.developcollect.core.lang.supplier;

public class RoundOutOfBoundsException extends RuntimeException {

    public RoundOutOfBoundsException(long rounds, long maxRounds) {
        super("轮次已超出：当前轮次:" + rounds + "  最大轮次:" + maxRounds);
    }
}
