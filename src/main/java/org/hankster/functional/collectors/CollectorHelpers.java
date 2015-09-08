package org.hankster.functional.collectors;

import java.util.Collection;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public interface CollectorHelpers {

    static<T, C extends Collection<T>> Collector<T,?,C> toSizedCollection(int initialCapacity, IntFunction<C> sizedFactory){
        return Collectors.toCollection(() -> sizedFactory.apply(initialCapacity));      // toCollection() assumes UNORDERED and IDENTITY_FINISH
    }

    static<T, C extends Collection<T>> Collector<T,?,C> toStableOrderCollection(Supplier<C> factory){
        return Collector.of(
                factory,
                C::add,
                (c1, c2) -> {
                    c1.addAll(c2);
                    return c1;
                },
                IDENTITY_FINISH);
    }

    static<T, C extends Collection<T>> Collector<T,?,C> toSizedStableOrderCollection(int initialCapacity, IntFunction<C> sizedFactory){
        return toStableOrderCollection(() -> sizedFactory.apply(initialCapacity));
    }
}
