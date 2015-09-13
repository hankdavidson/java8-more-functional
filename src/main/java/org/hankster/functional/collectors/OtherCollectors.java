package org.hankster.functional.collectors;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Some useful collectors not found in {@link ToListCollectors}, {@link ToSetCollectors}, etc. including collectors that stream to
 * files and to Streams.
 */
public interface OtherCollectors {

    /**
     * Returns a Stream with the results of the upstream.
     * Buffering to an {@link ArrayList} of default size is slower than this because of reallocs.  This uses a {@link Stream.Builder} to
     * buffer results from one stream, then returns a {@link Stream} of its contents. When {@link Stream.Builder} reallocs, it adds additional
     * buffers to a "spine", leaving existing data where it is.  Each spine is twice as big as the previous spine, so it
     * scales up kind of like {@link ArrayList}, but without moving existing data from the old array to the new one.  If you find
     * yourself reading in a bunch of stuff with a stream into memory then writing it all out with a stream, this may be a good alternative.
     * @param <T> Stream type
     * @return a Stream of type T
     */
    static<T> Collector<T, Stream.Builder<T>, Stream<T>> toStream(){
        return Collector.of(
                Stream::builder,                                            // creates the Stream.Builder
                Stream.Builder::accept,                                     // adds items from the upstream
                (sb1, sb2) -> {
                    sb2.build().forEach(sb1);                               // a Stream.Builder is a Consumer, so pour sb2 into sb1
                    return sb1;
                },
                Stream.Builder::build);                                     // Finisher converts Stream.Builder to a Stream
    }

    /**
     * Convenient wrapper for {@link FileCollector}, that writes each string element to the specified file as lines}.
     * The encoding it uses to write is UTF-8. The file is closed upon completion.
     * @param dest file to write
     * @param options options for opening the file
     * @throws UncheckedIOException that wraps any {@link IOException} thrown during file operations.
     * @return a collector that collects Strings to the specified file
     */
    static Collector<String, BufferedWriter, Path> toFile(Path dest, OpenOption...options){
        return new FileCollector(dest, StandardCharsets.UTF_8, options);
    }

    /**
     * A collector that pours the upstream results into a single collection that you specify.
     * @param existingCollection the collection to put results into
     * @param <T> the type of items in the stream and resulting collection
     * @param <C> the collection type
     * @return a collector that pours results into a collection you specify
     */
    static<T, C extends Collection<T>> Collector<T,?,C> toExisting(C existingCollection){
        return Collector.of(()->existingCollection, Collection::add, (a,b)->a, Collector.Characteristics.IDENTITY_FINISH, Collector.Characteristics.UNORDERED);
    }
}
