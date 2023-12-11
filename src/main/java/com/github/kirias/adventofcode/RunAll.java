package com.github.kirias.adventofcode;

import com.github.kirias.adventofcode.y2023.*;

public class RunAll {

    public static void main(String[] args) {
        Day1 day1 = new Day1("/y2023/Day1.txt");
        Day2 day2 = new Day2("/y2023/Day2.txt");
        Day7 day7 = new Day7("/y2023/Day7.txt");
        Day9 day9 = new Day9("/y2023/Day9.txt");
        Day11 day11 = new Day11("/y2023/Day11.txt");

        printSolution(day1); // 54644 53348
        printSolution(day2); // 2239 83435
        printSolution(day7); // 248179786 247885995
        printSolution(day9); // 1884768153 1031
        printSolution(day11); // 10494813 840988812853
    }

    static void printSolution(Problem problem) {
        System.out.printf("Day %d part 1 solution: %d%n", problem.getDay(), problem.getPart1Solution());
        System.out.printf("Day %d part 2 solution: %d%n", problem.getDay(), problem.getPart2Solution());
    }
}
