package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.LongPair;

import java.util.*;

public class Day03 extends Problem {

    public Day03(String path) {
        super(path, 3);
    }

    private Optional<Character> getChar(int pos, String line) {
        return Optional.ofNullable(line)
                .filter(l -> pos >= 0 && pos < l.length())
                .map(l -> l.charAt(pos));
    }

    private boolean isSymbol(int pos, String line) {
        return getChar(pos, line)
                .map(c -> {
                    if (c == '.') {
                        return false;
                    }
                    return !Character.isDigit(c);
                })
                .orElse(false);
    }

    private boolean isDigit(int i, String line) {
        return getChar(i, line)
                .map(Character::isDigit)
                .orElse(false);
    }

    private boolean isStar(int i, String line) {
        return getChar(i, line)
                .map(c -> c == '*')
                .orElse(false);
    }


    @Override
    public long getPart1Solution() {
        long sum = 0;

        List<String> lines = inputLines().toList();

        int width = lines.get(0).length();
        int height = lines.size();

        for (int lineIndex = 0; lineIndex < height; lineIndex++) {
            String prevLine = lineIndex > 0 ? lines.get(lineIndex - 1) : null;
            String nextLine = lineIndex < height - 1 ? lines.get(lineIndex + 1) : null;
            String line = lines.get(lineIndex);

            long numberAccumulator = 0;
            boolean connectedWithSymbol = false;
            for (int charIndex = 0; charIndex < width; charIndex++) {
                if (!Character.isDigit(line.charAt(charIndex))) {
                    if (connectedWithSymbol) {
                        sum += numberAccumulator;
                        connectedWithSymbol = false;
                    }
                    numberAccumulator = 0;
                } else {
                    if (numberAccumulator == 0) { // First digit
                        boolean prevColSymbol = isSymbol(charIndex - 1, line) ||
                                isSymbol(charIndex - 1, prevLine) ||
                                isSymbol(charIndex - 1, nextLine);
                        if (prevColSymbol) {
                            connectedWithSymbol = true;
                        }
                    }
                    if (isSymbol(charIndex, prevLine) || isSymbol(charIndex, nextLine)) {
                        connectedWithSymbol = true;
                    }
                    if (!isDigit(charIndex + 1, line)) { // Last digit
                        boolean nextColSymbol = isSymbol(charIndex + 1, line) ||
                                isSymbol(charIndex + 1, prevLine) ||
                                isSymbol(charIndex + 1, nextLine);
                        if (nextColSymbol) {
                            connectedWithSymbol = true;
                        }
                    }
                    numberAccumulator *= 10;
                    numberAccumulator += line.charAt(charIndex) - '0';
                }
            }
            if (connectedWithSymbol) {
                sum += numberAccumulator;
            }
        }

        return sum;
    }

    @Override
    public long getPart2Solution() {
        List<String> lines = inputLines().toList();
        Map<LongPair, List<Long>> gears = new HashMap<>();

        int width = lines.get(0).length();
        int height = lines.size();

        for (int lineIndex = 0; lineIndex < height; lineIndex++) {
            String prevLine = lineIndex > 0 ? lines.get(lineIndex - 1) : null;
            String nextLine = lineIndex < height - 1 ? lines.get(lineIndex + 1) : null;
            String line = lines.get(lineIndex);

            long numberAccumulator = 0;
            List<LongPair> gearCenters = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                if (!Character.isDigit(line.charAt(j))) {
                    if (!gearCenters.isEmpty()) {
                        final long numberFinal = numberAccumulator;
                        gearCenters.forEach(pair -> {
                            gears.computeIfAbsent(pair, p -> new ArrayList<>())
                                    .add(numberFinal);
                        });
                        gearCenters.clear();
                    }
                    numberAccumulator = 0;
                } else {
                    if (numberAccumulator == 0) { // First digit
                        if (isStar(j - 1, line)) {
                            gearCenters.add(new LongPair(lineIndex, j - 1));
                        }
                        if (isStar(j - 1, prevLine)) {
                            gearCenters.add(new LongPair(lineIndex - 1, j - 1));
                        }
                        if (isStar(j - 1, nextLine)) {
                            gearCenters.add(new LongPair(lineIndex + 1, j - 1));
                        }
                    }
                    if (isStar(j, prevLine)) {
                        gearCenters.add(new LongPair(lineIndex - 1, j));
                    }
                    if (isStar(j, nextLine)) {
                        gearCenters.add(new LongPair(lineIndex + 1, j));
                    }
                    if (!isDigit(j + 1, line)) { // Last digit
                        if (isStar(j + 1, line)) {
                            gearCenters.add(new LongPair(lineIndex, j + 1));
                        }
                        if (isStar(j + 1, prevLine)) {
                            gearCenters.add(new LongPair(lineIndex - 1, j + 1));
                        }
                        if (isStar(j + 1, nextLine)) {
                            gearCenters.add(new LongPair(lineIndex + 1, j + 1));
                        }
                    }
                    numberAccumulator *= 10;
                    numberAccumulator += line.charAt(j) - '0';
                }
            }
            if (!gearCenters.isEmpty()) {
                final long numberFinal = numberAccumulator;
                gearCenters.forEach(pair -> {
                    gears.computeIfAbsent(pair, p -> new ArrayList<>())
                            .add(numberFinal);
                });
                gearCenters.clear();
            }
        }
        return gears.values()
                .stream()
                .filter(l -> l.size() == 2)
                .mapToLong(l -> l.get(0) * l.get(1))
                .sum();
    }
}
