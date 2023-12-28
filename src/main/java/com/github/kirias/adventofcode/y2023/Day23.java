package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.Matrix;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day23 extends Problem {
    public Day23(String path) {
        super(path, 23);
    }

    @Override
    public long getPart1Solution() {
        return getSolution(this::slipperyMapper);
    }

    @Override
    public long getPart2Solution() {
        return getSolution(this::dryMapper);
    }

    private long getSolution(Function<Character, Set<Direction>> directionsMapper) {
        Matrix<Tile> matrix = new Matrix<>();
        int row = 0;
        for (String line : inputLines().toList()) {
            List<Tile> tiles = new ArrayList<>();
            int col = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                Tile t = new Tile(row, col, c);
                tiles.add(t);
                col++;
            }
            matrix.addRow(tiles);
            row++;
        }
        Tile start = matrix.rows().get(0).stream().filter(t -> t.type == '.').findAny().get();
        Tile end = matrix.rows().get(matrix.height() - 1).stream().filter(t -> t.type == '.').findAny().get();

        Way wayStart = new Way(start, null);
        PriorityQueue<Way> paths = new PriorityQueue<>();
        paths.add(wayStart);

        long currentMax = 0;

        while (!paths.isEmpty()) {
            Way current = paths.poll();
            Set<Direction> allowedDirections;
            allowedDirections = directionsMapper.apply(current.tile.type);

            List<Tile> nextTiles = allowedDirections.stream().map(d -> matrix.getIfExist(current.tile.row + d.rowOffset(), current.tile.col + d.colOffset())
                            .filter(t -> t.type != '#')
                            .filter(current::notContains))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            List<Tile> prevTiles = current.prevTiles;

            if (nextTiles.size() > 1) { // saving only crossroads
                prevTiles = new ArrayList<>(current.prevTiles.size() + 1);
                prevTiles.addAll(current.prevTiles);
                prevTiles.add(current.tile);
            }
            for (Tile nextTile : nextTiles) {
                if (nextTile == end) {
                    if (currentMax < current.getSteps() + 1) {
                        currentMax = current.getSteps() + 1;
                    }
                } else {
                    paths.add(new Way(nextTile, current, prevTiles));
                }
            }

        }

        return currentMax;
    }

    private Set<Direction> slipperyMapper(Character tileType) {
        if (tileType == '.') {
            return Direction.all();
        } else if (tileType == '>') {
            return EnumSet.of(Direction.RIGHT);
        } else if (tileType == '<') {
            return EnumSet.of(Direction.LEFT);
        } else if (tileType == '^') {
            return EnumSet.of(Direction.UP);
        } else if (tileType == 'v') {
            return EnumSet.of(Direction.DOWN);
        } else {
            return Set.of();
        }
    }

    private Set<Direction> dryMapper(Character tileType) {
        if (tileType == '#') {
            return Set.of();
        } else {
            return Direction.all();
        }
    }

    static final class Tile {
        private final int row;
        private final int col;
        private final char type;

        Tile(int row, int col, char type) {
            this.row = row;
            this.col = col;
            this.type = type;
        }

    }

    static final class Way implements Comparable<Way> {
        private final Tile tile;

        private List<Tile> prevTiles;
        private Tile prevTile;

        private int steps;

        Way(Tile tile, Way prev) {
            this.tile = tile;
            if (prev != null) {
                prevTiles = new ArrayList<>(prev.prevTiles.size() + 1);
                prevTiles.addAll(prev.prevTiles);
                prevTiles.add(prev.tile);
                prevTile = prev.tile;
                steps = prev.steps + 1;
            } else {
                prevTiles = new ArrayList<>();
                steps = 0;
            }
        }

        Way(Tile tile, Way prev, List<Tile> prevTiles) {
            this.tile = tile;
            this.prevTiles = prevTiles;
            this.prevTile = prev.tile;
            steps = prev.steps + 1;
        }

        public long getSteps() {
            return steps;
        }

        @Override
        public int compareTo(Way o) { // reverse order
            return Long.compare(o.getSteps(), getSteps());
        }

        public boolean notContains(Tile t) {
            if (prevTile == t) return false;
            return !prevTiles.contains(t);
        }
    }
}
