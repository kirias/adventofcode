package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.MapMatrix;
import com.github.kirias.adventofcode.common.Pair;
import com.github.kirias.adventofcode.common.RangeMatrix;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Day18 extends Problem {

    public Day18(String path) {
        super(path, 18);
    }

    @Override
    public long getPart2Solution() {

        Function<String[], Pair<Direction, Integer>> parsingFunction = dir_steps -> {
            Direction dir = switch (dir_steps[2].charAt(7)) {
                case '1' -> Direction.DOWN;
                case '2' -> Direction.LEFT;
                case '3' -> Direction.UP;
                default -> Direction.RIGHT;
            };
            int steps = Integer.parseInt(dir_steps[2], 2, 7, 16);

            return new Pair<>(dir, steps);
        };

        return getRangeSplitSolution(parsingFunction);
    }

    @Override
    public long getPart1Solution() {
        return getRangeSplitSolution(dir_steps -> new Pair<>(direction(dir_steps[0]), Integer.valueOf(dir_steps[1])));
    }

    private long getRangeSplitSolution(Function<String[], Pair<Direction, Integer>> parsingFunction) {

        RangeMatrix<Hole> matrix = new RangeMatrix<>();
        matrix.start();

        List<Pair<Direction, Integer>> pairStream = inputLines()
                .map(line -> line.split(" "))
                .map(parsingFunction)
                .toList();

        Direction firstDirection = null;
        Hole last = null;
        for (Pair<Direction, Integer> dig : pairStream) {
            Direction newDirection = dig.getLeft();
            if (last != null) {
                last.nextDirection = newDirection;
            }
            if (firstDirection == null) firstDirection = newDirection;
            last = new Hole(newDirection, newDirection);
            matrix.add(last, newDirection, dig.getRight() - 1);
            last = new Hole(newDirection, null);
            matrix.add(last, newDirection, 1);
        }
        last.nextDirection = firstDirection;

        matrix.finish();

        long count = 0;

        for (Iterator<RangeMatrix<Hole>.RowRange> rowsIterator = matrix.rowRanges(); rowsIterator.hasNext(); ) {
            RangeMatrix<Hole>.RowRange rowRange = rowsIterator.next();
            Iterator<RangeMatrix<Hole>.RowRange.ColRange> colsIterator = rowRange.colRanges();

            State state = State.OUT;
            RangeMatrix<Hole>.RowRange.ColRange next = colsIterator.next();
            Hole prev = next.value();
            Set<Direction> cornerDirections = new HashSet<>();
            if (prev != null) {
                count += next.numCols() * rowRange.numRows();
                state = State.WALL_TO_IN;
                cornerDirections.add(prev.direction);
                cornerDirections.add(prev.nextDirection);

            }

            while (colsIterator.hasNext()) {
                RangeMatrix<Hole>.RowRange.ColRange nexColRange = colsIterator.next();

                Hole hole = nexColRange.value();
                if (prev != null && hole == null) { // after
                    cornerDirections.add(prev.direction);
                    cornerDirections.add(prev.nextDirection);
                    if (cornerDirections.size() == 1) state = state.next();
                    if (cornerDirections.size() == 2) state = state.next();
                    if (cornerDirections.size() == 3) state = state.prev();
                    cornerDirections.clear();
                }
                if (prev == null && hole != null) { //before
                    cornerDirections.add(hole.direction);
                    cornerDirections.add(hole.nextDirection);
                    state = state.next();
                }
                if (hole != null) {
                    count += nexColRange.numCols() * rowRange.numRows();
                } else {
                    if (state == State.IN) {
                        count += nexColRange.numCols() * rowRange.numRows();
                    }
                }
                prev = hole;
            }
        }
        return count;
    }

    private int getMatrixSolution(Function<String[], Pair<Direction, Integer>> parsingFunction) {
        LinkedList<HoleWithCoordinates> holes = new LinkedList<>();
        holes.add(new HoleWithCoordinates(0, 0, null));
        AtomicInteger countRightTurns = new AtomicInteger(0);

        inputLines()
                .map(line -> line.split(" "))
                .map(parsingFunction)
                .forEach(dig -> {
                    HoleWithCoordinates last = holes.getLast();
                    Direction newDirection = dig.getLeft();
                    if (last.direction != null) {
                        if (last.direction.turnLeft() == newDirection) countRightTurns.decrementAndGet();
                        if (last.direction.turnRight() == newDirection) countRightTurns.incrementAndGet();
                    } else {
                        last.direction = newDirection;
                    }
                    for (long i = 0; i < dig.getRight(); i++) {
                        holes.getLast().nextDirection = newDirection;
                        HoleWithCoordinates hole = new HoleWithCoordinates(last.row + newDirection.rowOffset() * (i + 1), last.col + newDirection.colOffset() * (i + 1), newDirection);
                        holes.addLast(hole);
                    }
                });
        holes.removeFirst();
        holes.getLast().nextDirection = holes.getFirst().direction;
        long minRow = 0;
        long minCol = 0;
        long maxRow = 0;
        long maxCol = 0;
        for (HoleWithCoordinates hole : holes) {
            if (hole.row < minRow) minRow = hole.row;
            if (hole.col < minCol) minCol = hole.col;
            if (hole.row > maxRow) maxRow = hole.row;
            if (hole.col > maxCol) maxCol = hole.col;
        }
        MapMatrix<Hole> matrix = new MapMatrix<>(maxRow - minRow + 1, maxCol - minCol + 1);
        for (HoleWithCoordinates hole : holes) {
            hole.row = hole.row - minRow;
            hole.col = hole.col - minCol;
            matrix.set(hole.row, hole.col, hole);
        }

        AtomicInteger count = new AtomicInteger(0);
        for (int row = 0; row < matrix.height(); row++) {
            State state = State.OUT;
            Hole prev = matrix.get(row, 0);
            Set<Direction> cornerDirections = new HashSet<>();
            if (prev != null) {
                count.incrementAndGet();
                state = State.WALL_TO_IN;
                cornerDirections.add(prev.direction);
                cornerDirections.add(prev.nextDirection);
            }
//            System.out.print(prev == null ? "░" : "#");
            for (int col = 1; col < matrix.width(); col++) {
                Hole hole = matrix.get(row, col);
                if (prev != null && hole == null) { // after
                    cornerDirections.add(prev.direction);
                    cornerDirections.add(prev.nextDirection);
                    if (cornerDirections.size() == 1) state = state.next();
                    if (cornerDirections.size() == 2) state = state.next();
                    if (cornerDirections.size() == 3) state = state.prev();
                    cornerDirections.clear();
                }
                if (prev == null && hole != null) { //before
                    cornerDirections.add(hole.direction);
                    cornerDirections.add(hole.nextDirection);
                    state = state.next();
                }
                if (hole != null) {
                    count.incrementAndGet();
                } else {
                    if (state == State.IN) count.incrementAndGet();
                }
//                if (state == State.IN) {
//                    System.out.print("▓");
//                } else if (state == State.OUT) {
//                    System.out.print("░");
//                } else if (hole != null) {
//                    System.out.print("#");
//                } else {
//                    System.out.print(" ");
//                }
                prev = hole;
            }
            //System.out.println();
        }
        return count.get();
    }

    private Direction direction(String dirStep) {
        return switch (dirStep) {
            case "R" -> Direction.RIGHT;
            case "L" -> Direction.LEFT;
            case "U" -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    enum State {
        OUT, WALL_TO_IN, IN, WALL_TO_OUT;

        public State next() {
            return State.values()[(this.ordinal() + 1) % 4];
        }

        public State prev() {
            return State.values()[(this.ordinal() + 4 - 1) % 4];
        }
    }

    static class Hole {
        Direction direction, nextDirection;

        public Hole(Direction direction, Direction nextDirection) {
            this.direction = direction;
            this.nextDirection = nextDirection;
        }

        @Override
        public String toString() {
            return "#";
        }
    }

    static class HoleWithCoordinates extends Hole {
        long row, col;

        HoleWithCoordinates(long row, long col, Direction direction) {
            super(direction, null);
            this.row = row;
            this.col = col;
        }
    }
}
