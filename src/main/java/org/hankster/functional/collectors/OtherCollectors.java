package org.hankster.functional.collectors;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collector;
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

    static Collector<String, BufferedWriter, Path> toFile(Path dest, OpenOption...options){
        return new FileCollector(dest, StandardCharsets.UTF_8, options);
    }

    static Collector<String, BufferedWriter, Path> toFile(Path dest, Charset cs, OpenOption...options){
        return new FileCollector(dest, cs, options);
    }

}
