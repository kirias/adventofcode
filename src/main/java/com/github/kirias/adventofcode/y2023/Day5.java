package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Day5 extends Problem {

    record Mapper(long dest, long source, long len) {

        public boolean willMap(Long seed) {
            return seed >= source && seed < source + len;
        }

        public boolean willMap(SeedRange seedRange) {
            return seedRange.from >= source && seedRange.to < source + len;
        }

        public long map(long seed) {
            return seed - source + dest;
        }

        public SeedRange map(SeedRange seed) {
            if (willMap(seed)) {
                return new SeedRange(seed.from - source + dest, seed.to - source + dest);
            }
            return seed;
        }

        public Long intersectLeft(SeedRange sr) {
            if (source >= sr.from && source <= sr.to) return source;
            return sr.from;
        }

        public Long intersectRight(SeedRange sr) {
            long to = source + len - 1;
            if (to >= sr.from && to <= sr.to) return to;
            return sr.to;
        }
    }

    record SeedRange(long from, long to) {

        List<SeedRange> splitByMapperRange(Mapper mapper) {
            Long mapperLeftBound = mapper.intersectLeft(this);
            Long mapperRightBound = mapper.intersectRight(this);
            if (mapperLeftBound != from) {
                if (mapperRightBound != to) {
                    return List.of(
                            new SeedRange(from, mapperLeftBound - 1),
                            new SeedRange(mapperLeftBound, mapperRightBound),
                            new SeedRange(mapperRightBound + 1, to)
                    );
                } else {
                    return List.of(
                            new SeedRange(from, mapperLeftBound - 1),
                            new SeedRange(mapperLeftBound, to)
                    );
                }
            } else if (mapperRightBound != to) {
                return List.of(
                        new SeedRange(from, mapperRightBound),
                        new SeedRange(mapperRightBound + 1, to)
                );
            } else {
                return List.of(new SeedRange(from, to));
            }
        }
    }

    public Day5(String path) {
        super(path, 5);
    }

    @Override
    public long getPart1Solution() {
        List<String> inputLines = inputLines().toList();
        String seedsLine = inputLines.get(0).split(":")[1].trim();
        List<Long> seeds = Arrays.stream(seedsLine.split(" "))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<List<Mapper>> mapperGroups = getMapperGroups(inputLines);

        for (List<Mapper> mapperGroup : mapperGroups) {
            seeds = seeds.stream()
                    .map(seed -> mapperGroup.stream()
                            .filter(mapper -> mapper.willMap(seed))
                            .findFirst()
                            .map(mapper -> mapper.map(seed))
                            .orElse(seed))
                    .toList();
        }

        return seeds.stream()
                .mapToLong(s -> s)
                .min().getAsLong();
    }

    @Override
    public long getPart2Solution() {
        List<String> inputLines = inputLines().toList();
        String seedsLine = inputLines.get(0).split(":")[1].trim();
        List<Long> seeds = Arrays.stream(seedsLine.split(" "))
                .map(Long::valueOf)
                .toList();
        List<SeedRange> seedRanges = new ArrayList<>();
        for (int i = 0; i < seeds.size(); i += 2) {
            seedRanges.add(new SeedRange(seeds.get(i), seeds.get(i) + seeds.get(i + 1) - 1));
        }

        List<List<Mapper>> mapperGroups = getMapperGroups(inputLines);

        List<SeedRange> toMap = seedRanges;
        List<SeedRange> mapped = new ArrayList<>();
        List<SeedRange> skipped = new ArrayList<>();

        for (List<Mapper> mapperList : mapperGroups) {
            toMap.addAll(mapped);
            mapped.clear();
            for (Mapper mapper : mapperList) {
                skipped.clear();
                for (SeedRange seed : toMap) {
                    seed.splitByMapperRange(mapper)
                            .forEach(seedRange -> {
                                if (mapper.willMap(seedRange)) {
                                    mapped.add(mapper.map(seedRange));
                                } else {
                                    skipped.add(seedRange);
                                }
                            });
                }
                toMap = new ArrayList<>(skipped);
            }
        }

        toMap.addAll(mapped);

        return toMap.stream()
                .map(SeedRange::from)
                .min(Comparator.naturalOrder())
                .get();
    }

    private List<List<Mapper>> getMapperGroups(List<String> inputLines) {
        List<List<Mapper>> mapperGroups = new ArrayList<>();
        List<Mapper> mappers = null;
        for (int i = 2; i < inputLines.size(); i++) {
            String line = inputLines.get(i);
            if (line.contains("map")) {
                if (mappers != null && !mappers.isEmpty()) {
                    mapperGroups.add(mappers);
                }
                mappers = new ArrayList<>();
            } else if (!line.isBlank()) {
                String[] params = line.split(" ");
                mappers.add(new Mapper(Long.parseLong(params[0]), Long.parseLong(params[1]), Long.parseLong(params[2])));
            }
        }
        if (mappers != null && !mappers.isEmpty()) {
            mapperGroups.add(mappers);
        }
        return mapperGroups;
    }
}
