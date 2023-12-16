package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Direction;
import com.github.kirias.adventofcode.common.Matrix;

import java.util.*;

import static com.github.kirias.adventofcode.common.Direction.*;

public class Day10 extends Problem {

    static class Pipe {
        Set<Direction> connections = EnumSet.noneOf(Direction.class);
        boolean markedLeft;
        boolean markedRight;

        int row;
        int col;
        boolean pipe = false;

        public Pipe(Character segment, int row, int col) {
            this.row = row;
            this.col = col;
            switch (segment) {
                case '|' -> connections.addAll(List.of(UP, DOWN));
                case '-' -> connections.addAll(List.of(LEFT, RIGHT));
                case 'L' -> connections.addAll(List.of(UP, RIGHT));
                case 'J' -> connections.addAll(List.of(UP, LEFT));
                case '7' -> connections.addAll(List.of(LEFT, DOWN));
                case 'F' -> connections.addAll(List.of(RIGHT, DOWN));
                default -> {
                }
            }
        }

        @Override
        public String toString() {
            if (pipe) {
                if (connections.containsAll(List.of(UP, DOWN))) return "│";
                if (connections.containsAll(List.of(LEFT, RIGHT))) return "─";
                if (connections.containsAll(List.of(UP, RIGHT))) return "└";
                if (connections.containsAll(List.of(UP, LEFT))) return "┘";
                if (connections.containsAll(List.of(LEFT, DOWN))) return "┐";
                if (connections.containsAll(List.of(RIGHT, DOWN))) return "┌";
            } else {
                if (markedLeft) return "░";
                if (markedRight) return "▓";
            }
            return "▧";
        }

        public boolean connectedTo(Pipe pipe) {
            return this.connections
                    .stream()
                    .anyMatch(c -> this.row + c.rowOffset() == pipe.row && this.col + c.colOffset() == pipe.col);
        }

        public Set<Direction> getConnections() {
            return connections;
        }

        // 1 if clockwise, -1 if counterclockwise
        public int getTurn(Pipe prevPipe) {
            Direction connectionIn = this.connections
                    .stream()
                    .filter(c -> this.row + c.rowOffset() == prevPipe.row && this.col + c.colOffset() == prevPipe.col)
                    .findFirst().get();
            Direction vectorIn = connectionIn.opposite();
            if (this.connections.containsAll(Set.of(vectorIn, vectorIn.turnLeft()))) return -1;
            if (this.connections.containsAll(Set.of(vectorIn, vectorIn.turnRight()))) return 1;
            return 0;
        }

        public Set<Direction> getLeftVectors(Pipe prevPipe) {
            Direction connectionIn = this.connections
                    .stream()
                    .filter(c -> this.row + c.rowOffset() == prevPipe.row && this.col + c.colOffset() == prevPipe.col)
                    .findFirst().get();
            // this.connections are vectors from the center of the segment to its bounds
            // we found the one that acted as input,
            // we reverse this vector to point from the bound to the center
            // so if we want to check if the pipe turns left, we can use vectorIn.turnLeft() for vectorOut,
            // if we want to check if the pipe turns right, we can use vectorIn.turnRight() for vectorOut
            // direct pipe will contain vectorIn.opposite() -> connectionIn and vectorIn
            Direction vectorIn = connectionIn.opposite();

            if (this.connections.containsAll(Set.of(connectionIn, vectorIn.turnLeft()))) return Set.of();
            if (this.connections.containsAll(Set.of(connectionIn, vectorIn))) return Set.of(vectorIn.turnLeft());
            if (this.connections.containsAll(Set.of(connectionIn, vectorIn.turnRight()))) return Set.of(vectorIn.turnLeft(), vectorIn);
            return null;
        }

        public Set<Direction> getRightVectors(Pipe prevPipe) {
            Direction connectionIn = this.connections
                    .stream()
                    .filter(c -> this.row + c.rowOffset() == prevPipe.row && this.col + c.colOffset() == prevPipe.col)
                    .findFirst().get();
            Direction vectorIn = connectionIn.opposite();

            if (this.connections.containsAll(Set.of(connectionIn, vectorIn.turnRight()))) return Set.of();
            if (this.connections.containsAll(Set.of(connectionIn, vectorIn))) return Set.of(vectorIn.turnRight());
            if (this.connections.containsAll(Set.of(connectionIn, vectorIn.turnLeft()))) return Set.of(vectorIn.turnRight(), vectorIn);
            return null;
        }

        public void mark(boolean left) {
            if (left) {
                markedLeft = true;
            } else {
                markedRight = true;
            }
        }

        public void setPipe() {
            pipe = true;
        }
    }

    public Day10(String path) {
        super(path, 10);
    }

