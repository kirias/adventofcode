package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day04 extends Problem {

    public Day04(String path) {
        super(path, 4);
    }

    static class Card {

        private final int cardId;
        private final Set<String> cardNums;
        private final Set<String> playNums;

        public Card(int cardId, String cardLine) {
            this.cardId = cardId;
            String nums = cardLine.split(":")[1].trim();
            String[] round = nums.split("\\|");

            cardNums = Arrays.stream(round[0].trim()
                            .split(" "))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toUnmodifiableSet());
            playNums = Arrays.stream(round[1].trim()
                            .split(" "))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toUnmodifiableSet());
        }

        public int getCardId() {
            return cardId;
        }

        public Set<String> getCardNums() {
            return cardNums;
        }

        public Set<String> getPlayNums() {
            return playNums;
        }
    }

    @Override
    public long getPart1Solution() {
        AtomicInteger cardId = new AtomicInteger(0);
        return inputLines()
                .map(line -> new Card(cardId.getAndIncrement(), line))
                .map(card -> {
                    Set<String> winningNums = new HashSet<>(card.getCardNums());
                    winningNums.retainAll(card.getPlayNums());

                    if (winningNums.isEmpty()) {
                        return 0d;
                    }

                    return Math.pow(2, winningNums.size() - 1);
                })
                .mapToLong(Double::longValue)
                .sum();
    }

    @Override
    public long getPart2Solution() {
        AtomicInteger cardId = new AtomicInteger(0);
        List<Card> cards = inputLines()
                .map(line -> new Card(cardId.getAndIncrement(), line))
                .toList();
        List<Integer> counts = IntStream.generate(() -> 1)
                .limit(cards.size())
                .boxed()
                .collect(Collectors.toList());

        for (Card card : cards) {
            Set<String> winningNums = new HashSet<>(card.getCardNums());
            winningNums.retainAll(card.getPlayNums());

            int multiplier = counts.get(card.getCardId());
            for (int i = 0; i < winningNums.size(); i++) {
                int idx = card.getCardId() + i + 1;
                counts.set(idx, counts.get(idx) + multiplier);
            }
        }
        return counts.stream().mapToLong(i -> i).sum();
    }
}
