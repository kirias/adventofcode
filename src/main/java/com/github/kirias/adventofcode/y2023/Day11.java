package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 extends Problem {

    public Day11(String path) {
        super(path);
    }

    @Override
    public int getDay() {
        return 11;
    }

    @Override
    public long getPart1Solution() {
        return calculateLengths(2);
    }

    @Override
    public long getPart2Solution() {
        return calculateLengths(1_000_000);
    }

    private long calculateLengths(int expansionFactor) {
        int width = 0;
        int height = 0;
        List<Universe> universes = new ArrayList<>();

        for (String line : inputLines().toList()) {
            width = line.length();
            for (int i = 0; i < width; i++) {
                if (line.charAt(i) == '#') {
                    universes.add(new Universe(height, i));
                }
            }
            height++;

        }

        Set<Integer> doubledCols = IntStream.range(0, width)
                .boxed().collect(Collectors.toSet());
        universes.stream().map(u -> u.col).toList().forEach(doubledCols::remove);

        Set<Integer> doubledRows = IntStream.range(0, height)
                .boxed().collect(Collectors.toSet());
        universes.stream().map(u -> u.row).toList().forEach(doubledRows::remove);

        long sum = 0;

        for (int i = 0; i < universes.size() - 1; i++) {
            for (int j = i + 1; j < universes.size(); j++) {
                Universe universe1 = universes.get(i);
                Universe universe2 = universes.get(j);
                sum += Math.abs(universe1.col - universe2.col);
                sum += Math.abs(universe1.row - universe2.row);
                sum += (expansionFactor - 1) * doubledRows.stream()
                        .filter(r -> r > Math.min(universe1.row, universe2.row) &&
                                r < Math.max(universe1.row, universe2.row))
                        .count();
                sum += (expansionFactor - 1) * doubledCols.stream()
                        .filter(c -> c > Math.min(universe1.col, universe2.col) &&
                                c < Math.max(universe1.col, universe2.col))
                        .count();
            }
        }
        return sum;
    }

    record Universe(int row, int col) {}
}
