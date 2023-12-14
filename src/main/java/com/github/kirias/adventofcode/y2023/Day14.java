package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day14 extends Problem {

    public static final long MAX_CYCLES = 1000_000_000L;

    public Day14(String path) {
        super(path, 14);
    }

    @Override
    public long getPart1Solution() {
        List<String> rockLines = inputLines().toList();
        long width = rockLines.get(0).length();
        long height = rockLines.size();

        long weight = 0;

        for (int col = 0; col < width; col++) {
            long edge = 0;
            for (int row = 0; row < height; row++) {
                char symbol = rockLines.get(row).charAt(col);
                if (symbol == '#') {
                    edge = row + 1;
                } else if (symbol == 'O') {
                    weight += height - edge;
                    edge++;
                }
            }
        }

        return weight;
    }

    Map<List<BitSet>, Long> prevCycles = new HashMap<>();

    enum Rock {
        ROUND, EDGE, EMPTY
    }

    @Override
    public long getPart2Solution() {
        List<List<Rock>> rocks = inputLines()
                .map(line -> line.chars()
                        .mapToObj(c -> c == '#' ? Rock.EDGE : c == 'O' ? Rock.ROUND : Rock.EMPTY)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        int width = rocks.get(0).size();
        int height = rocks.size();

        for (long cycle = 0; cycle < MAX_CYCLES; cycle++) {
            for (int i = 0; i < width; i++) {
                tiltToTop(rocks, i);
            }
            for (int i = 0; i < height; i++) {
                tiltToLeft(rocks.get(i));
            }
            for (int i = 0; i < width; i++) {
                tiltToBottom(rocks, i);
            }
            for (int i = 0; i < height; i++) {
                tiltToRight(rocks.get(i));
            }

            List<BitSet> rockPositions = bitMasks(rocks);
            if (prevCycles.containsKey(rockPositions)) {
                long prevCycle = prevCycles.get(rockPositions);
                long diff = cycle - prevCycle;

                cycle = MAX_CYCLES - ((MAX_CYCLES - cycle) % diff);

                // we need only first match, avoid next matches
                prevCycles.clear();
            } else {
                prevCycles.put(rockPositions, cycle);
            }
        }

        return calcWeight(rocks);
    }

    private long calcWeight(List<List<Rock>> rocks) {
        long weight = 0;
        int width = rocks.get(0).size();
        int height = rocks.size();

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                Rock symbol = rocks.get(row).get(col);
                if (symbol == Rock.ROUND) {
                    weight += height - row;
                }
            }
        }
        return weight;
    }

    private List<BitSet> bitMasks(List<List<Rock>> rocks) {
        return rocks.stream()
                .map(this::bitMask)
                .collect(Collectors.toList());
    }

    private BitSet bitMask(List<Rock> rock) {
        BitSet bitMask = new BitSet();
        for (int i = 0; i < rock.size(); i++) {
            if (rock.get(i) == Rock.ROUND) {
                bitMask.set(i);
            }
        }
        return bitMask;
    }

    private void printRocks(List<List<Rock>> rocks) {
        for (List<Rock> rockLine : rocks) {
            for (Rock rock : rockLine) {
                if (rock == Rock.ROUND) System.out.print('O');
                if (rock == Rock.EDGE) System.out.print('#');
                if (rock == Rock.EMPTY) System.out.print('.');
            }
            System.out.println();
        }
    }

    private void tiltToLeft(List<Rock> rocks) {
        int edge = 0;
        for (int col = 0; col < rocks.size(); col++) {
            if (rocks.get(col) == Rock.EDGE) {
                edge = col + 1;
            } else if (rocks.get(col) == Rock.ROUND) {
                if (col != edge) {
                    rocks.set(edge, Rock.ROUND);
                    rocks.set(col, Rock.EMPTY);
                }
                edge++;
            }
        }
    }

    private void tiltToRight(List<Rock> rocks) {
        int edge = rocks.size() - 1;
        for (int col = rocks.size() - 1; col >= 0; col--) {
            if (rocks.get(col) == Rock.EDGE) {
                edge = col - 1;
            } else if (rocks.get(col) == Rock.ROUND) {
                if (col != edge) {
                    rocks.set(edge, Rock.ROUND);
                    rocks.set(col, Rock.EMPTY);
                }
                edge--;
            }
        }
    }

    private void tiltToTop(List<List<Rock>> rocks, int col) {
        int edge = 0;
        for (int row = 0; row < rocks.size(); row++) {
            if (rocks.get(row).get(col) == Rock.EDGE) {
                edge = row + 1;
            } else if (rocks.get(row).get(col) == Rock.ROUND) {
                if (row != edge) {
                    rocks.get(edge).set(col, Rock.ROUND);
                    rocks.get(row).set(col, Rock.EMPTY);
                }
                edge++;
            }
        }
    }

    private void tiltToBottom(List<List<Rock>> rocks, int col) {
        int edge = rocks.size() - 1;
        for (int row = rocks.size() - 1; row >= 0; row--) {
            if (rocks.get(row).get(col) == Rock.EDGE) {
                edge = row - 1;
            } else if (rocks.get(row).get(col) == Rock.ROUND) {
                if (row != edge) {
                    rocks.get(edge).set(col, Rock.ROUND);
                    rocks.get(row).set(col, Rock.EMPTY);
                }
                edge--;
            }
        }
    }
}
