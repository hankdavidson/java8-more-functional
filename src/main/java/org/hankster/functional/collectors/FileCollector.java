package org.hankster.functional.collectors;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * A {@link Collector} that writes {@link Stream} contents to a file, closing it when the stream is exhausted.
 */

public class FileCollector implements Collector<String, BufferedWriter, Path>, Closeable{
    Path path;
    OutputStream out = null;
    OutputStreamWriter osWriter = null;
    BufferedWriter writer = null;

    /**
     * Creates a Collector that takes the upstream strings and writes the strings as lines to the specified file.  The
     * file is closed upon completion.  If it does not complete, (if, for instance, a RuntimeException is thrown),
     * you will have to call close() on the collector.  The file is created even if the upstream is empty
     * @param path the file to write to
     * @param cs the character set to use
     * @param options 0 or more OpenOption values
     * @throws UncheckedIOException if an IOException is thrown while opening or writing to the file.
     */
    public FileCollector(Path path, Charset cs, OpenOption... options) {
        try {
            this.path = path;
            this.out = Files.newOutputStream(path, options);
            this.osWriter = new OutputStreamWriter(out, cs.newEncoder());
            this.writer = new BufferedWriter(osWriter);
        } catch (IOException e) {
            closeAndThrow(e);
        }
    }

    @Override
    public Supplier<BufferedWriter> supplier() {
        return () -> writer;
    }

    @Override
    public BiConsumer<BufferedWriter, String> accumulator() {
        return (wr, s) -> {
            try {
                wr.write(s);
                wr.newLine();
            } catch (IOException e) {
                closeAndThrow(e);
            }
        };
    }

    // supplier will always return the single instance, so combining is simple--just pick one and return it
    @Override
    public BinaryOperator<BufferedWriter> combiner() {
        return (bw1, bw2) -> bw1;
    }

    @Override
    public Function<BufferedWriter, Path> finisher() {
        return bw -> {
            try {
                close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return path;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    @Override
    public void close() throws IOException {
        // use try-with-resources to assure that all get closed
        try(
                OutputStream outRef = out;
                BufferedWriter writerRef = writer;
                OutputStreamWriter osWriterRef = osWriter;
        ){

        } finally {
            osWriter = null;
            writer = null;
            out = null;
        }
    }

    private void closeAndThrow(IOException e) {
        try {
            close();
        } catch (IOException e2){
            e.addSuppressed(e2);
        }
        throw new UncheckedIOException(e);
    }

}
