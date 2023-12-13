package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.csp.CSP;
import com.github.kirias.adventofcode.common.csp.Constraint;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 extends Problem {

    public enum Spring {
        OPERATIONAL, DAMAGED, UNKNOWN;

        public static Spring valueOf(int c) {
            return switch (c) {
                case '?' -> UNKNOWN;
                case '.' -> OPERATIONAL;
                case '#' -> DAMAGED;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            };
        }
    }

    static class MultipleSpringGroup extends SpringGroup {

        MultipleSpringGroup(String springs, List<Integer> groupCounts) {
            this.springs = IntStream.range(0, 5)
                    .mapToObj(i -> springs)
                    .collect(Collectors.joining("?"))
                    .chars()
                    .mapToObj(Spring::valueOf).toList();
            this.groupCounts = IntStream.range(0, 5)
                    .mapToObj(i -> groupCounts)
                    .flatMap(Collection::stream)
                    .toList();
        }
    }

    static class SpringGroup {

        protected List<Spring> springs;
        protected List<Integer> groupCounts;

        public SpringGroup() {
        }

        SpringGroup(String springs, List<Integer> groupCounts) {
            this.springs = springs.chars().mapToObj(Spring::valueOf).toList();
            this.groupCounts = groupCounts;
        }

        @Override
        public String toString() {
            return springs.stream()
                    .map(s -> s == Spring.UNKNOWN ? "?" : s == Spring.DAMAGED ? "#" : ".")
                    .collect(Collectors.joining());
        }

        public List<Integer> getVariables() {
            return IntStream.range(0, springs.size()).boxed().toList();
        }

        public Map<Integer, List<Spring>> getDomains() {
            Map<Integer, List<Spring>> domains = new HashMap<>();
            for (int i = 0; i < springs.size(); i++) {
                if (springs.get(i) == Spring.UNKNOWN) {
                    domains.put(i, List.of(Spring.DAMAGED, Spring.OPERATIONAL));
                } else {
                    domains.put(i, List.of(springs.get(i)));
                }
            }
            return domains;
        }

        public List<Integer> getGroupCounts() {
            return groupCounts;
        }
    }

    public Day12(String path) {
        super(path, 12);
    }

    @Override
    public long getPart1Solution() {
        return getCSPSolution(SpringGroup::new);
    }

    @Override
    public long getPart2Solution() {
        return getCSPSolution(MultipleSpringGroup::new);
    }

    private long getCSPSolution(BiFunction<String, List<Integer>, SpringGroup> springCreator) {
        List<SpringGroup> springGroups = inputLines()
                .map(line -> line.split(" "))
                .map(line -> {
                    List<Integer> groups = Arrays.stream(line[1].split(","))
                            .map(Integer::parseInt)
                            .toList();
                    return springCreator.apply(line[0], groups);
                })
                .toList();
        return springGroups.parallelStream().mapToInt(sg -> {
            List<Integer> variables = sg.getVariables();

            CSP<Integer, Spring> csp = new CSP<>(variables, sg.getDomains());
            SpringsCountingConstraint countingConstraint = new SpringsCountingConstraint(variables, sg.getGroupCounts());
            csp.addConstraint(countingConstraint);

            csp.backtrackingSearch();
            return countingConstraint.getCountValid();
        }).sum();
    }


    static class SpringsCountingConstraint extends Constraint<Integer, Spring> {

        private final List<Integer> groupCounts;
        private final int totalDamaged;
        private final int springsCount;

        private int countValid = 0;

        public SpringsCountingConstraint(List<Integer> variables, List<Integer> groupCounts) {
            super(variables);
            this.groupCounts = groupCounts;
            this.totalDamaged = groupCounts.stream().mapToInt(i -> i).sum();
            this.springsCount = variables.size();
        }

        @Override
        public boolean satisfied(Map<Integer, Spring> assignment) {
            int countDamaged = 0;
            int currentPosition = 0;
            for (Map.Entry<Integer, Spring> entry: assignment.entrySet()) {
                if (entry.getKey() > currentPosition) currentPosition = entry.getKey();
                if (entry.getValue() == Spring.DAMAGED) countDamaged++;
            }
            if (countDamaged > totalDamaged) {
                return false;
            }

            boolean completed = assignment.size() == springsCount;

            List<Integer> currentGroupCounts = new ArrayList<>();
            int currentGroupCount = 0;
            for (int i = 0; i < currentPosition + 1; i++) {
                Spring spring = assignment.get(i);
                if (spring == Spring.DAMAGED) {
                    currentGroupCount++;
                } else {
                    if (currentGroupCount != 0) {
                        currentGroupCounts.add(currentGroupCount);
                        currentGroupCount = 0;
                    }
                }
            }
            if (currentGroupCount != 0) {
                currentGroupCounts.add(currentGroupCount);
            }
            int countGroups = currentGroupCounts.size();
            if (countGroups > this.groupCounts.size()) {
                return false;
            }
            if (completed && countGroups != this.groupCounts.size()) {
                return false;
            }
            for (int i = 0; i < countGroups; i++) {
                if (i < countGroups - 1) {
                    if (!currentGroupCounts.get(i).equals(groupCounts.get(i))) {
                        return false;
                    }
                } else {
                    if (completed) {
                        if (currentGroupCounts.get(i).equals(this.groupCounts.get(i))) {
                            // we don't need to find one valid sequence,
                            // so increment the count and go to the next one
                            countValid++;
                        }
                        return false;
                    } else {
                        if (currentGroupCounts.get(i) > this.groupCounts.get(i)) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }

        public int getCountValid() {
            return countValid;
        }
    }
}
