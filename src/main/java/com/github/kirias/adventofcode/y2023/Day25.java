package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Pair;

import java.util.*;
import java.util.function.Function;

public class Day25 extends Problem {
    public Day25(String path) {
        super(path, 25);
    }

    @Override
    public long getPart1Solution() {
        Map<Pair<String, String>, Integer> edges = new HashMap<>();
        Set<String> distinctNodes = new HashSet<>();
        Map<String, Set<String>> connections = new HashMap<>();
        inputLines().forEach(line -> {
            Scanner scanner = new Scanner(line).useDelimiter("[: ]+");
            String sourceNode = scanner.next();
            distinctNodes.add(sourceNode);
            while (scanner.hasNext()) {
                String next = scanner.next();
                distinctNodes.add(next);
                edges.put(new Pair<>(sourceNode, next), 0);
                Set<String> nodeConenctions;
                if (connections.containsKey(sourceNode)) {
                    nodeConenctions = connections.get(sourceNode);
                } else {
                    nodeConenctions = new HashSet<>();
                }
                nodeConenctions.add(next);
                connections.put(sourceNode, nodeConenctions);

                if (connections.containsKey(next)) {
                    nodeConenctions = connections.get(next);
                } else {
                    nodeConenctions = new HashSet<>();
                }
                nodeConenctions.add(sourceNode);
                connections.put(next, nodeConenctions);
            }
        });
        List<String> nodes = new ArrayList<>(distinctNodes);

        Set<Pair<String, String>> countPassesTotal = new HashSet<>();

        // connections =  1529
        // each side ~ 1529 / 2
        // min for connecting path ~ (1529 / 2) / 3

        int startNode = 0;
        boolean first = true;
        List<String> checked = new ArrayList<>();
        int removed = 0;

        Map<String, Set<String>> connectionsOriginal = new HashMap<>(connections);

        Pair<String, String> lastRemoved = null;


        do {
            Map<Pair<String, String>, Integer> countPasses = new HashMap<>();

            String node1 = nodes.get(startNode);
            checked.add(node1);

            for (int j = 0; j < nodes.size(); j++) {

                String node2 = nodes.get(j);


                if (node1.equals(node2)) continue;

                Path path = path(node1, node2, connections::get);

                List<String> pathNodes = path.getPath();
                for (int i = 0; i < pathNodes.size() - 1; i++) {
                    Pair<String, String> key;
                    if (countPasses.containsKey(new Pair<>(pathNodes.get(i), pathNodes.get(i + 1)))) {
                        key = new Pair<>(pathNodes.get(i), pathNodes.get(i + 1));
                    } else if (countPasses.containsKey(new Pair<>(pathNodes.get(i + 1), pathNodes.get(i)))) {
                        key = new Pair<>(pathNodes.get(i + 1), pathNodes.get(i));
                    } else {
                        key = new Pair<>(pathNodes.get(i), pathNodes.get(i + 1));
                        countPasses.put(key, 0);
                    }
                    countPasses.put(key, countPasses.get(key) + 1);
                }
            }
            if (first) {
                countPassesTotal.addAll(countPasses.keySet());
                first = false;
            }
            final int removedFinal = removed;
            countPasses.entrySet().stream().filter(e -> e.getValue() < nodes.size() / (2 * (3 - removedFinal) * 20) )
                    .forEach(e -> {
                        countPassesTotal.remove(e.getKey());
                        countPassesTotal.remove(e.getKey().swap());
                    });
            String nextNode = countPasses.entrySet().stream()
                    .filter(e -> !checked.contains(e.getKey().getLeft()))
                    .min(Map.Entry.comparingByValue())
                    .map(e -> e.getKey().getLeft()).get();
//            startNode = nodes.indexOf(nextNode);
            startNode = new Random().nextInt(nodes.size());
            System.out.println(countPassesTotal.size() + " next start node " + nodes.get(startNode));

            if (countPassesTotal.size() <= 3 - removed) {
                countPassesTotal.stream().limit(1).forEach(connection -> {
                    connections.get(connection.getLeft()).remove(connection.getRight());
                    connections.get(connection.getRight()).remove(connection.getLeft());
                    System.out.println("Removing node " + connection.getLeft() + " " + connection.getRight());
                });
                removed ++;
                lastRemoved = countPassesTotal.stream().findFirst().get();
                countPassesTotal.clear();
                first = true;
            }
        } while (removed < 3);

        System.out.println(countConnected(lastRemoved.getRight(), connections));
        System.out.println(countConnected(lastRemoved.getLeft(), connections));

//        798
//        731

        return 0;
    }

    int countConnected(String node, Map<String, Set<String>> connections) {
        Set<String> visited = new HashSet<>();

        LinkedList<String> toVisit = new LinkedList<>();
        toVisit.add(node);

        while (!toVisit.isEmpty()) {
            String current = toVisit.poll();
            visited.add(current);
            connections.get(current).stream()
                    .filter(c -> !visited.contains(c))
                    .forEach(toVisit::add);
        }
        return visited.size();
    }


    Path path(String startNode, String endNode, Function<String, Set<String>> nodeConnections) {
        List<String> prevNodes;
        Path first = new Path(startNode, null, new ArrayList<>());
        Queue<Path> toExplore = new PriorityQueue<>();
        toExplore.add(first);
        while (!toExplore.isEmpty()) {
            Path current = toExplore.poll();

            List<String> connections;
            try {
                connections = nodeConnections.apply(current.node)
                        .stream()
                        .filter(c -> !current.contains(c))
                        .toList();
            } catch (Exception e) {
                System.out.println("exception for node " + current.node);
                throw  e;
            }

            prevNodes = new ArrayList<>(current.prevNodes.size() + 1);
            prevNodes.addAll(current.prevNodes);
            prevNodes.add(current.node);
            for (String connection : connections) {
                Path next = new Path(connection, current, prevNodes);
                if (connection.equals(endNode)) {
                    return next;
                } else {
                    toExplore.add(next);
                }
            }
        }
        return null;
    }

    public static class Path implements Comparable<Path> {
        String node;
        Path prev;

        int score;
        List<String> prevNodes;

        public Path(String node, Path prev, List<String> prevNodes) {
            this.node = node;
            this.prev = prev;
            this.prevNodes = prevNodes;
            if (prev == null) {
                score = 0;
            } else {
                score = prev.score() + 1;
            }
        }

        public int score() {
            return score;
        }

        public List<String> getPath() {
            List<String> nodes = new ArrayList<>();
            nodes.addAll(prevNodes);
            nodes.add(node);
            return nodes;
        }
        public boolean contains(String node) {
            if (node.equals(this.node)) return true;
            return prevNodes.contains(node);
        }

        @Override
        public int compareTo(Path o) {
            return Integer.compare(score(), o.score());
        }
    }


    @Override
    public long getPart2Solution() {
        return 0;
    }
}
