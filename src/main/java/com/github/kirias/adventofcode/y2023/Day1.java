package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day1 extends Problem {

    public Day1(String path) {
        super(path);
    }

    @Override
    public int getDay() {
        return 1;
    }

    @Override
    public long getPart1Solution() {
        long sum = 0;
        for (String line : inputLines().toList()) {
            boolean firstFound = false;
            Character last = null;
            for (Character c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    last = c;
                    if (!firstFound) {
                        firstFound = true;
                        sum += (c - '0') * 10;
                    }
                }
            }
            sum += last - '0';
        }
        return sum;
    }

    @Override
    public long getPart2Solution() {
        long sum = 0;

        Pattern firstNum = Pattern.compile("(one|two|three|four|five|six|seven|eight|nine|[0-9])");
        Pattern lastNum = Pattern.compile(".*(one|two|three|four|five|six|seven|eight|nine|[0-9])");

        for (String line : inputLines().toList()) {
            Matcher firstNumMatcher = firstNum.matcher(line);
            firstNumMatcher.find();
            int firstDigit = transformToNumber(firstNumMatcher.group(1)) - '0';

            Matcher lastNumMatcher = lastNum.matcher(line);
            lastNumMatcher.find();
            int lastDigit = transformToNumber(lastNumMatcher.group(1)) - '0';
            sum += firstDigit * 10 + lastDigit;
        }

        return sum;
    }

    private Character transformToNumber(String str) {
        return str.replace("one", "1")
                .replace("two", "2")
                .replace("three", "3")
                .replace("four", "4")
                .replace("five", "5")
                .replace("six", "6")
                .replace("seven", "7")
                .replace("eight", "8")
                .replace("nine", "9").charAt(0);
    }
}
