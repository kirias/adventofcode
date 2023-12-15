package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Day15 extends Problem {

    public Day15(String path) {
        super(path, 15);
    }

    @Override
    public long getPart1Solution() {
        String line = inputLines().findFirst().get();
        String[] tokens = line.split(",");
        return Arrays.stream(tokens)
                .mapToInt(Day15::toHash)
                .mapToLong(i -> i)
                .sum();
    }

    private static int toHash(String s) {
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash += s.charAt(i);
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

    static class Token {
        public boolean add = false;
        public String label;
        public int focalLen = 0;

        public Token(String str) {
            this.add = str.contains("=");
            this.label = str.split(add ? "=" : "-")[0];
            this.focalLen = add ? Integer.parseInt(str.split("=")[1]) : 0;
        }

        @Override
        public int hashCode() {
            return toHash(label);
        }

        @Override
        public boolean equals(Object obj) {
            Token other = (Token) obj;
            return label.equals(other.label);
        }
    }

    @Override
    public long getPart2Solution() {
        CustomHashMap hm = new CustomHashMap();
        String line = inputLines().findFirst().get();
        String[] tokens = line.split(",");
        Arrays.stream(tokens)
                .map(Token::new)
                .forEach(hm::process);

        return hm.calcPower();  // 243472 wrong
    }

    static class CustomHashMap {
        List<LinkedList<Token>> buckets = new ArrayList<>(256);

        public CustomHashMap() {
            IntStream.range(0, 256).forEach(i -> {
                buckets.add(new LinkedList<>());
            });
        }

        public void process(Token token) {
            if (token.add) {
                add(token);
            } else {
                remove(token);
            }
        }

        public void add(Token token) {
            LinkedList<Token> tokens = buckets.get(token.hashCode());

            int index = tokens.indexOf(token);
            if (index == -1) {
                tokens.addLast(token);
            } else {
                tokens.get(index).focalLen = token.focalLen;
            }
        }

        public void remove(Token token) {
            int bucket = token.hashCode();
            LinkedList<Token> tokens = buckets.get(bucket);
            tokens.remove(token);
        }

        public long calcPower() {
            long power = 0;
            for (int i = 0; i < 256; i++) {
                LinkedList<Token> get = buckets.get(i);
                for (int j = 0; j < get.size(); j++) {
                    Token token = get.get(j);
                    power += (i + 1L) * (j + 1L) * token.focalLen;
                }
            }
            return power;
        }
    }
}
