package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.Matrix;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class Day16 extends Problem {

    enum GridType {
        EMPTY, MIRROR, SPLITTER
    }

    static class GridEl {
        private final int row;
        private final int col;
        private final GridType type;
        private final char el;

        boolean beamLeft, beamRight, beamUp, beamDown;

        public GridEl(char s, int row, int col) {
            this.row = row;
            this.col = col;
            this.el = s;
            type = switch (s) {
                case '.' -> GridType.EMPTY;
                case '/' -> GridType.MIRROR;
                case '\\' -> GridType.MIRROR;
                case '|' -> GridType.SPLITTER;
                case '-' -> GridType.SPLITTER;
                default -> throw new IllegalStateException("Unexpected value: " + s);
            };
        }

        public void lightLeftDir(Matrix<GridEl> matrix, Consumer<GridEl> ifLighted) {
            if (type == GridType.MIRROR) {
                if (el == '/') {
                    matrix.apply(row + 1, col, el -> el.lightDown(ifLighted));
                } else {
                    matrix.apply(row - 1, col, el -> el.lightUp(ifLighted));
                }
            } else if (type == GridType.EMPTY) {
                matrix.apply(row, col - 1, el -> el.lightLeft(ifLighted));
            } else if (type == GridType.SPLITTER) {
                if (el == '|') {
                    matrix.apply(row - 1, col, el -> el.lightUp(ifLighted));
                    matrix.apply(row + 1, col, el -> el.lightDown(ifLighted));
                } else {
                    matrix.apply(row, col - 1, el -> el.lightLeft(ifLighted));
                }
            }
        }

        public void lightRightDir(Matrix<GridEl> matrix, Consumer<GridEl> ifLighted) {
            if (type == GridType.MIRROR) {
                if (el == '/') {
                    matrix.apply(row - 1, col, el -> el.lightUp(ifLighted));
                } else {
                    matrix.apply(row + 1, col, el -> el.lightDown(ifLighted));
                }
            } else if (type == GridType.EMPTY) {
                matrix.apply(row, col + 1, el -> el.lightRight(ifLighted));
            } else if (type == GridType.SPLITTER) {
                if (el == '|') {
                    matrix.apply(row - 1, col, el -> el.lightUp(ifLighted));
                    matrix.apply(row + 1, col, el -> el.lightDown(ifLighted));
                } else {
                    matrix.apply(row, col + 1, el -> el.lightRight(ifLighted));
                }
            }
        }

        public void lightUpDir(Matrix<GridEl> matrix, Consumer<GridEl> ifLighted) {
            if (type == GridType.MIRROR) {
                if (el == '/') {
                    matrix.apply(row, col + 1, el -> el.lightRight(ifLighted));
                } else {
                    matrix.apply(row, col - 1, el -> el.lightLeft(ifLighted));
                }
            } else if (type == GridType.EMPTY) {
                matrix.apply(row - 1, col, el -> el.lightUp(ifLighted));
            } else if (type == GridType.SPLITTER) {
                if (el == '-') {
                    matrix.apply(row, col - 1, el -> el.lightLeft(ifLighted));
                    matrix.apply(row, col + 1, el -> el.lightRight(ifLighted));
                } else {
                    matrix.apply(row - 1, col, el -> el.lightUp(ifLighted));
                }
            }
        }

        public void lightDownDir(Matrix<GridEl> matrix, Consumer<GridEl> ifLighted) {
            if (type == GridType.MIRROR) {
                if (el == '/') {
                    matrix.apply(row, col - 1, el -> el.lightLeft(ifLighted));
                } else {
                    matrix.apply(row, col + 1, el -> el.lightRight(ifLighted));
                }
            } else if (type == GridType.EMPTY) {
                matrix.apply(row + 1, col, el -> el.lightDown(ifLighted));
            } else if (type == GridType.SPLITTER) {
                if (el == '-') {
                    matrix.apply(row, col - 1, el -> el.lightLeft(ifLighted));
                    matrix.apply(row, col + 1, el -> el.lightRight(ifLighted));
                } else {
                    matrix.apply(row + 1, col, el -> el.lightDown(ifLighted));
                }
            }
        }


        public boolean anyBeam() {
            return beamRight || beamLeft || beamUp || beamDown;
        }

        public void setBeamLeft() {
            this.beamLeft = true;
        }

        public void lightLeft(Consumer<GridEl> ifLighted) {
            if (!beamLeft) {
                beamLeft = true;
                ifLighted.accept(this);
            }
        }

        public void lightRight(Consumer<GridEl> ifLighted) {
            if (!beamRight) {
                beamRight = true;
                ifLighted.accept(this);
            }
        }

        public void lightUp(Consumer<GridEl> ifLighted) {
            if (!beamUp) {
                beamUp = true;
                ifLighted.accept(this);
            }
        }

        public void lightDown(Consumer<GridEl> ifLighted) {
            if (!beamDown) {
                beamDown = true;
                ifLighted.accept(this);
            }
        }

        public void setBeamRight() {
            this.beamRight = true;
        }

        public void setBeamUp() {
            this.beamUp = true;
        }

        public void setBeamDown() {
            this.beamDown = true;
        }

        public void resetBeams() {
            beamRight = false;
            beamLeft = false;
            beamUp = false;
            beamDown = false;
        }
    }

    public Day16(String path) {
        super(path, 16);
    }

    @Override
    public long getPart1Solution() {
        Matrix<GridEl> matrix = getInputMatrix();

        matrix.get(0, 0).setBeamRight();
        return countEnergized(matrix, 0, 0);
    }

    @Override
    public long getPart2Solution() {
        Matrix<GridEl> matrix = getInputMatrix();

        long maxEnergized = 0;
        for (int row = 0; row < matrix.height(); row++) {
            matrix.get(row, 0).setBeamRight();
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, row, 0));
            matrix.forEach(GridEl::resetBeams);

            matrix.get(row, matrix.width() - 1).setBeamLeft();
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, row, matrix.width() - 1));
            matrix.forEach(GridEl::resetBeams);
        }
        for (int col = 0; col < matrix.width(); col++) {
            matrix.get(0, col).setBeamDown();
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, 0, col));
            matrix.forEach(GridEl::resetBeams);

            matrix.get(matrix.height() - 1, col).setBeamUp();
            maxEnergized = Math.max(maxEnergized, countEnergized(matrix, matrix.height() - 1, col));
            matrix.forEach(GridEl::resetBeams);
        }

        return maxEnergized;
    }

    private Matrix<GridEl> getInputMatrix() {
        Matrix<GridEl> matrix = new Matrix<>();
        int row = 0;
        for (String line : inputLines().toList()) {
            List<GridEl> gridLine = new ArrayList<>();
            int col = 0;
            for (char c : line.toCharArray()) {
                gridLine.add(new GridEl(c, row, col));
                col++;
            }
            matrix.addRow(gridLine);
            row++;
        }
        return matrix;
    }

    private long countEnergized(Matrix<GridEl> matrix, int startRow, int startCol) {
        Queue<GridEl> elements = new LinkedList<>();

        elements.add(matrix.get(startRow, startCol));

        while (!elements.isEmpty()) {
            GridEl beam = elements.poll();
            if (beam.beamLeft) {
                beam.lightLeftDir(matrix, elements::add);
            }
            if (beam.beamRight) {
                beam.lightRightDir(matrix, elements::add);
            }
            if (beam.beamUp) {
                beam.lightUpDir(matrix, elements::add);
            }
            if (beam.beamDown) {
                beam.lightDownDir(matrix, elements::add);
            }
        }

        return matrix.count(GridEl::anyBeam);
    }
}