    @Override
    public long getPart1Solution() {
        Pipe start = null;
        Matrix<Pipe> matrix = new Matrix<>();
        int row = 0;
        for (String line : inputLines().toList()) {
            List<Pipe> gridLine = new ArrayList<>();
            int col = 0;
            for (char c : line.toCharArray()) {
                Pipe pipe = new Pipe(c, row, col);
                if (c == 'S') start = pipe;
                gridLine.add(pipe);
                col++;
            }
            matrix.addRow(gridLine);
            row++;
        }
        List<Pipe> connectedToStart = findConnectedNeighbours(matrix, start);
        if (connectedToStart.size() != 2) {
            throw new RuntimeException("More than two pipes connected to start");
        }

        Pipe left = connectedToStart.get(0);
        Pipe leftPrev = start;
        Pipe right = connectedToStart.get(1);
        Pipe rightPrev = start;

        long steps = 1;
        while (true) {
            Pipe leftNext = getNextConnectedPipe(matrix, left, leftPrev);
            if (leftNext == right) {
                break;
            }
            Pipe rightNext = getNextConnectedPipe(matrix, right, rightPrev);
            if (rightNext == leftNext) {
                steps++;
                break;
            }
            steps++;
            leftPrev = left;
            rightPrev = right;
            left = leftNext;
            right = rightNext;
        }

        return steps;
    }

    @Override
    public long getPart2Solution() {
        Pipe start = null;
        Matrix<Pipe> matrix = new Matrix<>();
        int row = 0;
        for (String line : inputLines().toList()) {
            List<Pipe> gridLine = new ArrayList<>();
            int col = 0;
            for (char c : line.toCharArray()) {
                Pipe pipe = new Pipe(c, row, col);
                if (c == 'S') start = pipe;
                gridLine.add(pipe);
                col++;
            }
            matrix.addRow(gridLine);
            row++;
        }

        List<Pipe> connectedToStart = findConnectedNeighbours(matrix, start);
        if (connectedToStart.size() != 2) {
            throw new RuntimeException("More than two pipes connected to start");
        }
        int startRow = start.row;
        int startCol = start.col;
        start.getConnections().addAll(connectedToStart.stream()
                .map(connection -> {
                    if (connection.row == startRow - 1) return UP;
                    if (connection.row == startRow + 1) return DOWN;
                    if (connection.col == startCol - 1) return LEFT;
                    if (connection.col == startCol + 1) return RIGHT;
                    return null;
                }).toList());

        Pipe current = connectedToStart.get(0);
        while (current != null) {
            current.setPipe();
            current = getConnected(matrix, current)
                    .stream()
                    .filter(pipe -> !pipe.pipe)
                    .findFirst()
                    .orElse(null);
        }


        Pipe prev = start;
        current = connectedToStart.get(0);
        Pipe next;

        int turns = 0;

        while (current != start) {
            final Pipe currentFinal = current;
            current.getLeftVectors(prev).forEach(
                    v -> markAreas(matrix, currentFinal, v, true));
            current.getRightVectors(prev).forEach(
                    v -> markAreas(matrix, currentFinal, v, false));
            next = getNextConnectedPipe(matrix, current, prev);
            turns += next.getTurn(current);
            prev = current;
            current = next;
        }

        final Pipe currentFinal = current;
        current.getLeftVectors(prev).forEach(
                v -> markAreas(matrix, currentFinal, v, true));
        current.getRightVectors(prev).forEach(
                v -> markAreas(matrix, currentFinal, v, false));

        final boolean countRight = turns > 0;

        // matrix.print();

        return matrix.stream()
                .mapToInt(p -> {
                    if (countRight && p.markedRight) return 1;
                    if (!countRight && p.markedLeft) return 1;
                    return 0;
                })
                .sum();
    }

    void markAreas(Matrix<Pipe> pipes, Pipe pipe, Direction vector, boolean left) {
        int nextX = pipe.row + vector.rowOffset();
        int nextY = pipe.col + vector.colOffset();
        while (pipes.inMatrix(nextX, nextY) && !pipes.get(nextX, nextY).pipe) {
            pipes.get(nextX, nextY).mark(left);
            nextX = nextX + vector.rowOffset();
            nextY = nextY + vector.colOffset();
        }
    }

    List<Pipe> findConnectedNeighbours(Matrix<Pipe> matrix, Pipe pipe) {
        return Direction.all()
                .stream()
                .map(d -> matrix.getIfExist(pipe.row + d.rowOffset(), pipe.col + d.colOffset()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.connectedTo(pipe))
                .toList();
    }

    List<Pipe> getConnected(Matrix<Pipe> matrix, Pipe pipe) {
        return pipe.getConnections()
                .stream()
                .map(dir -> matrix.getIfExist(pipe.row + dir.rowOffset(), pipe.col + dir.colOffset()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    Pipe getNextConnectedPipe(Matrix<Pipe> matrix, Pipe pipe, Pipe skipPipe) {
        return getConnected(matrix, pipe)
                .stream()
                .filter(p -> p != skipPipe)
                .findFirst()
                .get();
    }

}
