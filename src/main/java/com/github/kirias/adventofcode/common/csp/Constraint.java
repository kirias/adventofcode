package com.github.kirias.adventofcode.common.csp;

import java.util.List;
import java.util.Map;

public abstract class Constraint<V, D> {
    List<V> variables;

    public Constraint(List<V> variables) {
        this.variables = variables;
    }

    public List<V> getVariables() {
        return variables;
    }

    public abstract boolean satisfied(Map<V, D> assignment);
}
