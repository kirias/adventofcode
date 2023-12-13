package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Pair;
import com.github.kirias.adventofcode.common.csp.CSP;
import com.github.kirias.adventofcode.common.csp.Constraint;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * There are two solutions here and any of them can be used for the first part
 * However, the csp solution takes ages for the second part, so another approach was implemented
 */
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

        public List<Spring> getSprings() {
            return springs;
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
        // return getBruteForceWithCacheSolution(SpringGroup::new);
        return getCSPSolution(SpringGroup::new);
    }

    @Override
    public long getPart2Solution() {
        // return getCSPSolution(MultipleSpringGroup::new);
        return getBruteForceWithCacheSolution(MultipleSpringGroup::new);
    }

    /*
    First solution, takes ages for the second part
     */
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

    /*
    Second solution inspired by reddit comments
     */
    private long getBruteForceWithCacheSolution(BiFunction<String, List<Integer>, SpringGroup> springCreator) {
        List<SpringGroup> springGroups = inputLines()
                .map(line -> line.split(" "))
                .map(line -> {
                    List<Integer> groups = Arrays.stream(line[1].split(","))
                            .map(Integer::parseInt)
                            .toList();
                    return springCreator.apply(line[0], groups);
                })
                .toList();
        return springGroups.stream().mapToLong(sg -> {
            long countCombinations = getCountCombinations(new LinkedList<>(sg.getSprings()), new LinkedList<>(sg.getGroupCounts()));
            return countCombinations;
        }).sum();
    }

    private HashMap<Pair<List<Spring>, List<Integer>>, Long> cache = new HashMap<>();

    private long getCountCombinationsCached(List<Spring> springs, List<Integer> brokenGroups) {
        Pair<List<Spring>, List<Integer>> cacheKey = new Pair<>(springs, brokenGroups);
        if (cache.containsKey(cacheKey)) return cache.get(cacheKey);
        Pair<List<Spring>, List<Integer>> resultKey = new Pair<>(new LinkedList<>(springs), new LinkedList<>(brokenGroups));
        long countCombinations = getCountCombinations(springs, brokenGroups);
        cache.put(resultKey, countCombinations);
        return countCombinations;
    }

    /*
    It's possible to go backwards and use ArrayList instead. But it's easier to get first element instead of last
     */
    private long getCountCombinations(List<Spring> springs, List<Integer> brokenGroups) {
        while (!springs.isEmpty() && springs.get(0) == Spring.OPERATIONAL) springs.remove(0);

        if (springs.isEmpty()) return brokenGroups.isEmpty() ? 1 : 0;

        if (springs.get(0) == Spring.UNKNOWN) {
            List<Spring> springsWithBrokenFirst = new LinkedList<>(springs);
            springsWithBrokenFirst.set(0, Spring.DAMAGED);
            List<Spring> springsWithOperFirst = new LinkedList<>(springs);
            springsWithOperFirst.set(0, Spring.OPERATIONAL);
            return getCountCombinationsCached(springsWithBrokenFirst, new LinkedList<>(brokenGroups))
                    + getCountCombinationsCached(springsWithOperFirst, new LinkedList<>(brokenGroups));
        }

        if (!brokenGroups.isEmpty()) {
            Integer firstBrokenGroup = brokenGroups.get(0);
            for (int i = 0; i < firstBrokenGroup; i++) {
                if (springs.isEmpty()) return 0;
                Spring spring = springs.get(0);
                if (spring != Spring.DAMAGED && spring != Spring.UNKNOWN) {
                    return 0;
                }
                springs.remove(0);
            }
            brokenGroups.remove(0);
        }

        if (!brokenGroups.isEmpty()) {
            if (springs.isEmpty() || springs.get(0) == Spring.DAMAGED) {
                return 0;
            } else {
                springs.remove(0);
                return getCountCombinationsCached(springs, brokenGroups);
            }
        } else {
            for (int i = 0; i < springs.size(); i++) {
                if (springs.get(i) == Spring.DAMAGED) return 0;
            }
        }
        return 1;
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
            for (Map.Entry<Integer, Spring> entry : assignment.entrySet()) {
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
