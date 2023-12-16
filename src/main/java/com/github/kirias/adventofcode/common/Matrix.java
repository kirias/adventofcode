package com.github.kirias.adventofcode.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Matrix<E> {

    private List<List<E>> rows;

    public Matrix(List<List<E>> rows) {
        this.rows = rows;
    }

    public Matrix(int rows, int cols) {
        this.rows = new ArrayList<>(rows);
        IntStream.range(0, rows)
                .forEach(i -> this.rows.add(new ArrayList<>(cols)));
    }

    public Matrix() {
        this.rows = new ArrayList<>();
    }

    public void addRow(List<E> row) {
        rows.add(row);
    }


    public int width() {
        return rows.get(0).size();
    }

    public int height() {
        return rows.size();
    }

    public E get(int row, int col) {
        return inMatrix(row, col) ? rows.get(row).get(col) : null;
    }

    public Optional<E> getIfExist(int row, int col) {
        return inMatrix(row, col) ? Optional.of(rows.get(row).get(col)) : Optional.empty();
    }

    public List<E> getList(int row, int col) {
        return inMatrix(row, col) ? List.of(rows.get(row).get(col)) : List.of();
    }

    public void apply(int row, int col, Consumer<E> consumer) {
        if (inMatrix(row, col))
            consumer.accept(rows.get(row).get(col));
    }

    public long count(Predicate<E> test) {
        long count = 0;
        for (List<E> row : rows) {
            for (E el : row) {
                if (test.test(el)) count++;
            }
        }
        return count;
    }

    public void forEach(Consumer<E> consumer) {
        for (List<E> row : rows) {
            for (E el : row) {
                consumer.accept(el);
            }
        }
    }

    public void set(int row, int col, E value) {
        if (!inMatrix(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        rows.get(row).set(col, value);
    }

    public boolean inMatrix(int row, int col) {
        if (row < 0 || col < 0) return false;
        if (row >= height() || col >= width()) return false;
        return true;
    }

    public Stream<E> stream() {
        return rows.stream().flatMap(Collection::stream);
    }

    public void print() {
        for (List<E> row : rows) {
            for (E el : row) {
                System.out.print(el);
            }
            System.out.println();
        }
    }
}
