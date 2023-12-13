package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day13 extends Problem {

    public Day13(String path) {
        super(path, 13);
    }

    @Override
    public long getPart1Solution() {
        return getSolution(0);
    }

    @Override
    public long getPart2Solution() {
        return getSolution(1);
    }

    private static class Map {
        List<String> rowsStr;

        List<Long> rows;
        List<Long> cols;

        public Map(List<String> rowsStr) {
            this.rowsStr = rowsStr;

            this.rows = rowsStr.stream().map(this::mapRowToLong).toList();
            this.cols = IntStream.range(0, rowsStr.get(0).length())
                    .mapToObj(this::mapColToLong)
                    .toList();
        }

        public List<Long> getRows() {
            return rows;
        }

        public List<Long> getCols() {
            return cols;
        }

        /*
                Maps string column to bits
                Input: ###...# (vertically)
                Output: 0b1000111
                 */
        private Long mapColToLong(long col) {
            long transformed = 0;
            long currentPos = 1;
            for (String s : rowsStr) {
                char c = s.charAt((int) col);
                if (c == '#') {
                    transformed += currentPos;
                }
                currentPos = currentPos << 1;
            }
            return transformed;
        }

        /*
        Maps string row to bits
        Input: ###...#
        Output: 0b1000111
         */
        private Long mapRowToLong(String line) {
            char[] charArray = line.toCharArray();
            long transformed = 0;
            long currentPos = 1;
            for (char c : charArray) {
                if (c == '#') {
                    transformed += currentPos;
                }
                currentPos = currentPos << 1;
            }
            return transformed;
        }
    }

    private long getSolution(int bitDifferenceRequired) {
        List<Map> maps = new ArrayList<>();

        List<String> map = new ArrayList<>();

        for (String line : inputLines().toList()) {
            if (!line.isEmpty()) {
                map.add(line);
            } else {
                maps.add(new Map(map));
                map = new ArrayList<>();
            }
        }
        if (!map.isEmpty()) maps.add(new Map(map));

        long sum = 0;

        for (Map value : maps) {
            List<Long> mapCols = value.getCols();
            List<Long> mapRows = value.getRows();

            int colReflection = getReflection(mapCols, bitDifferenceRequired);
            int rowReflection = getReflection(mapRows, bitDifferenceRequired);

            sum += colReflection;
            sum += rowReflection * 100L;

        }

        return sum;
    }

    private int getReflection(List<Long> series, int bitDifferenceRequired) {
        int reflectionStart;
        for (int pos = 0; pos < series.size() - 1; pos++) {
            int countDifferentBits = 0;
            reflectionStart = pos;
            for (int reflectionPos = 0; reflectionPos < series.size() / 2; reflectionPos++) {
                if (pos - reflectionPos >= 0 && pos + 1 + reflectionPos < series.size()) {
                    Long value1 = series.get(pos - reflectionPos);
                    Long value2 = series.get(pos + 1 + reflectionPos);
                    countDifferentBits += countOfDifferentBits(value1, value2);
                    if (countDifferentBits > bitDifferenceRequired) {
                        break;
                    }
                }
            }
            if (countDifferentBits == bitDifferenceRequired) return reflectionStart + 1;
        }
        return 0;
    }

    private int countOfDifferentBits(Long value1, Long value2) {
        long xor = value1 ^ value2;
        int bitDifference = 0;
        while (xor > 0) {
            if (xor != ((xor >> 1) << 1)) bitDifference++;
            xor = xor >> 1;
        }
        return bitDifference;
    }

}
