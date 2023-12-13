package com.github.kirias.adventofcode.common.csp;

import java.util.*;

public class CSP<V, D> {
    List<V> variables;
    Map<V, List<D>> domains;
    Map<V, List<Constraint<V, D>>> constraints;

    public CSP(List<V> variables, Map<V, List<D>> domains) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = new HashMap<>();

        for (V variable : this.variables) {
            this.constraints.put(variable, new ArrayList<>());
            if (!domains.containsKey(variable)) {
                throw new IllegalStateException("Variable without domain!");
            }
        }
    }

    public void addConstraint(Constraint<V, D> constraint) {
        for (V variable : constraint.getVariables()) {
            if (!variables.contains(variable)) {
                throw new IllegalStateException("Missing variable");
            }
            constraints.get(variable).add(constraint);
        }
    }

    public boolean consistent(V variable, Map<V, D> assignment) {
        for (Constraint<V, D> constraint : constraints.get(variable)) {
            if (!constraint.satisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

    public Map<V, D> backtrackingSearch() {
        return backtrackingSearch(new HashMap<>());
    }

    public Map<V, D> backtrackingSearch(Map<V, D> assignment) {
        if (assignment.size() == variables.size()) {
            return assignment;
        }

        V firstUnassigned = null;
        for (V variable : variables) {
            if (!assignment.containsKey(variable)) {
                firstUnassigned = variable;
                break;
            }
        }

        for (D domain : domains.get(firstUnassigned)) {
            assignment.put(firstUnassigned, domain);
            if (consistent(firstUnassigned, assignment)) {
                Map<V, D> assignmentResult = backtrackingSearch(assignment);
                if (assignmentResult != null) {
                    return assignmentResult;
                }
            }
            assignment.remove(firstUnassigned);
        }
        return null;
    }
}
