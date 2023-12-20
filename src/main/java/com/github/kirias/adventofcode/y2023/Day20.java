package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.MathUtil;

import java.util.*;

public class Day20 extends Problem {

    static long cycles = 0;

    public Day20(String path) {
        super(path, 20);
    }

    record Signal(String from, String to, Integer pulse) {
    }

    @Override
    public long getPart1Solution() {
        Map<String, Module> moduleMap = buildModules();
        Queue<Signal> signals = new LinkedList<>();

        long high = 0;
        long low = 0;
        for (int i = 0; i < 1000; i++) {
            signals.add(new Signal("button", "roadcaster", 0));
            while (!signals.isEmpty()) {
                Signal signal = signals.poll();
                if (signal.pulse == 1) {
                    high++;
                } else {
                    low++;
                }

                Module module = moduleMap.get(signal.to);
                if (module != null) {
                    List<Signal> nextSignals = module.sendSignal(signal.pulse, signal.from);
                    signals.addAll(nextSignals);
                }
            }
        }

        return high * low;
    }

    @Override
    public long getPart2Solution() {
        Map<String, Module> moduleMap = buildModules();

        List<Module> rxSources = moduleMap.values().stream().filter(m -> m.outs.contains("rx")).toList();
        if (rxSources.size() != 1) throw new RuntimeException("Not supported configuration");
        Module rxSource = rxSources.get(0);
        if (!rxSource.type.equals("&")) throw new RuntimeException("Not supported configuration");
        List<Module> rxSourceSources = moduleMap.values().stream().filter(m -> m.outs.contains(rxSource.name)).toList();

        Queue<Signal> signals = new LinkedList<>();

        while (rxSourceSources.stream().anyMatch(m -> m.highPulseCycle == null)) {
            cycles++;
            signals.add(new Signal("button", "roadcaster", 0));
            while (!signals.isEmpty()) {
                Signal signal = signals.poll();
                Module module = moduleMap.get(signal.to);
                if (module != null) {
                    List<Signal> nextSignals = module.sendSignal(signal.pulse, signal.from);
                    signals.addAll(nextSignals);
                } else if (signal.to.equals("rx") && signal.pulse == 0) { // who knows
                    return cycles;
                }
            }
        }

        return MathUtil.lcm(rxSourceSources.stream().map(m -> m.highPulseCycle).toList());
    }

    private Map<String, Module> buildModules() {
        Map<String, Module> moduleMap = new HashMap<>();
        inputLines().forEach(line -> {
            String[] name_out = line.split(" -> ");
            String type = name_out[0].substring(0, 1);
            String name = name_out[0].substring(1);
            String[] outs = name_out[1].split(", ");
            Module m = new Module(type, name, 0, new HashMap<>(), Arrays.asList(outs));
            moduleMap.put(m.name, m);
        });
        for (Module m : moduleMap.values()) {
            for (String out : m.outs) {
                if (moduleMap.containsKey(out)) {
                    moduleMap.get(out).conjunctionState.put(m.name, 0);
                }
            }
        }
        return moduleMap;
    }

    static final class Module {
        private final String type;
        private final String name;
        private int flipFlopState;
        private final Map<String, Integer> conjunctionState;
        private final List<String> outs;
        private Long highPulseCycle = null;

        Module(String type, String name, int flipFlopState, Map<String, Integer> conjunctionState, List<String> outs) {
            this.type = type;
            this.name = name;
            this.flipFlopState = flipFlopState;
            this.conjunctionState = conjunctionState;
            this.outs = outs;
        }

        public List<Signal> sendSignal(Integer pulse, String from) {
            if (type.equals("b")) { // broadcaster
                return outs.stream().map(s -> new Signal(name, s, pulse)).toList();
            } else if (type.equals("%")) {
                if (pulse == 1) {
                    return List.of();
                } else {
                    flipFlopState = (flipFlopState + 1) % 2;
                    return outs.stream().map(s -> new Signal(name, s, flipFlopState)).toList();
                }
            } else if (type.equals("&")) {
                conjunctionState.put(from, pulse);
                long countZeroes = conjunctionState.values().stream().filter(i -> i == 0).count();
                int outPulse = countZeroes == 0 ? 0 : 1;
                if (outPulse == 1) {
                    highPulseCycle = cycles;
                }
                return outs.stream().map(s -> new Signal(name, s, outPulse)).toList();
            }
            return List.of();
        }

    }
}
