package org.hankster.functional.collectors;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Additional collectors for collecting to Lists not found in {@link Collectors}
 */
public interface ToListCollectors {

    /**
     * An alternative to {@link Collectors#toList()}} that lets you specify the size of {@link ArrayList} to be used to collect results.
     * @param initialCapacity the number of items you want the set to be able to hold before reallocating
     * @param <T> Set type
     * @return a Set of type T
     */
    static<T> Collector<T,?,List<T>> toList(int initialCapacity){
        return CollectorHelpers.toSizedStableOrderCollection(initialCapacity, ArrayList::new);
    }
}