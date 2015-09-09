package org.hankster.functional.collectors;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * {@link BinaryOperator}s used for dealing with duplicate values in some {@link Collector}s
 */
public interface MergeFunctions {

    static <V> BinaryOperator<V> alwaysThrow(){
        return alwaysThrow((oldV,newV) -> "Duplicate keys with values " + oldV + ", " + newV, IllegalStateException::new);
    }

    static <V, R extends RuntimeException> BinaryOperator<V> alwaysThrow(BiFunction<V,V,String> messageProducer, Function<String,R> exceptionFactory){
        return (v1, v2) -> {
            throw exceptionFactory.apply(messageProducer.apply(v1, v2));
        };
    }


}
