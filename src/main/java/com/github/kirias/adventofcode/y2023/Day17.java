package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.Matrix;
import com.github.kirias.adventofcode.common.Pair;

import java.util.*;
import java.util.stream.IntStream;

public class Day17 extends Problem {

    record Block(int row, int col, int loss) {
    }

    record Visits(int row, int col, Direction dir, int stepsInDir) {
    }

    static final class PathFactory {
        private final int maxAllowed;
        private final int minAllowed;

        public PathFactory(int maxAllowed, int minAllowed) {
            this.maxAllowed = maxAllowed;
            this.minAllowed = minAllowed;
        }

        public Path createPath(Path prev, Block block, Direction direction) {
            return new Path(maxAllowed, minAllowed, prev, block, direction);
        }
    }

    static final class Path {
        private final Path prev;
        private final Block block;
        private final Direction direction;

        private int loss = -1;
        private final int maxAllowed;
        private final int minAllowed;

        Path(int maxAllowed, int minAllowed, Path prev, Block block, Direction direction) {
            this.maxAllowed = maxAllowed;
            this.minAllowed = minAllowed;
            this.prev = prev;
            this.block = block;
            this.direction = direction;
        }

        int getLoss() {
            if (loss == -1) {
                loss = block.loss + (prev == null ? 0 : prev.getLoss());
            }
            return loss;
        }

        static Map<Pair<Integer, Integer>, Integer> minSumHash = new HashMap<>();
        static boolean useManhattanHeuristic = true; // with manhattan Cycles - 231862, with minSum Cycles - 519184

        int getLossWithHeuristic(Matrix<Block> matrix) {
            if (useManhattanHeuristic) {
                int rowsLeft = matrix.height() - block.row;
                int colsLeft = matrix.width() - block.col;
                return getLoss() + rowsLeft + colsLeft;
            } else {
                int rowsLeft = matrix.height() - block.row + 1;
                int colsLeft = matrix.width() - block.col + 1;

                int minSumFromRegion;
                if (minSumHash.containsKey(new Pair<>(rowsLeft, colsLeft))) {
                    minSumFromRegion = minSumHash.get(new Pair<>(rowsLeft, colsLeft));
                } else {
                    int minStepsInRegion = Math.min(rowsLeft, colsLeft);
                    minSumFromRegion = IntStream.range(rowsLeft - 1, matrix.height())
                            .flatMap(r -> IntStream.range(colsLeft - 1, matrix.width())
                                    .map(c -> matrix.get(r, c).loss))
                            .sorted()
                            .limit(minStepsInRegion)
                            .sum();
                    minSumHash.put(new Pair<>(rowsLeft, colsLeft), minSumFromRegion);
                }

                return getLoss() + minSumFromRegion;
            }
        }

        @Override
        public String toString() {
            return "Path{" +
                    "block.row=" + block.row +
                    " block.col=" + block.col +
                    '}';
        }

        Set<Direction> allowedNextSteps() {
            if (prev == null || direction == null) return Direction.all();
            if (minAllowed == 0) return exceptStepBack(Direction.all());
            Path check = this;
            for (int i = 0; i < minAllowed; i++) {
                if (check == null || check.direction != direction) return Set.of(direction);
                check = check.prev;
            }
            return exceptStepBack(Direction.all());
        }

        private Set<Direction> exceptStepBack(Set<Direction> allowedDirections) {
            allowedDirections.remove(direction.opposite());
            return allowedDirections;
        }

        int countDirectionAllowed(Direction checkDirection) {
            int allowed = maxAllowed;
            Path currentCheck = this;
            while (currentCheck != null && allowed > 0) {
                if (currentCheck.direction == checkDirection) {
                    allowed--;
                    currentCheck = currentCheck.prev;
                } else {
                    break;
                }
            }
            return allowed;
        }
    }

    public Day17(String path) {
        super(path, 17);
    }

    @Override
    public long getPart1Solution() {
        PathFactory pathFactory = new PathFactory(3, 0);
        return getSolution(pathFactory);
    }

    @Override
    public long getPart2Solution() {
        PathFactory pathFactory = new PathFactory(10, 4);
        return getSolution(pathFactory);
    }

    private int getSolution(PathFactory pathFactory) {
        Matrix<Block> matrix = new Matrix<>();
        int row = 0;
        for (String line : inputLines().toList()) {
            List<Block> gridLine = new ArrayList<>();
            int col = 0;
            for (char c : line.toCharArray()) {
                gridLine.add(new Block(row, col, c - '0'));
                col++;
            }
            matrix.addRow(gridLine);
            row++;
        }

        PriorityQueue<Path> queue = new PriorityQueue<>(Comparator.comparing(path1 -> path1.getLossWithHeuristic(matrix)));
        Path start = pathFactory.createPath(null, matrix.get(0, 0), null);
        Block end = matrix.get(matrix.height() - 1, matrix.width() - 1);

        Path result = null;
        Map<Visits, Integer> explored = new HashMap<>();
        queue.add(start);


        while (!queue.isEmpty()) {
            Path shortestPath = queue.poll();
            if (shortestPath.block == end) {
                result = shortestPath;
                break;
            }

            shortestPath.allowedNextSteps()
                    .forEach(direction -> {
                        int directionAllowed = shortestPath.countDirectionAllowed(direction);
                        if (directionAllowed > 0) {
                            matrix.getIfExist(shortestPath.block.row + direction.rowOffset(), shortestPath.block.col + direction.colOffset())
                                    .map(b -> pathFactory.createPath(shortestPath, b, direction))
                                    .flatMap(path -> {
                                        Visits v = new Visits(path.block.row, path.block.col, direction, directionAllowed);
                                        int pathLoss = path.getLoss();
                                        if (explored.containsKey(v) && explored.get(v) <= pathLoss) {
                                            return Optional.empty();
                                        } else {
                                            explored.put(v, pathLoss);
                                            return Optional.of(path);
                                        }
                                    })
                                    .ifPresent(queue::add);
                        }
                    });
        }
        return result.getLoss() - matrix.get(0, 0).loss;
    }
}
