package com.github.kirias.adventofcode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public abstract class Problem {

    private final String path;
    private final int day;

    protected Problem(String path, int day) {
        this.path = path;
        this.day = day;
    }

    public abstract long getPart1Solution();

    public abstract long getPart2Solution();

    public int getDay() {
        return day;
    }

    protected Stream<String> inputLines() {
        InputStream inputStream = this.getClass().getResourceAsStream("/inputs" + path);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        return reader.lines();
    }

}
