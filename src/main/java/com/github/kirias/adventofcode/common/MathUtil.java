package com.github.kirias.adventofcode.common;

import java.math.BigInteger;

import static java.math.BigInteger.valueOf;

public class MathUtil {

    public static long gcd(long number1, long number2) {
        return valueOf(number1).gcd(valueOf(number2)).longValue();
    }

    public static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0)
            return 0;
        else {
            BigInteger bigNumber1 = valueOf(number1);
            BigInteger bigNumber2 = valueOf(number2);
            BigInteger gcd = bigNumber1.gcd(bigNumber2);

            return bigNumber1.multiply(bigNumber2).abs().divide(gcd).longValue();
        }
    }

    public static long lcm(long number1, long number2, long... numbers) {
        long lcm = lcm(number1, number2);
        for (long number : numbers) {
            lcm = lcm(lcm, number);
        }
        return lcm;
    }
}
