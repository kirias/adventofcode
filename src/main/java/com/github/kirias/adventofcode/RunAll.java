package com.github.kirias.adventofcode;

import com.github.kirias.adventofcode.y2023.*;

public class RunAll {

    public static void main(String[] args) {
        Day1 day1 = new Day1("/y2023/Day1.txt");
        Day2 day2 = new Day2("/y2023/Day2.txt");
        Day3 day3 = new Day3("/y2023/Day3.txt");
        Day4 day4 = new Day4("/y2023/Day4.txt");
        Day5 day5 = new Day5("/y2023/Day5.txt");
        Day7 day7 = new Day7("/y2023/Day7.txt");
        Day9 day9 = new Day9("/y2023/Day9.txt");
        Day11 day11 = new Day11("/y2023/Day11.txt");
        Day12 day12 = new Day12("/y2023/Day12.txt");
        Day13 day13 = new Day13("/y2023/Day13.txt");
        Day14 day14 = new Day14("/y2023/Day14.txt");
        Day15 day15 = new Day15("/y2023/Day15.txt");

        printSolution(day1); // 54644 53348
        printSolution(day2); // 2239 83435
        printSolution(day3); // 532445 79842967
        printSolution(day4); // 22193 5625994
        printSolution(day5); // 324724204 104070862
        printSolution(day7); // 248179786 247885995
        printSolution(day9); // 1884768153 1031
        printSolution(day11); // 10494813 840988812853
        printSolution(day12); // 8270 204640299929836
        printSolution(day13); // 43614 36771
        printSolution(day14); // 108826 99291
        printSolution(day15); // 509152 244403
    }

    static void printSolution(Problem problem) {
        System.out.printf("Day %d part 1 solution: %d%n", problem.getDay(), problem.getPart1Solution());
        System.out.printf("Day %d part 2 solution: %d%n", problem.getDay(), problem.getPart2Solution());
    }
}
