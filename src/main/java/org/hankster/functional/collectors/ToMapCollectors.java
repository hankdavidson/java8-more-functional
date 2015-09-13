package org.hankster.functional.collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static org.hankster.functional.collectors.MergeFunctions.*;

/**
 * Additional collectors for collecting to Maps not found in {@link Collectors}
 */
public class ToMapCollectors {

    /**
     * A collector that, like Collectors.groupingBy(), produces a map of keys to collected values, but which, like
     * Collectors.toMap(), lets you specify functions for both the key and the values to be collected.  What is done
     * with the collected values is specified by the downstream collector.
     * @param keyExtractor A function on the upstream type that returns the value that will become a key in the map
     * @param collectionMemberExtractor A function on the upstream type that returns a value that is not expected to be
     *                                  unique, so all values that map to the corresponding key are collected in some way
     *                                  (determined by the downstream collector).
     * @param mapSupplier a factory for the type of map to create to put results in
     * @param downstream a Collector that determines what is done with the values that map to the same key
     * @param <T> The upstream type
     * @param <K> The return type of the key extractor -- will be the map's key type
     * @param <R> The type of values that will be collected by the collector
     * @param <V> the type returned by the downstream collector, which will become the values in the map
     * @param <M> The type of map produced by mapSupplier
     * @return A Map that maps the keys produced by keyExtractor to the result of the downstream collector that combines
     *         the values returned by collectionMemberExtractor
     */
    static<T, K, R, V, M extends Map<K,V>> Collector<T,?,M> grouping(Function<T,K> keyExtractor,
                                                                      Function<T,R> collectionMemberExtractor,
                                                                      Supplier<M> mapSupplier,
                                                                      Collector<R, ?, V> downstream){

        return groupingBy(keyExtractor, mapSupplier, mapping(collectionMemberExtractor,downstream));
    }

    /**
     * A specialization of Collectors.toMap() that stores results in an EnumMap giving O(1) performance
     * @param keyExtractor A function that produces a unique Enum key value from the upstream type T
     * @param valueExtractor A function that produces a value from the upstream type T.  If two values map to the same
     *                       key, an IllegalStateException will be thrown.
     * @param enumClass the class of the Enum keys
     * @param <T> The upstream type
     * @param <E> The enum type
     * @param <V> The value type.
     * @return A Map of Enum keys with O(1) performance
     * @throws IllegalStateException if two values map to the same Enum key
     */
    static<T, E extends Enum<E>, V>  Collector<T,?,EnumMap<E,V>>  toEnumMap(
            Function<? super T, ? extends E> keyExtractor,
            Function<? super T, ? extends V> valueExtractor,
            Class<E> enumClass){

        return toMap(keyExtractor, valueExtractor, alwaysThrow(), () -> new EnumMap<>(enumClass));
    }

    /**
     * A specialization of Collectors.groupingBy where they key is an Enum type, resulting in an EnumMap that gives O(1) performance.
     * @param keyExtractor A function that produces a unique Enum key value from the upstream type T
     * @param enumClass the class of the Enum keys
     * @param downstream the collector that combines the non-unique values that map to the keys produced by keyExtractor
     * @param <T> the upstream type
     * @param <K> the key type
     * @param <V> the type returned by the downstream collector, which will become the values in the map
     * @return A map of enum keys to a type determined by the downstream collector which combines the non-unique values
     *         that mapped to the key
     */
    static<T, K extends Enum<K>, V>  Collector<T,?,EnumMap<K,V>>  groupingByEnum (
            Function<? super T, ? extends K> keyExtractor,
            Class<K> enumClass,
            Collector<T, ?, V> downstream) {

        return groupingBy(keyExtractor, () -> new EnumMap<>(enumClass), downstream);
    }

