package org.hankster.functional.collectors;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * {@link BinaryOperator}s used for dealing with duplicate values in some {@link Collector}s
 */
public interface MergeFunctions {

    /**
     * A merge function, useful in Collectors.toMap and similar collectors to deal with non-unique key values.  This
     * merge function throws IllegalStateException if keys are duplicated.
     * @param <V> value type, ignored
     * @return a merge function that always throws IllegalStateException with a canned message
     */
    static <V> BinaryOperator<V> alwaysThrow(){
        return alwaysThrow((oldV,newV) -> "Duplicate keys with values " + oldV + ", " + newV, IllegalStateException::new);
    }

    /**
     * A merge function that throws an exception when a collector encounters duplicate keys.  Rather than merging two
     * values for the same key, the strategy here is to throw an exception
     * @param messageProducer A BiFunction that produces the error message to throw.  The function is passed the two
     *                        values that map to the same key in case you want to use them in the error message
     * @param exceptionFactory A Function that takes an error message string and returns a RuntimeException or a subtype
     * @param <V> the type of values that mapped to the same key
     * @param <R> the RuntimeException type
     * @return a merge function that always throws an exception you specify with an error message you specify.
     */
    static <V, R extends RuntimeException> BinaryOperator<V> alwaysThrow(BiFunction<V,V,String> messageProducer, Function<String,R> exceptionFactory){
        return (v1, v2) -> {
            throw exceptionFactory.apply(messageProducer.apply(v1, v2));
        };
    }


}
