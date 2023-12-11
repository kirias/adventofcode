package com.github.kirias.adventofcode;

import com.github.kirias.adventofcode.y2023.Day9;

public class RunAll {

    public static void main(String[] args) {
        Day9 day9 = new Day9("/y2023/Day9.txt");

        printSolution(day9); // 1884768153 1031
    }

    static void printSolution(Problem problem) {
        System.out.printf("Day %d part 1 solution: %d%n", problem.getDay(), problem.getPart1Solution());
        System.out.printf("Day %d part 2 solution: %d%n", problem.getDay(), problem.getPart2Solution());
    }
}
