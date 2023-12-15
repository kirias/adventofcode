package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day06 extends Problem {

    public Day06(String path) {
        super(path, 6);
    }

    @Override
    public long getPart1Solution() {
        List<Long> times = new ArrayList<>();
        List<Long> distances = new ArrayList<>();

        Scanner scanner = inputScanner();
        scanner.next();
        while (scanner.hasNextLong()) {
            times.add(scanner.nextLong());
        }
        scanner.next();
        while (scanner.hasNextLong()) {
            distances.add(scanner.nextLong());
        }

        return getSolution(times, distances);
    }


    @Override
    public long getPart2Solution() {
        Scanner scanner = inputScanner();
        scanner.next();
        long time = Long.parseLong(scanner.nextLine().replace(" ", ""));
        scanner.next();
        long distance = Long.parseLong(scanner.nextLine().replace(" ", ""));

        return getSolution(List.of(time), List.of(distance));
    }

    private long getSolution(List<Long> times, List<Long> dist) {
        long resultsMultiplied = 1;

        for (int i = 0; i < times.size(); i++) {
            Long time = times.get(i);
            Long distance = dist.get(i);

            long rangeStart = 1L;
            long rangeEnd = time / 2;

            while (rangeEnd - rangeStart > 1) {
                long rangeMiddle = (rangeEnd - rangeStart) / 2 + rangeStart;
                double result = (time - rangeMiddle) * rangeMiddle;
                if (result >= distance) {
                    rangeEnd = rangeMiddle;
                } else {
                    rangeStart = rangeMiddle;
                }
            }

            long accelerateFrom = rangeEnd;

            rangeStart = time / 2;
            rangeEnd = time;

            while (rangeEnd - rangeStart > 1) {
                long rangeMiddle = (rangeEnd - rangeStart) / 2 + rangeStart;
                double result = (time - rangeMiddle) * rangeMiddle;
                if (result >= distance) {
                    rangeStart = rangeMiddle;
                } else {
                    rangeEnd = rangeMiddle;
                }
            }

            long accelerateTo = rangeStart;

            resultsMultiplied *= accelerateTo - accelerateFrom + 1;
        }

        return resultsMultiplied;
    }
}
