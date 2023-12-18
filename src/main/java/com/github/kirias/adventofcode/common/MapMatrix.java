package com.github.kirias.adventofcode.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MapMatrix<E> {

    record Key(long row, long col) {
        @Override
        public boolean equals(Object obj) {
            return row == ((Key)obj).row &&
                    col == ((Key)obj).col;
        }

        @Override
        public int hashCode() {
            return (int)(row + col);
        }
    }

    private Map<Key, E> elements;
    private long height, width;


    public MapMatrix(long rows, long cols) {
        this.elements = new HashMap<>();
        this.height = rows;
        this.width = cols;
    }

    public long width() {
        return width;
    }

    public long height() {
        return height;
    }

    public E get(long row, long col) {
        return elements.get(new Key(row, col));
    }

    public Optional<E> getIfExist(long row, long col) {
        return Optional.ofNullable(get(row, col));
    }

    public List<E> getList(int row, int col) {
        return elements.containsKey(new Key(row, col))
                ? List.of(elements.get(new Key(row, col))) : List.of();
    }

    public void apply(int row, int col, Consumer<E> consumer) {
        if (inMatrix(row, col)) {
            consumer.accept(elements.get(new Key(row, col)));
        }
    }

    public long count(Predicate<E> test) {
        return stream()
                .filter(test)
                .count();
    }

    public void forEach(Consumer<E> consumer) {
        elements.values().forEach(consumer);
    }

    public void set(long row, long col, E value) {
        if (!inMatrix(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        elements.put(new Key(row, col), value);
    }

    public boolean inMatrix(long row, long col) {
        if (row < 0 || col < 0) return false;
        if (row >= height() || col >= width()) return false;
        return true;
    }

    public Stream<E> stream() {
        return elements.values().stream();
    }
}
