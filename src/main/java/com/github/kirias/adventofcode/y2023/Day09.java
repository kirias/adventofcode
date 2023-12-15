package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day09 extends Problem {

    public Day09(String path) {
        super(path, 9);
    }

    @Override
    public long getPart1Solution() {
        return inputLines()
                .map(Sequence::new)
                .mapToLong(Sequence::predictNext)
                .sum();
    }

    @Override
    public long getPart2Solution() {
        return inputLines()
                .map(Sequence::new)
                .mapToLong(Sequence::predictPrev)
                .sum();
    }

    static class Sequence {

        List<Long> originalSequence;

        public Sequence(String line) {
            originalSequence = Arrays.stream(line.split(" "))
                    .map(Long::valueOf)
                    .toList();
        }

        public long predictNext() {
            List<Long> sequence = originalSequence;
            List<Long> sequenceLastElements = new ArrayList<>();

            while (!sequence.stream().allMatch(l -> l == 0)) {
                sequenceLastElements.add(sequence.get(sequence.size() - 1));
                final List<Long> sequenceFinal = sequence;
                sequence = IntStream.range(1, sequence.size())
                        .mapToObj(r -> sequenceFinal.get(r) - sequenceFinal.get(r - 1))
                        .toList();
            }

            return sequenceLastElements.stream().mapToLong(l -> l).sum();
        }

        public long predictPrev() {
            List<Long> sequence = originalSequence;
            List<Long> sequenceFirstElements = new ArrayList<>();

            while (!sequence.stream().allMatch(l -> l == 0)) {
                sequenceFirstElements.add(sequence.get(0));
                final List<Long> sequenceFinal = sequence;
                sequence = IntStream.range(1, sequence.size())
                        .mapToLong(r -> sequenceFinal.get(r) - sequenceFinal.get(r - 1))
                        .boxed()
                        .toList();
            }
            long calculated = 0;
            for (int i = sequenceFirstElements.size() - 1; i >= 0; i--) {
                calculated = sequenceFirstElements.get(i) - calculated;
            }
            return calculated;
        }
    }
}
