package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.Matrix;
import com.github.kirias.adventofcode.common.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day17 extends Problem {

    record Block(int row, int col, int loss) {
    }

    record Visits(int row, int col, Direction dir) {
    }

    record PathFactory(int minAllowed, int maxAllowed) {
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

        Set<Pair<Direction, Integer>> allowedNextSteps() {
            return (direction == null ? Direction.all() : Set.of(direction.turnRight(), direction.turnLeft()))
                    .stream()
                    .flatMap(direction -> IntStream.range(minAllowed, maxAllowed + 1)
                            .mapToObj(i -> new Pair<>(direction, i))).collect(Collectors.toSet());

        }
    }

    public Day17(String path) {
        super(path, 17);
    }

    @Override
    public long getPart1Solution() {
        PathFactory pathFactory = new PathFactory(1, 3);
        return getSolution(pathFactory);
    }

    @Override
    public long getPart2Solution() {
        PathFactory pathFactory = new PathFactory(4, 10);
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
                    .forEach(step -> {
                        Direction direction = step.getLeft();
                        Integer steps = step.getRight();
                        matrix.getIfExist(shortestPath.block.row + direction.rowOffset() * steps, shortestPath.block.col + direction.colOffset() * steps)
                                .map(b -> {
                                    Path current = shortestPath;
                                    for (int i = 0; i < steps; i++) {
                                        current = pathFactory.createPath(current,
                                                matrix.get(current.block.row + direction.rowOffset(), current.block.col + direction.colOffset()),
                                                direction);
                                    }
                                    return current;
                                })
                                .flatMap(path -> {
                                    Visits v = new Visits(path.block.row, path.block.col, direction);
                                    int pathLoss = path.getLoss();
                                    if (explored.containsKey(v) && explored.get(v) <= pathLoss) {
                                        return Optional.empty();
                                    } else {
                                        explored.put(v, pathLoss);
                                        return Optional.of(path);
                                    }
                                })
                                .ifPresent(queue::add);

                    });
        }
        return result.getLoss() - matrix.get(0, 0).loss;
    }
}
