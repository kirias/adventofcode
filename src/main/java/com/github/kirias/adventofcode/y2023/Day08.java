package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.MathUtil;
import com.github.kirias.adventofcode.common.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Day08 extends Problem {

    public Day08(String path) {
        super(path, 8);
    }

    @Override
    public long getPart1Solution() {
        String instructions = inputLines().findFirst().get();
        Map<String, Pair<String, String>> nodes = inputLines()
                .skip(2)
                .map(line -> {
                    String from = line.substring(0, 3);
                    String left = line.substring(7, 10);
                    String right = line.substring(12, 15);
                    return new Pair<>(from, new Pair<>(left, right));
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        int stepsCount = 0;
        int instructionsPointer = -1;
        String currentNode = "AAA";

        while (!currentNode.equals("ZZZ")) {
            stepsCount++;
            instructionsPointer = (instructionsPointer + 1) % instructions.length();

            char direction = instructions.charAt(instructionsPointer);
            if (direction == 'L') {
                currentNode = nodes.get(currentNode).getLeft();
            } else {
                currentNode = nodes.get(currentNode).getRight();
            }
        }
        return stepsCount;
    }

    @Override
    public long getPart2Solution() {
        String instructionsStr = inputLines().findFirst().get();

        List<Instruction> instructions = instructionsStr.chars()
                .mapToObj(c -> new Instruction(c == 'L'))
                .toList();

        List<String> startNodes = new ArrayList<>();

        Map<String, Pair<String, String>> nodes = inputLines()
                .skip(2)
                .map(line -> {
                    String from = line.substring(0, 3);
                    String left = line.substring(7, 10);
                    String right = line.substring(12, 15);
                    if (from.charAt(2) == 'A') {
                        startNodes.add(from);
                    }
                    return new Pair<>(from, new Pair<>(left, right));
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        int stepsCount = 0;
        int instructionsPointer = -1;

        int nodesCount = startNodes.size();

        while (true) {
            stepsCount++;
            instructionsPointer = (instructionsPointer + 1) % instructionsStr.length();

            if (stepsCount % 100 == 0) {
                long countCommon = instructions.stream().filter(i -> i.isCommon(nodesCount)).count();
                if (countCommon > 0) {
                    break;
                }
            }
            Instruction instruction = instructions.get(instructionsPointer);
            for (int currentNodeIndex = 0; currentNodeIndex < nodesCount; currentNodeIndex++) {
                String currentNode = startNodes.get(currentNodeIndex);
                String nextNode;
                if (instruction.left) {
                    nextNode = nodes.get(currentNode).getLeft();
                    startNodes.set(currentNodeIndex, nextNode);
                } else {
                    nextNode = nodes.get(currentNode).getRight();
                    startNodes.set(currentNodeIndex, nextNode);
                }
                if (nextNode.charAt(2) == 'Z') {
                    instruction.addReachedZ(currentNodeIndex, stepsCount, nextNode);
                }
            }
        }

        Instruction instruction = instructions.stream().filter(i -> i.isCommon(nodesCount)).findFirst().get();

        List<Long> cycleLengths = new ArrayList<>();

        instruction.cycles.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().startNodeIndex))
                .forEach(es -> {
                    if (es.getValue().stepsBeforeCycle != es.getValue().cycleLength) {
                        throw new RuntimeException("Can handle the only cases when stepsBeforeCycle == cycleLength");
                    }
                    cycleLengths.add(es.getValue().cycleLength);
                });

        long lcm = 1;
        for (Long cycleLength : cycleLengths) {
            lcm = MathUtil.lcm(lcm, cycleLength);
        }

        return lcm;
    }


    static class Instruction {
        boolean left;
        Map<NodeReach, Long> nodeReachMap = new HashMap<>();
        Map<NodeReach, NodeReachCycle> cycles = new HashMap<>();

        public Instruction(boolean left) {
            this.left = left;
        }

        public void addReachedZ(int startNodeIndex, long stepsCount, String znode) {
            NodeReach key = new NodeReach(startNodeIndex, znode);
            if (nodeReachMap.containsKey(key) && !cycles.containsKey(key)) {
                Long stepsBeforeCycle = nodeReachMap.get(key);
                cycles.put(key, new NodeReachCycle(stepsBeforeCycle, stepsCount - stepsBeforeCycle));
            } else if (!nodeReachMap.containsKey(key)) {
                nodeReachMap.put(key, stepsCount);
            }

        }

        public boolean isCommon(int nodeCount) {
            return cycles.keySet()
                    .stream()
                    .map(NodeReach::startNodeIndex)
                    .distinct()
                    .count() == nodeCount;
        }
    }

    record NodeReach(int startNodeIndex, String znode) {
    }

    record NodeReachCycle(long stepsBeforeCycle, long cycleLength) {
    }
}
