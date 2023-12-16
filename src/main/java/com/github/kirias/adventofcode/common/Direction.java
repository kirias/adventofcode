package com.github.kirias.adventofcode.common;

import java.util.EnumSet;
import java.util.Set;

public enum Direction {
    LEFT(0, -1), RIGHT(0, 1), UP(-1, 0), DOWN(1, 0);

    final int colOffset;
    final int rowOffset;

    Direction(int rowOffset, int colOffset) {
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
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

    public static Set<Direction> all() {
        return EnumSet.allOf(Direction.class);
    }
}
