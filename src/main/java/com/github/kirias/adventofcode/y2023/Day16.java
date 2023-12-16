package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.Matrix;

import java.util.*;
import java.util.function.Consumer;

import static com.github.kirias.adventofcode.common.Direction.*;

public class Day16 extends Problem {

    static class GridEl {
        private final int row;
        private final int col;
        private final char el;

        Set<Direction> lights = EnumSet.noneOf(Direction.class);

        public GridEl(char s, int row, int col) {
            this.row = row;
            this.col = col;
            this.el = s;
        }

        public void lightNextEls(Matrix<GridEl> matrix, Direction direction, Consumer<GridEl> ifLighted) {
            if (el == '/' || el == '\\') {
                Direction mirrorReflection = switch (direction) {
                    case LEFT -> DOWN;
                    case RIGHT -> UP;
                    case UP -> RIGHT;
                    default -> LEFT;
                };
                final Direction finalDirection;
                if (el == '/') {
                    finalDirection = mirrorReflection;
                } else {
                    finalDirection = mirrorReflection.opposite();
                }
                matrix.apply(row + finalDirection.rowOffset(), col + finalDirection.colOffset(), el -> el.tryLight(finalDirection, ifLighted));
            } else if (el == '.') {
                matrix.apply(row + direction.rowOffset(), col + direction.colOffset(), el -> el.tryLight(direction, ifLighted));
            } else if (el == '|' || el == '-') {
                if ((el == '|' && (direction == UP || direction == DOWN)) ||
                        (el == '-' && (direction == LEFT || direction == RIGHT))) {
                    matrix.apply(row + direction.rowOffset(), col + direction.colOffset(), el -> el.tryLight(direction, ifLighted));
                } else {
                    Direction left = direction.turnLeft();
                    Direction right = direction.turnRight();
                    matrix.apply(row + left.rowOffset(), col + left.colOffset(), el -> el.tryLight(left, ifLighted));
                    matrix.apply(row + right.rowOffset(), col + right.colOffset(), el -> el.tryLight(right, ifLighted));
                }
            }
        }

        public boolean anyLight() {
            return !lights.isEmpty();
        }

        public void tryLight(Direction lightDirection, Consumer<GridEl> ifLighted) {
            if (!lights.contains(lightDirection)) {
                lights.add(lightDirection);
                ifLighted.accept(this);
            }
        }

        public void setLight(Direction lightDirection) {
            lights.add(lightDirection);
        }

        public void resetLights() {
            lights.clear();
        }

        public void forEachLight(Consumer<Direction> directionConsumer) {
            lights.forEach(directionConsumer);
        }
    }

    public Day16(String path) {
        super(path, 16);
    }

    @Override
    public long getPart1Solution() {
        Matrix<GridEl> matrix = getInputMatrix();

        matrix.get(0, 0).setLight(RIGHT);
        return countEnergized(matrix, 0, 0);
    }

    @Override
    public long getPart2Solution() {
        Matrix<GridEl> matrix = getInputMatrix();

        long maxEnergized = 0;
        for (int row = 0; row < matrix.height(); row++) {
            matrix.get(row, 0).setLight(RIGHT);
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, row, 0));

            matrix.get(row, matrix.width() - 1).setLight(LEFT);
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, row, matrix.width() - 1));
        }
        for (int col = 0; col < matrix.width(); col++) {
            matrix.get(0, col).setLight(DOWN);
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, 0, col));

            matrix.get(matrix.height() - 1, col).setLight(UP);
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, matrix.height() - 1, col));
        }

        return maxEnergized;
    }

    private Matrix<GridEl> getInputMatrix() {
        Matrix<GridEl> matrix = new Matrix<>();
        int row = 0;
        for (String line : inputLines().toList()) {
            List<GridEl> gridLine = new ArrayList<>();
            int col = 0;
            for (char c : line.toCharArray()) {
                gridLine.add(new GridEl(c, row, col));
                col++;
            }
            matrix.addRow(gridLine);
            row++;
        }
        return matrix;
    }

    private long countEnergized(Matrix<GridEl> matrix, int startRow, int startCol) {
        Queue<GridEl> elements = new LinkedList<>();

        elements.add(matrix.get(startRow, startCol));

        while (!elements.isEmpty()) {
            GridEl beam = elements.poll();
            beam.forEachLight(dir -> beam.lightNextEls(matrix, dir, elements::add));
        }
        long countLights = matrix.count(GridEl::anyLight);
        matrix.forEach(GridEl::resetLights);
        return countLights;
    }
}
