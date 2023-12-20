package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 extends Problem {

    public Day19(String path) {
        super(path, 19);
    }

    @Override
    public long getPart1Solution() {
        Map<String, Workflow> workflows = new HashMap<>();
        List<Part> parts = new ArrayList<>();

        inputLines().forEach(line -> {
            if (line.isEmpty()) return;
            if (!line.startsWith("{")) {
                Workflow workflow = new Workflow(line);
                workflows.put(workflow.name, workflow);
            } else {
                parts.add(new Part(line));
            }
        });

        long sum = 0;

        for (Part part : parts) {
            Workflow workflow = workflows.get("in");
            while (true) {
                String target = workflow.calcTarget(part);
                if (target.equals("R")) break;
                if (target.equals("A")) {
                    sum += part.categories[0] + part.categories[1] + part.categories[2] + part.categories[3];
                    break;
                }
                workflow = workflows.get(target);
            }
        }

        return sum;
    }

    @Override
    public long getPart2Solution() {
        Map<String, Workflow> workflows = new HashMap<>();

        inputLines().forEach(line -> {
            if (line.isEmpty()) return;
            if (!line.startsWith("{")) {
                Workflow workflow = new Workflow(line);
                workflows.put(workflow.name, workflow);
            }
        });

        AtomicLong sum = new AtomicLong(0);

        PartRange range = new PartRange();

        Queue<Task> tasks = new LinkedList<>();
        tasks.add(new Task(range, "in"));
        while (!tasks.isEmpty()) {
            Task task = tasks.poll();
            List<Task> targets = workflows.get(task.target).calcTargets(task.partRange);
            targets.stream().filter(es -> {
                if (es.target.equals("R")) return false;
                if (es.target.equals("A")) {
                    sum.addAndGet(es.partRange.rangeCount());
                    return false;
                }
                return true;
            }).forEach(tasks::add);
        }

        return sum.get();
    }

    static class Condition {

        String field, target;
        long value;
        boolean gt;

        public Condition(String conditionStr) {
            Pattern pattern = Pattern.compile("([xmas])([<>])(\\d+):(\\w+)");
            Matcher matcher = pattern.matcher(conditionStr);
            matcher.find();
            field = matcher.group(1);
            gt = matcher.group(2).equals(">");
            value = Long.parseLong(matcher.group(3));
            target = matcher.group(4);
        }

        public String getTarget(Part part) {
            if (gt) {
                if (part.categories[getFieldOffset()] > value) return target;
            } else {
                if (part.categories[getFieldOffset()] < value) return target;
            }
            return null;
        }

        public Pair<PartRange, PartRange> split(PartRange partRange) {
            if (!gt) {
                Pair<PartRange, PartRange> matched_unmatched = partRange.splitBy(getFieldOffset(), value);
                return matched_unmatched;
            } else {
                Pair<PartRange, PartRange> unmatched_matched = partRange.splitBy(getFieldOffset(), value + 1);
                return new Pair<>(unmatched_matched.getRight(), unmatched_matched.getLeft());
            }
        }

        private int getFieldOffset() {
            return switch (field) {
                case "x" -> 0;
                case "m" -> 1;
                case "a" -> 2;
                default -> 3;
            };
        }
    }

    static class Workflow {

        String name, end;
        List<Condition> conditions = new ArrayList<>();

        public Workflow(String line) {
            Pattern pattern = Pattern.compile("([a-z]+)\\{([a-z0-9<>:,AR]+),([a-zAR]+)}");
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            name = matcher.group(1);
            end = matcher.group(3);

            String[] conditionsStr = matcher.group(2).split(",");
            for (String conditionStr : conditionsStr) {
                conditions.add(new Condition(conditionStr));
            }
        }

        public String calcTarget(Part part) {
            for (Condition condition : conditions) {
                String target = condition.getTarget(part);
                if (target != null) return target;
            }
            return end;
        }

        public List<Task> calcTargets(PartRange partRange) {
            List<Task> tasks = new ArrayList<>();

            for (Condition condition : conditions) {
                Pair<PartRange, PartRange> matched_unmatched = condition.split(partRange);
                if (matched_unmatched.getLeft() != null) {
                    tasks.add(new Task(matched_unmatched.getLeft(), condition.target));
                }
                if (matched_unmatched.getRight() == null) return tasks;

                partRange = matched_unmatched.getRight();
            }
            tasks.add(new Task(partRange, end));
            return tasks;
        }
    }

    static class Part {

        long[] categories = new long[4];

        public Part(String line) {
            Pattern pattern = Pattern.compile("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}");
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            categories[0] = Long.parseLong(matcher.group(1));
            categories[1] = Long.parseLong(matcher.group(2));
            categories[2] = Long.parseLong(matcher.group(3));
            categories[3] = Long.parseLong(matcher.group(4));
        }
    }

    static class PartRange {

        long[] categoryMins = {1, 1, 1, 1};
        long[] categoryMaxs = {4000, 4000, 4000, 4000};

        public PartRange(long[] categoryMins, long[] categoryMaxs) {
            this.categoryMins = Arrays.copyOf(categoryMins, categoryMins.length);
            this.categoryMaxs = Arrays.copyOf(categoryMaxs, categoryMaxs.length);
        }

        public PartRange() {
        }

        public Pair<PartRange, PartRange> splitBy(int field, long rightInclusive) {
            long minAccepted = categoryMins[field];
            long maxAccepted = categoryMaxs[field];

            if (rightInclusive > maxAccepted) {
                return new Pair<>(this, null);
            }
            if (rightInclusive <= minAccepted) {
                return new Pair<>(null, this);
            }
            PartRange left = copy();
            PartRange right = copy();
            left.categoryMaxs[field] = rightInclusive - 1;
            right.categoryMins[field] = rightInclusive;
            return new Pair<>(left, right);
        }

        private PartRange copy() {
            return new PartRange(categoryMins, categoryMaxs);
        }

        public long rangeCount() {
            return (categoryMaxs[0] - categoryMins[0] + 1) *
                    (categoryMaxs[1] - categoryMins[1] + 1) *
                    (categoryMaxs[2] - categoryMins[2] + 1) *
                    (categoryMaxs[3] - categoryMins[3] + 1);
        }
    }

    record Task(PartRange partRange, String target) {
    }
}
