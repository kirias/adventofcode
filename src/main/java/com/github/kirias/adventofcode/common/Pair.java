package com.github.kirias.adventofcode.common;

import java.util.Objects;

public class Pair<L, R> {

    L left;
    R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public Pair<L, R> swap() {
        return new Pair<>((L)right, (R)left);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
