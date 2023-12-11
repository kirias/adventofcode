package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

public class Day2 extends Problem {

    static final int RED_MAX = 12;
    static final int GREEN_MAX = 13;
    static final int BLUE_MAX = 14;

    public Day2(String path) {
        super(path);
    }

    @Override
    public int getDay() {
        return 2;
    }

    @Override
    public long getPart1Solution() {
        long sum = 0;

        for (String line : inputLines().toList()) {
            boolean possible = true;
            String[] title_sets = line.split(":");
            String gameSetsStr = title_sets[1].trim();
            String[] gameSets = gameSetsStr.split(";");
            for (String gameSet : gameSets) {
                String[] reveals = gameSet.trim().split(",");
                for (String reveal : reveals) {
                    String[] count_color = reveal.trim().split(" ");
                    int count = Integer.parseInt(count_color[0]);
                    String color = count_color[1];
                    int max_for_color = switch (color) {
                        case "red" -> RED_MAX;
                        case "blue" -> BLUE_MAX;
                        case "green" -> GREEN_MAX;
                        default -> throw new RuntimeException("wrong color");
                    };
                    if (count > max_for_color) {
                        possible = false;
                        break;
                    }
                }
                if (!possible) {
                    break;
                }
            }
            if (possible) {
                sum += Integer.parseInt(title_sets[0].split(" ")[1]);
            }
        }

        return sum;
    }

    @Override
    public long getPart2Solution() {
        long sum = 0;

        for (String line : inputLines().toList()) {
            long minRed = 1;
            long minBlue = 1;
            long minGreen = 1;
            String[] title_sets = line.split(":");
            String[] gameSets = title_sets[1].trim().split(";");
            for (String gameSet : gameSets) {
                String[] reveals = gameSet.trim().split(",");
                for (String reveal : reveals) {
                    String[] count_color = reveal.trim().split(" ");
                    int count = Integer.parseInt(count_color[0]);
                    String color = count_color[1];
                    switch (color) {
                        case "red" -> minRed = Math.max(minRed, count);
                        case "blue" -> minBlue = Math.max(minBlue, count);
                        case "green" -> minGreen = Math.max(minGreen, count);
                    }

                }
            }
            sum += minRed * minBlue * minGreen;
        }
        return sum;
    }
}
