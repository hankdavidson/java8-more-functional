package org.hankster.functional.collectors;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface OtherCollectors {
    /**
     * Returns a Stream with the results of the upstream.
     * Buffering to an ArrayList of default size is slower than this because of reallocs.  This uses a Stream.Builder to
     * buffer results from one stream, then returns a Stream of its contents. When Stream.Builder reallocs, it adds additional
     * buffers to a "spine", leaving existing data where it is.  Each spine is twice as big as the previous spine, so it
     * scales up kind of like ArrayList, but without moving existing data from the old array to the new one.  If you find
     * yourself reading in a bunch of stuff with a stream into memory then writing it all out with a stream, this may be a good alternative.
     * @param <T> stream type
     * @return a Stream<T>
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
