package org.hankster.functional.collectors;

import org.junit.Test;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;

public class CollectorHelpersTest {

        @Test
        public void testToCollection() throws Exception {
            // given the data
            Supplier<Set<String>> setSupplier = LinkedHashSet::new;
            Stream<String> stringStream = Stream.of("3", "1", "1", "2");
            Collector.Characteristics characteristics = Collector.Characteristics.IDENTITY_FINISH;

            // when the collector is tested
            Collector<String, ?, Set<String>> collector = CollectorHelpers.toCollection(setSupplier, characteristics);
            Set<String> set = stringStream.collect(collector);

            // collector returns a set
            assertNotNull(set);

            // collector has the supplied characteristics
            assertTrue(collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH));
            assertFalse(collector.characteristics().contains(Collector.Characteristics.UNORDERED));

            // no data is lost
            assertThat(set.size(), is(3));
            assertThat(set,hasItems("3", "1", "2"));

            // the collection exhibits the characteristics of the supplied Set implementation
            assertTrue(set instanceof LinkedHashSet);
            String[] strings = set.toArray(new String[3]);
            assertThat(strings[0], is("3"));
            assertThat(strings[1], is("1"));
            assertThat(strings[2], is("2"));
        }

    @Test
    public void testToUnorderedCollection() throws Exception {
        // given the data
        Supplier<Set<String>> setSupplier = HashSet::new;
        Stream<String> stringStream = Stream.of("3", "1", "1", "2");

        // when the collector is tested
        Collector<String, ?, Set<String>> collector = CollectorHelpers.toUnorderedCollection(setSupplier);
        Set<String> set = stringStream.collect(collector);

        // collector returns a set
        assertNotNull(set);

        // collector has the supplied characteristics
        assertTrue(collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH));
        assertTrue(collector.characteristics().contains(Collector.Characteristics.UNORDERED));

        // no data is lost
        assertThat(set.size(), is(3));
        assertThat(set,hasItems("3", "1", "2"));

        // the collection exhibits the characteristics of the supplied Set implementation
        assertTrue(set instanceof HashSet);
    }

    @Test
    public void testToStableOrderCollection() throws Exception {
        // given the data
        Supplier<Set<String>> setSupplier = LinkedHashSet::new;
        Stream<String> stringStream = Stream.of("3", "1", "1", "2");

        // when the collector is tested
        Collector<String, ?, Set<String>> collector = CollectorHelpers.toStableOrderCollection(setSupplier);
        Set<String> set = stringStream.collect(collector);

        // collector returns a set
        assertNotNull(set);

        // collector has the supplied characteristics
        assertTrue(collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH));
        assertFalse(collector.characteristics().contains(Collector.Characteristics.UNORDERED));

        // no data is lost
        assertThat(set.size(), is(3));
        assertThat(set,hasItems("3", "1", "2"));

        // the collection exhibits the behavior of the supplied Set implementation
        assertTrue(set instanceof LinkedHashSet);
        String[] strings = set.toArray(new String[3]);
        assertThat(strings[0], is("3"));
        assertThat(strings[1], is("1"));
        assertThat(strings[2], is("2"));
    }

    @Test
    public void testToSizedCollection() throws Exception {
        // given the data
        IntFunction<List<String>> listSupplier = ArrayList::new;
        Stream<String> stringStream = Stream.of("3", "1", "1", "2");
        Collector.Characteristics characteristics = Collector.Characteristics.IDENTITY_FINISH;

        // when the collector is tested
        Collector<String, ?, List<String>> collector = CollectorHelpers.toSizedCollection(1000, listSupplier, characteristics);
        List<String> list = stringStream.collect(collector);

        // collector returns a set
        assertNotNull(list);

        // collector has the supplied characteristics
        assertTrue(collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH));
        assertFalse(collector.characteristics().contains(Collector.Characteristics.UNORDERED));

        // no data is lost
        assertThat(list.size(), is(4));
        assertThat(list,hasItems("3", "1", "2"));

        // the collection exhibits the behavior of the supplied Set implementation
        assertTrue(list instanceof ArrayList);
        String[] strings = list.toArray(new String[4]);
        assertThat(strings[0], is("3"));
        assertThat(strings[1], is("1"));
        assertThat(strings[2], is("1"));
        assertThat(strings[3], is("2"));
    }
}