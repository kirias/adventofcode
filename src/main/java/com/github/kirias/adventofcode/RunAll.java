package com.github.kirias.adventofcode;

import com.github.kirias.adventofcode.y2023.*;

public class RunAll {

    public static void main(String[] args) {
        Day01 day01 = new Day01("/y2023/Day1.txt");
        Day02 day02 = new Day02("/y2023/Day2.txt");
        Day03 day03 = new Day03("/y2023/Day3.txt");
        Day04 day04 = new Day04("/y2023/Day4.txt");
        Day05 day05 = new Day05("/y2023/Day5.txt");
        Day06 day06 = new Day06("/y2023/Day6.txt");
        Day07 day07 = new Day07("/y2023/Day7.txt");
        Day09 day09 = new Day09("/y2023/Day9.txt");
        Day11 day11 = new Day11("/y2023/Day11.txt");
        Day12 day12 = new Day12("/y2023/Day12.txt");
        Day13 day13 = new Day13("/y2023/Day13.txt");
        Day14 day14 = new Day14("/y2023/Day14.txt");
        Day15 day15 = new Day15("/y2023/Day15.txt");

        printSolution(day01); // 54644 53348
        printSolution(day02); // 2239 83435
        printSolution(day03); // 532445 79842967
        printSolution(day04); // 22193 5625994
        printSolution(day05); // 324724204 104070862
        printSolution(day06); // 771628 27363861
        printSolution(day07); // 248179786 247885995
        printSolution(day09); // 1884768153 1031
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
