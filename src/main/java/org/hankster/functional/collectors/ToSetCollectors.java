package org.hankster.functional.collectors;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

/**
 * Additional collectors for collecting to Sets not found in {@link Collectors}
 */
public interface ToSetCollectors {

    /**
     * An alternative to {@link Collectors#toSet()} that lets you specify the size of Sets to be used to collect results.
     * Like {@link Collectors#toSet()}, this returns an unordered Set.
     * @param initialCapacity the number of items you want the set to be able to hold before reallocating
     * @param <T> type of elements in the Set.
     * @return a Set of type T
     */
    static<T> Collector<T,?,Set<T>> toSet(int initialCapacity){
        return CollectorHelpers.toSizedUnorderedCollection(initialCapacity, (IntFunction<Set<T>>) HashSet::new);
    }

    /**
     * Returns a Set containing the unique contents of the upstream, in encounter order.
     * If you need to iterate across the Set, this should perform better than the HashMap returned
     * by {@link Collectors#toSet()}.
     * @param <T> type of elements in the Set.
     * @return a LinkedHashSet of type T
     */
    static <T> Collector<T, ?, Set<T>> toLinkedHashSet(){
        return CollectorHelpers.toStableOrderCollection(LinkedHashSet::new);
    }

    /**
     * Returns a Set containing the unique contents of the upstream, in encounter order, allowing the caller to
     * specify the initial size of collections allocated during collection.
     * @param initialCapacity the number of items you want the set to be able to hold before reallocating
     * @param <T> type of elements in the Set.
     * @return a LinkedHashSet of type T
     */
    static <T> Collector<T, ?, Set<T>> toLinkedHashSet(int initialCapacity){
        return CollectorHelpers.toSizedStableOrderCollection(initialCapacity, LinkedHashSet::new);
    }

    /**
     * Creates a set of unique Strings where strings that differ only by case are treated as equivalent
     * @return a NavigableSet of Strings
     */
    static Collector<String, ?, NavigableSet<String>> toCaseInsensitiveSet(){
        return toSortedSet(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Creates an EnumSet containing the set of enum values from the upstream. When enums are involved, EnumSets provide
     * much better performance (O(1)) than other set types.
     * @param enumClass the class of the enum type
     * @param <T> an enum type
     * @return a Set of enum values.
     */
    static<T extends Enum<T>> Collector<T,?,Set<T>> toEnumSet(Class<T> enumClass){
        return CollectorHelpers.toStableOrderCollection(() -> EnumSet.noneOf(enumClass));
    }

    /**
     * for Streams of values that implement {@link Comparable}, this will return a unique set of values from the stream,
     * sorted by their "natural order" (the order defined by their implementation of {@link Comparable#compareTo(Object)})
     * @param <T> type of elements in the set
     * @return a NavigableSet
     */
    static<T extends Comparable<T>> Collector<T,?,NavigableSet<T>> toNaturalOrderSet(){
        return CollectorHelpers.toStableOrderCollection(TreeSet::new);
    }

    /**
     * Returns a set of non-duplicate values (where duplicate is defined by two values that return "0" when the given comparator's
     * compare() function is called), sorted according to the values returned by the given {@link Comparator}'s compare() function.
     * @param comparator a {@link Comparator} that defines the uniqueness of values (and the ordering) in the Set
     * @param <T> type of elements in the set
     * @return a NavigableSet
     */
    static<T> Collector<T,?,NavigableSet<T>> toSortedSet(Comparator<T> comparator){
        return CollectorHelpers.toStableOrderCollection(() -> new TreeSet<>(comparator));
    }
}