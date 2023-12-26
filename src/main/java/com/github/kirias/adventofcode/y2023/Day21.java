package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.Matrix;

import java.util.*;
import java.util.stream.Collectors;

public class Day21 extends Problem {

    static final long PART_2_STEPS = 26501365;

    public Day21(String path) {
        super(path, 21);
    }

    record Tile(int row, int col, char type) {
    }

    @Override
    public long getPart1Solution() {
        Matrix<Tile> matrix = new Matrix<>();
        Tile start = null;
        int row = 0;
        for (String line : inputLines().toList()) {
            List<Tile> tiles = new ArrayList<>();
            int col = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i) == 'S' ? '.' : line.charAt(i);
                Tile t = new Tile(row, col, c);
                tiles.add(t);
                if (line.charAt(i) == 'S') start = t;
                col++;
            }
            matrix.addRow(tiles);
            row++;
        }

        Set<Tile> visited = new HashSet<>();
        Queue<Tile> toExplore = new LinkedList<>();
        Queue<Tile> newExplored = new LinkedList<>();
        toExplore.add(start);
        visited.add(start);

        for (int i = 0; i < 64; i += 2) {
            while (!toExplore.isEmpty()) {
                Tile current = toExplore.poll();
                Set<Tile> next = getNext(matrix, current, 2);
                next.stream().filter(n -> !visited.contains(n)).forEach(n -> {
                    visited.add(n);
                    newExplored.add(n);
                });
            }
            if (newExplored.isEmpty()) break;
            toExplore.addAll(newExplored);
            newExplored.clear();
        }

        return visited.size();
    }

    private Set<Tile> getNext(Matrix<Tile> matrix, Tile tile, int steps) {
        Set<Tile> next = new HashSet<>();
        if (steps == 1) {
            for (Direction d : Direction.all()) {
                matrix.getIfExist(tile.row + d.rowOffset(), tile.col + d.colOffset()).filter(t -> t.type != '#').ifPresent(next::add);
            }
            return next;
        } else {
            return getNext(matrix, tile, 1).stream().flatMap(n -> getNext(matrix, n, steps - 1).stream()).collect(Collectors.toSet());
        }
    }

    @Override
    public long getPart2Solution() {
        Matrix<Tile> matrix = new Matrix<>();
        Tile start = null;
        int row = 0;
        for (String line : inputLines().toList()) {
            List<Tile> tiles = new ArrayList<>();
            int col = 0;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i) == 'S' ? '.' : line.charAt(i);
                Tile t = new Tile(row, col, c);
                tiles.add(t);
                if (line.charAt(i) == 'S') start = t;
                col++;
            }
            matrix.addRow(tiles);
            row++;
        }


        long countToVisitInEvenMatrix = getCountTilesToVisit(matrix, start);
        long countToVisitInOddMatrix = getCountTilesToVisit(matrix, matrix.get(start.row + 1, start.col));

        Tile leftCenter = matrix.get(matrix.height() / 2, 0);
        Tile topCenter = matrix.get(0, matrix.width() / 2);
        Tile rightCenter = matrix.get(matrix.height() / 2, matrix.width() - 1);
        Tile bottomCenter = matrix.get(matrix.height() - 1, matrix.width() / 2);

        int stepsToBorder = start.col;
        int stepsInsideMatrix = matrix.width();


        long stepsBeforeLastMatrix = (PART_2_STEPS - stepsToBorder) % stepsInsideMatrix;
        long stepsBeforeDiagonalMatrix = (PART_2_STEPS - stepsToBorder - stepsToBorder) % stepsInsideMatrix - 1;
        long countFullMatrixesInRow = (PART_2_STEPS - stepsToBorder) / stepsInsideMatrix;

        long evenTotal = 0;
        long oddTotal = 0;
        boolean even = false;
        for (long i = 1; i <= countFullMatrixesInRow; i++) {
            long countOuter = i * (i + 1) * 4 / 2;
            long countInner = (i - 1) * i * 4 / 2;
            if (even) {
                evenTotal += countOuter - countInner;
            } else {
                oddTotal += countOuter - countInner;
            }
            even = ! even;
        }
        boolean lastFullTypeEven = ! even;

        long countLastLeft = getCountTilesToVisit(matrix, rightCenter, stepsBeforeLastMatrix, !lastFullTypeEven);
        long countLastRight = getCountTilesToVisit(matrix, leftCenter, stepsBeforeLastMatrix, !lastFullTypeEven);

        long countLastTop = getCountTilesToVisit(matrix, bottomCenter, stepsBeforeLastMatrix, !lastFullTypeEven);
        long countLastBottom = getCountTilesToVisit(matrix, topCenter, stepsBeforeLastMatrix, !lastFullTypeEven);

        long countPreLastLeft = getCountTilesToVisit(matrix, rightCenter, stepsBeforeLastMatrix + stepsInsideMatrix - 1, lastFullTypeEven);
        long countPreLastRight = getCountTilesToVisit(matrix, leftCenter, stepsBeforeLastMatrix + stepsInsideMatrix - 1, lastFullTypeEven);
        long countPreLastTop = getCountTilesToVisit(matrix, bottomCenter, stepsBeforeLastMatrix + stepsInsideMatrix - 1, lastFullTypeEven);
        long countPreLastBottom = getCountTilesToVisit(matrix, topCenter, stepsBeforeLastMatrix + stepsInsideMatrix - 1, lastFullTypeEven);

        long preLastDiff = 0;
        if (lastFullTypeEven) {
            preLastDiff = countToVisitInEvenMatrix * -4 + countPreLastLeft + countPreLastRight + countPreLastTop + countPreLastBottom;
        } else {
            preLastDiff = countToVisitInOddMatrix * -4 + countPreLastLeft + countPreLastRight + countPreLastTop + countPreLastBottom;
        }

        long countLD = getCountTilesToVisit(matrix, matrix.get(matrix.height() - 1, 0), stepsBeforeDiagonalMatrix, lastFullTypeEven);
        long countLU = getCountTilesToVisit(matrix, matrix.get(0, 0), stepsBeforeDiagonalMatrix, lastFullTypeEven);
        long countRU = getCountTilesToVisit(matrix, matrix.get(0, matrix.width() - 1), stepsBeforeDiagonalMatrix, lastFullTypeEven);
        long countRD = getCountTilesToVisit(matrix, matrix.get(matrix.height() - 1, matrix.width() - 1), stepsBeforeDiagonalMatrix, lastFullTypeEven);

        long countPreLD = getCountTilesToVisit(matrix, matrix.get(matrix.height() - 1, 0), stepsBeforeDiagonalMatrix + stepsInsideMatrix, !lastFullTypeEven);
        long countPreLU = getCountTilesToVisit(matrix, matrix.get(0, 0), stepsBeforeDiagonalMatrix + stepsInsideMatrix, !lastFullTypeEven);
        long countPreRU = getCountTilesToVisit(matrix, matrix.get(0, matrix.width() - 1), stepsBeforeDiagonalMatrix + stepsInsideMatrix, !lastFullTypeEven);
        long countPreRD = getCountTilesToVisit(matrix, matrix.get(matrix.height() - 1, matrix.width() - 1), stepsBeforeDiagonalMatrix + stepsInsideMatrix, !lastFullTypeEven);

        long replacedPreDiagonal = lastFullTypeEven ? countToVisitInEvenMatrix : countToVisitInOddMatrix;

        return evenTotal * countToVisitInEvenMatrix + oddTotal * countToVisitInOddMatrix + countToVisitInEvenMatrix +
                countLastLeft + countLastRight + countLastBottom + countLastTop +
                (countLD + countRD + countLU + countRU) * countFullMatrixesInRow +
                (countPreLD + countPreRD + countPreLU + countPreRU) * (countFullMatrixesInRow - 1) +
                preLastDiff -
                replacedPreDiagonal * 4 * (countFullMatrixesInRow - 1);
    }

    private long getCountTilesToVisit(Matrix<Tile> matrix, Tile start) {
        Set<Tile> nextAvailable = getNext(matrix, start, 1);

        Queue<Tile> toExplore = new LinkedList<>(nextAvailable);
        Set<Tile> visited = new HashSet<>(nextAvailable);
        Queue<Tile> newExplored = new LinkedList<>();

        while(true) {
            while (!toExplore.isEmpty()) {
                Tile current = toExplore.poll();
                Set<Tile> next = getNext(matrix, current, 2);
                next.stream().filter(n -> !visited.contains(n)).forEach(n -> {
                    visited.add(n);
                    newExplored.add(n);
                });
            }
            if (newExplored.isEmpty()) break;
            toExplore.addAll(newExplored);
            newExplored.clear();
        }
        return visited.size();
    }

    private long getCountTilesToVisit(Matrix<Tile> matrix, Tile start, long stepsLimit, boolean even) {
        if (stepsLimit < 1) return 0;
        Set<Tile> visited = new HashSet<>();
        Queue<Tile> toExplore = new LinkedList<>();
        Queue<Tile> newExplored = new LinkedList<>();

        if (even) {
            toExplore.add(start);
            visited.add(start);
        } else {
            Set<Tile> next = getNext(matrix, start, 1);
            next.forEach(n -> {
                toExplore.add(n);
                visited.add(n);
            });
            stepsLimit --;
        }

        for (int i = 0; i < stepsLimit - 1; i += 2) {
            while (!toExplore.isEmpty()) {
                Tile current = toExplore.poll();
                Set<Tile> next = getNext(matrix, current, 2);
                next.stream().filter(n -> !visited.contains(n)).forEach(n -> {
                    visited.add(n);
                    newExplored.add(n);
                });
            }
            if (newExplored.isEmpty()) break;
            toExplore.addAll(newExplored);
            newExplored.clear();
        }
        return visited.size();
    }
}
