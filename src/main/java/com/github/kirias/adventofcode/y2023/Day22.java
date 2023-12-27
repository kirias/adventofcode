package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22 extends Problem {
    public Day22(String path) {
        super(path, 22);
    }

    @Override
    public long getPart1Solution() {
        List<Brick> bricks = inputLines().map(Brick::new).toList();

        Space space = new Space(bricks);

        while (true) {
            if (space.moveOneBlockDown().isEmpty()) break;
        }
        int canBeRemoved = 0;
        for (Brick brick : bricks) {
            List<Brick> bricksOver = space.brickLayingOn(brick);

            boolean allOverHaveMoreThanOneSupport = bricksOver.stream().allMatch(brickOver -> space.countBricksUnder(brickOver) > 1);

            if (allOverHaveMoreThanOneSupport) {
                canBeRemoved++;
            }
        }

        return canBeRemoved;
    }

    @Override
    public long getPart2Solution() {
        List<Brick> bricks = inputLines().map(Brick::new).toList();

        Space space = new Space(bricks);

        while (true) {
            if (space.moveOneBlockDown().isEmpty()) break;
        }

        int countFallenTotal = 0;

        for (Brick brick : bricks) {
            countFallenTotal += countFallenAfterDesintegrate(space.copyExcept(brick));
        }

        return countFallenTotal;
    }

    public int countFallenAfterDesintegrate(Space space) {
        Set<Brick> fallen = new HashSet<>();
        while (true) {
            Set<Brick> bricksFallen = space.moveOneBlockDown();
            fallen.addAll(bricksFallen);
            if (bricksFallen.isEmpty()) break;
        }
        return fallen.size();
    }

    public record Coordinate(int x, int y, int z) {
    }

    public static class Space {
        private final List<Brick> bricks;

        public Space(List<Brick> bricks) {
            this.bricks = bricks;
        }

        public Space copyExcept(Brick skip) {
            return new Space(bricks.stream().filter(b -> b != skip).map(Brick::copy).toList());
        }

        public Set<Brick> moveOneBlockDown() {
            Set<Brick> moved = new HashSet<>();
            Set<Coordinate> allCoords = bricks.stream().flatMap(b -> b.spaceCoords.stream()).collect(Collectors.toSet());

            for (Brick brick : bricks) {
                Set<Coordinate> coordinatesUnderBrick = brick.coordsBelow();
                if (!coordinatesUnderBrick.isEmpty() && coordinatesUnderBrick.stream().noneMatch(allCoords::contains)) {
                    brick.moveOneDown();
                    moved.add(brick);
                }
            }
            return moved;
        }

        public List<Brick> brickLayingOn(Brick brick) {
            Set<Coordinate> coordinatesAboveBrick = brick.coordsAbove();
            return bricks.stream().filter(b -> b.spaceCoords.stream().anyMatch(coordinatesAboveBrick::contains)).toList();
        }

        public long countBricksUnder(Brick brick) {
            Set<Coordinate> coordinatesUnderOverBrick = brick.coordsBelow();
            return bricks.stream().filter(b -> b.spaceCoords.stream().anyMatch(coordinatesUnderOverBrick::contains)).count();
        }
    }

    public static class Brick {
        int x1, x2, y1, y2, z1, z2;
        Set<Coordinate> spaceCoords = new HashSet<>();

        public Brick(String coords) {
            Scanner scanner = new Scanner(coords);
            scanner.useDelimiter("[,~]");
            x1 = scanner.nextInt();
            y1 = scanner.nextInt();
            z1 = scanner.nextInt();

            x2 = scanner.nextInt();
            y2 = scanner.nextInt();
            z2 = scanner.nextInt();

            fillCoords();
        }

        public Brick(int x1, int x2, int y1, int y2, int z1, int z2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z1 = z1;
            this.z2 = z2;
            fillCoords();
        }

        private void fillCoords() {
            IntStream.range(Math.min(x1, x2), Math.max(x1, x2) + 1).forEach(eachX -> {
                IntStream.range(Math.min(y1, y2), Math.max(y1, y2) + 1).forEach(eachY -> {
                    IntStream.range(Math.min(z1, z2), Math.max(z1, z2) + 1).forEach(eachZ -> {
                        spaceCoords.add(new Coordinate(eachX, eachY, eachZ));
                    });
                });
            });
        }


        public Brick copy() {
            return new Brick(x1, x2, y1, y2, z1, z2);
        }

        @Override
        public String toString() {
            return x1 + "," + y1 + "," + z1 + "~" + x2 + "," + y2 + "," + z2;
        }

        public void moveOneDown() {
            z1--;
            z2--;
            spaceCoords = spaceCoords.stream()
                    .map(s -> new Coordinate(s.x, s.y, s.z - 1))
                    .collect(Collectors.toSet());
        }

        public Set<Coordinate> coordsBelow() {
            if (Math.min(z1, z2) == 1) return Set.of();
            Set<Coordinate> coordsBelow = new HashSet<>();

            IntStream.range(Math.min(x1, x2), Math.max(x1, x2) + 1).forEach(eachX -> {
                IntStream.range(Math.min(y1, y2), Math.max(y1, y2) + 1).forEach(eachY -> {
                    coordsBelow.add(new Coordinate(eachX, eachY, Math.min(z1, z2) - 1));
                });
            });

            return coordsBelow;
        }

        public Set<Coordinate> coordsAbove() {
            Set<Coordinate> coordsBelow = new HashSet<>();

            IntStream.range(Math.min(x1, x2), Math.max(x1, x2) + 1).forEach(eachX -> {
                IntStream.range(Math.min(y1, y2), Math.max(y1, y2) + 1).forEach(eachY -> {
                    coordsBelow.add(new Coordinate(eachX, eachY, Math.max(z1, z2) + 1));
                });
            });

            return coordsBelow;
        }
    }
}
