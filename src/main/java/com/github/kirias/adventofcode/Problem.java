package com.github.kirias.adventofcode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public abstract class Problem {

    private final String path;

    protected Problem(String path) {
        this.path = path;
    }

    public abstract int getDay();

    public abstract long getPart1Solution();

    public abstract long getPart2Solution();

    protected Stream<String> inputLines() {
        InputStream inputStream = this.getClass().getResourceAsStream("/inputs" + path);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        return reader.lines();
    }

}
