package com.github.kirias.adventofcode.common;

import java.util.Objects;

public class LongPair {

    long left;
    long right;

    public LongPair(long left, long right) {
        this.left = left;
        this.right = right;
    }

    public long getLeft() {
        return left;
    }

    public long getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongPair longPair = (LongPair) o;
        return left == longPair.left && right == longPair.right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