    /**
     * For the rare case where you need to map an Enum to a set of other Enum values (same or different enum class),
     * this will give the best possible performance, because both EnumMap and EnumSet offer O(1) performance
     * @param keyExtractor a function that returns an Enum from the upstream type T
     * @param valueExtractor a function that returns an Enum from the upstream type T
     * @param keyClass the class of the key type
     * @param valueClass the class of the values that will be mapped to the key type
     * @param <T> the upstream type
     * @param <K> they key type
     * @param <R> the type of values in the EnumSets mapped to the keys
     * @return An EnumMap<enum1,EnumSet<enum2>>
     */
    static<T, K extends Enum<K>, R extends Enum<R>>  Collector<T, ?, EnumMap<K, EnumSet<R>>>  mapEnumToEnums (
            Function<? super T, ? extends K> keyExtractor,
            Function<? super T, ? extends R> valueExtractor,
            Class<K> keyClass,
            Class<R> valueClass) {
        return groupingByEnum(keyExtractor, keyClass, mapping(valueExtractor, ToSetCollectors.toEnumSet(valueClass)));
    }

    /**
     * Returns a Map that stores canonical values.
     * list of canonical values.  The comparator should return 0 for all values that should map to the canonical value.
     * So, if T is String and you have a comparator that sees strings with different case as equal, and for which strings
     * with whitespace before or after are seen as equal to trimmed strings in the canonical map, then simply doing a
     * map.get() in this map will always return a trimmed string with the correct case.  Map.containsKey() can be used
     * to see if a value can be canonicalized.
     * @param canonicalizingComparator a Comparator that sees non-canonical equivalents as equal to canonical values.
     *                                 For example, for canonical Strings, using Guava's Ordering and CharMatcher,
<pre>Ordering.from(String.CASE_INSENSITIVE_ORDER).onResultOf(CharMatcher.WHITESPACE::trimFrom);</pre>
                                       will return a map such that if it contained, say, "foo", then map.get("FOO"), \
                                       map.get("   foo") and map.get("\tfoo\t") would all return "foo".
                                       The map can be used to convert a stream of strings to canonical form, e.g.
                                       stream.map(myCanonicalMap::get).  The map can also be used to filter out unknown
                                       (uncanonicalizable) strings, e.g. stream.filter(myCanonicalMap::containsKey).
     * @param <T> the type of values to canonicalize
     * @return the canonicalizing map
     */
    static<T> Collector<T, ?, NavigableMap<T,T>> toCanonicalMap(Comparator<T> canonicalizingComparator) {
        return collectingAndThen(
                toMap(identity(), identity(), alwaysThrow(), (Supplier<NavigableMap<T, T>>) () -> new TreeMap<>(canonicalizingComparator)),
                Collections::unmodifiableNavigableMap);
    }

    /**
     * Takes a stream of Map.Entry<K,V> and reassembles it into a Map<K,V>.  If the stream made any of the keys non-unique,
     * this will throw IllegalStateException.  Handy for filtering maps, e.g.
     * map.entrySet().stream().filter(myfilter).collect(ToMapCollectors.remap())
     * @param <K> map key type
     * @param <V> map value type
     * @return a Map with the contents of the stream.
     * @throws IllegalStateException if the upstream made any of the keys non-unique
     */
    static<K,V> Collector<Map.Entry<K,V>, ?, Map<K,V>> reMap() {
        return toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    /**
     * Collector for collecting to a Guava BiMap (a map of unique values to other unique values that can be inverted)
     * @param keyExtractor A function that supplies the keys (and the values of the inverted map)
     * @param reverseKeyExtractor A function that supplies the values (and the keys of the inverted map)
     * @param <T> The upstream type
     * @param <K1> The map key type
     * @param <K2> The inverted map key type
     * @return a BimMap<K1,K2>
     */
    static<T, K1, K2> Collector<T, ?, BiMap<K1, K2>> toBiMap(Function<T, K1> keyExtractor, Function<T, K2> reverseKeyExtractor){
        return toMap(keyExtractor, reverseKeyExtractor, alwaysThrow(), HashBiMap::create);
    }
}
