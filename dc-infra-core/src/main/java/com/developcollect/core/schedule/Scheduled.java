package com.developcollect.core.schedule;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zak
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {


    /**
     * Execute the annotated method with a fixed period between the end of the
     * last invocation and the start of the next.
     * <p>The time unit is milliseconds by default but can be overridden via
     * {@link #timeUnit}.
     *
     * @return the delay
     */
    long fixedDelay() default -1;

    /**
     * Execute the annotated method with a fixed period between invocations.
     * <p>The time unit is milliseconds by default but can be overridden via
     * {@link #timeUnit}.
     *
     * @return the period
     */
    long fixedRate() default -1;

    /**
     * Number of units of time to delay before the first execution of a
     * {@link #fixedRate} or {@link #fixedDelay} task.
     * <p>The time unit is milliseconds by default but can be overridden via
     * {@link #timeUnit}.
     *
     * @return the initial
     * @since 3.2
     */
    long initialDelay() default -1;


    /**
     * The {@link TimeUnit} to use for {@link #fixedDelay},
     * {@link #fixedRate}, {@link #initialDelay}.
     * <p>Defaults to {@link TimeUnit#MILLISECONDS}.
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
