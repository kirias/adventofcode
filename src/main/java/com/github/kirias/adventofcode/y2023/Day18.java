package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day18 extends Problem {

    static class Hole {
        int row;
        int col;
        Direction direction, nextDirection;

        public Hole(int row, int col, Direction direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "#";
        }
    }

    public Day18(String path) {
        super(path, 18);
    }

    @Override
    public long getPart2Solution() {

//        Function<String[], Pair<Direction, Integer>> parsingFunction = dir_steps -> {
//            Direction dir = switch (dir_steps[2].charAt(7)) {
//                case '1' -> Direction.DOWN;
//                case '2' -> Direction.LEFT;
//                case '3' -> Direction.UP;
//                default -> Direction.RIGHT;
//            };
//            int steps = Integer.parseInt(dir_steps[2], 2, 7, 16);
//
//            return new Pair<Direction, Integer>(dir, steps);
//        };
//
//        inputLines()
//                .map(line -> line.split(" "))
//                .map(parsingFunction)
//                .collect(Collectors.groupingBy(Pair::getLeft))
//                .entrySet()
//                .stream()
//                .forEach(es -> {
//                    List<Integer> values = es.getValue().stream().map(Pair::getRight).toList();
//                    System.out.println(es.getKey());
//                    System.out.println(values);
//                    System.out.println(MathUtil.gcd(values));
//                });
//
//
//
//
//
//        return getSolution(parsingFunction);
        return 0;
    }

    @Override
    public long getPart1Solution() {
        return getSolution(dir_steps -> new Pair<>(direction(dir_steps[0]), Integer.valueOf(dir_steps[1])));
    }

    private int getSolution(Function<String[], Pair<Direction, Integer>> parsingFunction) {
        LinkedList<Hole> holes = new LinkedList<>();
        holes.add(new Hole(0, 0, null));
        AtomicInteger countRightTurns = new AtomicInteger(0);

        inputLines()
                .map(line -> line.split(" "))
                .map(parsingFunction)
                .forEach(dig -> {
                    Hole last = holes.getLast();
                    Direction newDirection = dig.getLeft();
                    if (last.direction != null) {
                        if (last.direction.turnLeft() == newDirection) countRightTurns.decrementAndGet();
                        if (last.direction.turnRight() == newDirection) countRightTurns.incrementAndGet();
                    } else {
                        last.direction = newDirection;
                    }
                    for (int i = 0; i < dig.getRight(); i++) {
                        holes.getLast().nextDirection = newDirection;
                        Hole hole = new Hole(last.row + newDirection.rowOffset() * (i + 1), last.col + newDirection.colOffset() * (i + 1), newDirection);
                        holes.addLast(hole);
                    }
                });
        holes.removeFirst();
        holes.getLast().nextDirection = holes.getFirst().direction;
        int minRow = 0;
        int minCol = 0;
        int maxRow = 0;
        int maxCol = 0;
        for (Hole hole : holes) {
            if (hole.row < minRow) minRow = hole.row;
            if (hole.col < minCol) minCol = hole.col;
            if (hole.row > maxRow) maxRow = hole.row;
            if (hole.col > maxCol) maxCol = hole.col;
        }
        MapMatrix<Hole> matrix = new MapMatrix<>(maxRow - minRow + 1, maxCol - minCol + 1);
        for (Hole hole : holes) {
            hole.row = hole.row - minRow;
            hole.col = hole.col - minCol;
            matrix.set(hole.row, hole.col, hole);
        }

        AtomicInteger count = new AtomicInteger(0);
        System.out.println("Height - " + matrix.height());
        for (int row = 0; row < matrix.height(); row++) {
            if (row % 1000 == 0) {
                System.out.println("Row - " + row);
            }
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

    enum State {
        OUT, WALL_TO_IN, IN, WALL_TO_OUT;


        public State next() {
            return State.values()[(this.ordinal() + 1) % 4];
        }

        public State prev() {
            return State.values()[(this.ordinal() + 4 - 1) % 4];
        }
    }

    private Direction direction(String dirStep) {
        return switch (dirStep) {
            case "R" -> Direction.RIGHT;
            case "L" -> Direction.LEFT;
            case "U" -> Direction.UP;
            default -> Direction.DOWN;
        };
    }
}
