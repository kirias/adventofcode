package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day07 extends Problem {

    private static final int HAND_SIZE = 5;

    public Day07(String path) {
        super(path, 7);
    }

    @Override
    public long getPart1Solution() {
        return getDaySolution(RegularHand::new);
    }

    @Override
    public long getPart2Solution() {
        return getDaySolution(HandWithJokers::new);
    }

    private long getDaySolution(BiFunction<String, Integer, Hand> handFactory) {
        TreeSet<Hand> hands = inputLines()
                .map(l -> l.split(" "))
                .map(cards_bid -> handFactory.apply(cards_bid[0], Integer.parseInt(cards_bid[1])))
                .collect(Collectors.toCollection(TreeSet::new));

        long sum = 0;

        Iterator<Hand> iterator = hands.iterator();
        for (int i = 0; i < hands.size(); i++) {
            Hand next = iterator.next();
            sum += (i + 1) * next.bid;
        }

        return sum;
    }

    static class RegularHand extends Hand {

        static final String ORDER = "23456789TJQKA";

        public RegularHand(String cards, long bid) {
            super(cards, bid, ORDER);

            Map<Character, Long> cardCounts = cards.chars()
                    .mapToObj(i -> (char) i)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

            rank = calculateRank(cardCounts);
        }
    }

    static class HandWithJokers extends Hand {

        static final String ORDER = "J23456789TQKA";

        public HandWithJokers(String cards, long bid) {
            super(cards, bid, ORDER);

            Map<Character, Long> cardCounts = cards.chars()
                    .mapToObj(i -> (char) i)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

            Long jokersCount = cardCounts.getOrDefault('J', 0L);

            if (jokersCount > 0) {
                cardCounts.remove('J');
                if (cardCounts.isEmpty()) { // Special case with only Jokers
                    cardCounts.put('A', 0L);
                }
                Map.Entry<Character, Long> maxCount = cardCounts.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue()).get();
                maxCount.setValue(maxCount.getValue() + jokersCount);
            }

            rank = calculateRank(cardCounts);
        }
    }

    static abstract class Hand implements Comparable<Hand> {
        String cards;
        long bid;

        int rank;

        String order;

        public Hand(String cards, long bid, String order) {
            this.cards = cards;
            this.bid = bid;
            this.order = order;
        }

        protected int calculateRank(Map<Character, Long> cardCounts) {
            Collection<Long> counts = cardCounts.values();
            if (cardCounts.size() == 1) { // Five of a kind
                return 7;
            } else {
                if (cardCounts.size() == 2) {
                    Long card = counts.iterator().next();
                    if (card == 1 || card == 4) { // Four of a kind
                        return 6;
                    } else { // Full house (Pair + Three of a kind)
                        return 5;
                    }
                } else if (cardCounts.size() == 3) {
                    if (counts.stream().anyMatch(i -> i.equals(3L))) { // Three of a kind
                        return 4;
                    } else { // Two pair
                        return 3;
                    }
                } else if (cardCounts.size() == 4) { // One pair
                    return 2;
                } else { // High card
                    return 1;
                }
            }
        }

        @Override
        public int compareTo(Hand otherHand) {
            if (this.rank != otherHand.rank) {
                return Integer.compare(this.rank, otherHand.rank);
            }

            for (int i = 0; i < HAND_SIZE; i++) {
                char thisCard = this.cards.charAt(i);
                char otherCard = otherHand.cards.charAt(i);
                if (thisCard != otherCard) {
                    return Integer.compare(order.indexOf(thisCard), order.indexOf(otherCard));
                }
            }
            return 0;
        }
    }
}
