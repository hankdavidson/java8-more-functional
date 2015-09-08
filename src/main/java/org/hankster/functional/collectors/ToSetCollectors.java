package org.hankster.functional.collectors;

import java.util.*;
import java.util.stream.Collector;

/**
 * Additional collectors not found in java.util.stream.Collectors
 */
public interface ToSetCollectors {

    /**
     * An alternative to Collectors.toSet() that lets you specify the size of Sets to be used to collect results.
     * @param initialCapacity the number of items you want the set to be able to hold before reallocating
     * @param <T> Set type
     * @return a Set<T>
     */
    static<T> Collector<T,?,Set<T>> toSet(int initialCapacity){
        return CollectorHelpers.toSizedCollection(initialCapacity, HashSet::new);
    }

    /**
     * Returns a Set<T> containing the unique contents of the upstream, in stable order.
     * If you need to iterate across the Set, this should perform better than the HashMap returned
     * by Collectors.toSet(), since LinkedHashSet iterates size() times, while HashSet iterations follow the
     * allocated capacity of the hash table, which is always greater than the number of elements in it.
     * @param <T> type of element in the Set.
     * @return a LinkedHashSet
     */
    static <T> Collector<T, ?, Set<T>> toLinkedHashSet(){
        return CollectorHelpers.toStableOrderCollection(LinkedHashSet::new);
    }

    static <T> Collector<T, ?, Set<T>> toLinkedHashSet(int initialCapacity){
        return CollectorHelpers.toSizedStableOrderCollection(initialCapacity, LinkedHashSet::new);
    }

    static Collector<String, ?, NavigableSet<String>> toCaseInsensitiveSet(){
        return CollectorHelpers.toStableOrderCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Returns an EnumSet<T> containing the set of enum values from the upstream. When enums are involved, EnumSets provide
     * much better performance (O(1)) than other set types.
     * @param enumClass the class of the enum type
     * @param <T> an enum type
     * @return a Set of enum values.
     */
    static<T extends Enum<T>> Collector<T,?,Set<T>> toEnumSet(Class<T> enumClass){
        return CollectorHelpers.toStableOrderCollection(() -> EnumSet.noneOf(enumClass));
    }

    static<T extends Comparable<T>> Collector<T,?,NavigableSet<T>> toNaturalOrderSet(){
        return CollectorHelpers.toStableOrderCollection(TreeSet::new);
    }

    static<T> Collector<T,?,NavigableSet<T>> toSortedSet(Comparator<T> comparator){
        return CollectorHelpers.toStableOrderCollection(() -> new TreeSet<>(comparator));
    }
}