package com.github.kirias.adventofcode.common;

public enum Direction {
    LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1);

    final int colOffset;
    final int rowOffset;

    Direction(int colOffset, int rowOffset) {
        this.colOffset = colOffset;
        this.rowOffset = rowOffset;
    }

    public int colOffset() {
        return colOffset;
    }

    public int rowOffset() {
        return rowOffset;
    }

    public Direction opposite() {
        if (this == LEFT) return RIGHT;
        if (this == RIGHT) return LEFT;
        if (this == UP) return DOWN;
        return UP;
    }

    public Direction turnLeft() {
        if (this == LEFT) return DOWN;
        if (this == RIGHT) return UP;
        if (this == UP) return LEFT;
        return RIGHT;
    }

    public Direction turnRight() {
        return turnLeft().opposite();
    }
}
