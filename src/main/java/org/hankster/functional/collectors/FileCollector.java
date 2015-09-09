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
 * A {@link Collector} that writes {@link Stream} contents to a file
 */

public class FileCollector implements Collector<String, BufferedWriter, Path> {
    Path path;
    OutputStream out = null;
    OutputStreamWriter osWriter = null;
    BufferedWriter writer = null;

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
            //noinspection EmptyTryBlock
            try {
                bw.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                this.writer = null;
                this.osWriter = null;
                this.out = null;
            }
            return path;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    private void closeAndThrow(IOException e) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException suppressed) {
                e.addSuppressed(suppressed);
            }
            writer = null;
        }
        if (osWriter != null) {
            try {
                osWriter.close();
            } catch (IOException suppressed) {
                e.addSuppressed(suppressed);
            }
            osWriter = null;
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException suppressed) {
                e.addSuppressed(suppressed);
            }
            out = null;
        }
        throw new UncheckedIOException(e);
    }
}
