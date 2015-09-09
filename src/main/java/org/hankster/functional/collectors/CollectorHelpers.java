package org.hankster.functional.collectors;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.*;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;

/**
 * Some lower-level helper functions used in this library that can be used to create more convenient collectors
 */

public interface CollectorHelpers {

    /**
     * low-level alternative to {@link Collectors#toCollection(Supplier)} that allows you to specify the collector's {@link Characteristics}.
     * {@link Collectors#toCollection(Supplier)} specifies the characteristics {@link Characteristics#IDENTITY_FINISH} and {@link Characteristics#UNORDERED} which are not ideal for
     * some collection types.  This allows specific {@link Characteristics} to be selected.
     * @param factory a no-arg function that returns instances of the collection to put results into. For example, passing
     *                in {@code HashSet::new} will call {@link HashSet}'s no-arg constructor.  May be called more
     *                than once, but it is not required to return a different instance every time.
     * @param characteristics zero or more collection characteristics that can affect performance in some situations
     * @param <T> The type of elements in the collection
     * @param <C> The type of collection to stream to
     * @return A {@link Collection} of type C containing elements of type T.
     */
    static<T, C extends Collection<T>> Collector<T,?,C> toCollection(Supplier<C> factory, Characteristics... characteristics){
        return of(
                factory,
                C::add,
                (c1, c2) -> {
                    c1.addAll(c2);
                    return c1;
                },
                characteristics);
    }

    /**
     * Specialization of toCollection() that does the same thing as Collectors.toCollection, but is here for completeness.
     * @param factory a no-arg function that returns instances of the collection to put results into. May be called more
     *                than once, but it is not required to return a different instance every time.
     * @param <T> The type of elements in the collection
     * @param <C> The type of collection to stream to
     * @return A collection of type C containing elements of type T.
     */
    static<T, C extends Collection<T>> Collector<T,?,C> toUnorderedCollection(Supplier<C> factory){
        return toCollection(factory, IDENTITY_FINISH, UNORDERED);
    }

    /**
     * Specialization of toCollection() adapted for ordered collections.
     * @param factory a no-arg function that returns instances of the collection to put results into. May be called more
     *                than once, but it is not required to return a different instance every time.
     * @param <T> The type of elements in the collection
     * @param <C> The type of collection to stream to
     * @return A collection of type C containing elements of type T.
     */
    static<T, C extends Collection<T>> Collector<T,?,C> toStableOrderCollection(Supplier<C> factory){
        return toCollection(factory, IDENTITY_FINISH);
    }


    /**
     * a variation of toCollection() that allows you to specify the initial size of collections that get created
     * @param initialCapacity the initial capacity to reserve when creating empty collections
     * @param sizedFactory an IntFunction that corresponds to 1-arg collection constructors that take an initial capacity parameter.
     *                     For example, passing in LinkedHashSet::new will call LinkedHashSet's 1-arg constructor.
     * @param characteristics collection characteristics that can affect performance in some situations
     * @param <T> The type of elements in the collection
     * @param <C> The type of collection to stream to
     * @return A collection of type C containing elements of type T.

     */
    static<T, C extends Collection<T>> Collector<T,?,C> toSizedCollection(int initialCapacity, IntFunction<C> sizedFactory, Characteristics... characteristics) {
        return toCollection(() -> sizedFactory.apply(initialCapacity), characteristics);
    }

    /**
     * Specialization of toSizedCollection() adapted for unordered collections.
     * @param initialCapacity the initial capacity to reserve when creating empty collections
     * @param sizedFactory an IntFunction that corresponds to 1-arg collection constructors that take an initial capacity parameter.
     *                     For example, passing in HashSet::new will call HashSet's 1-arg constructor.
     * @param <T> The type of elements in the collection
     * @param <C> The type of collection to stream to
     * @return A collection of type C containing elements of type T.
     */
    static<T, C extends Collection<T>> Collector<T,?,C> toSizedUnorderedCollection(int initialCapacity, IntFunction<C> sizedFactory){
        return toSizedCollection(initialCapacity, sizedFactory, IDENTITY_FINISH, UNORDERED);
    }

    /**
     * Specialization of toSizedCollection() adapted for ordered collections.
     * @param initialCapacity the initial capacity to reserve when creating empty collections
     * @param sizedFactory an IntFunction that corresponds to 1-arg collection constructors that take an initial capacity parameter.
     *                     For example, passing in LinkedHashSet::new will call LinkedHashSet's 1-arg constructor.
     * @param <T> The type of elements in the collection
     * @param <C> The type of collection to stream to
     * @return A collection of type C containing elements of type T.
     */
    static<T, C extends Collection<T>> Collector<T,?,C> toSizedStableOrderCollection(int initialCapacity, IntFunction<C> sizedFactory){
        return toSizedCollection(initialCapacity, sizedFactory, IDENTITY_FINISH);
    }
}
