package org.hankster.functional.collectors;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;


public class ToSetCollectorsTest {

    @Test
    public void testToLinkedHashSet() throws Exception {
        // given the data
        Stream<String> stringStream = Stream.of("3", "1", "1", "2");

        // when the collector is tested
        Collector<String, ?, Set<String>> collector = ToSetCollectors.toLinkedHashSet();
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
    public void testToCaseInsensitiveSet() throws Exception {
        // given the data
        Stream<String> stringStream = Stream.of("b", "B", "A", "a", "c", "D");

        // when the collector is tested
        Collector<String, ?, NavigableSet<String>> collector = ToSetCollectors.toCaseInsensitiveSet();
        Set<String> set = stringStream.collect(collector);

        // collector returns a set
        assertNotNull(set);

        // collector has the supplied characteristics
        assertTrue(collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH));
        assertFalse(collector.characteristics().contains(Collector.Characteristics.UNORDERED));

        // no data is lost
        assertThat(set.size(), is(4));
        assertThat(set,hasItems("a", "A", "b", "B", "c", "C", "d", "D"));

        // the collection exhibits the behavior of the supplied Set implementation
        assertTrue(set instanceof TreeSet);
        String[] strings = set.toArray(new String[4]);
        assertThat(strings[0], is("A"));
        assertThat(strings[1], is("b"));
        assertThat(strings[2], is("c"));
        assertThat(strings[3], is("D"));
    }

    enum ABC { A,B,C,D,E,F };
    @Test
    public void testToEnumSet() throws Exception {
        // given the data
        Stream<ABC> abcStream = Stream.of(ABC.A, ABC.C, ABC.D, ABC.F);

        // when the collector is tested
        Collector<ABC, ?, EnumSet<ABC>> collector = ToSetCollectors.toEnumSet(ABC.class);
        Set<ABC> set = abcStream.collect(collector);

        // collector returns a set
        assertNotNull(set);

        // collector has the supplied characteristics
        assertTrue(collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH));
        assertTrue(collector.characteristics().contains(Collector.Characteristics.UNORDERED));

        // no data is lost
        assertThat(set.size(), is(4));
        assertThat(set,hasItems(ABC.A, ABC.C, ABC.D, ABC.F));

        // the collection exhibits the behavior of the supplied Set implementation
        assertTrue(set instanceof EnumSet);
        ABC[] abcs = set.toArray(new ABC[4]);
        assertThat(abcs[0], is(ABC.A));
        assertThat(abcs[1], is(ABC.C));
        assertThat(abcs[2], is(ABC.D));
        assertThat(abcs[3], is(ABC.F));
    }

    @Test
    public void testToNaturalOrderSet() throws Exception {

    }

    @Test
    public void testToSortedSet() throws Exception {

    }
}