package com.github.kirias.adventofcode.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class RangeMatrix<E> {

    List<RowsLine> rows = new ArrayList<>();
    List<ColsLine> cols = new ArrayList<>();
    List<Range> rowRanges;
    List<Range> colRanges;
    private long addingRow;
    private long addingCol;
    public RangeMatrix() {
    }

    public void start() {
        this.addingRow = 0;
        this.addingCol = 0;
    }

    public void add(E element, Direction cursorDirection, int count) {
        if (cursorDirection == Direction.RIGHT) {
            addingCol += 1;
            cols.add(new ColsLine(addingRow, addingCol, addingCol + count - 1, element));
            addingCol += count - 1;
        } else if (cursorDirection == Direction.LEFT) {
            addingCol--;
            cols.add(new ColsLine(addingRow, addingCol - count + 1, addingCol, element));
            addingCol -= count - 1;
        } else if (cursorDirection == Direction.DOWN) {
            addingRow++;
            rows.add(new RowsLine(addingCol, addingRow, addingRow + count - 1, element));
            addingRow += count - 1;
        } else if (cursorDirection == Direction.UP) {
            addingRow--;
            rows.add(new RowsLine(addingCol, addingRow - count + 1, addingRow, element));
            addingRow -= count - 1;
        }
    }

    public void finish() {
        rowRanges = new ArrayList<>();
        rows.stream().map(r -> new Range(r.from, r.to)).forEach(rowRanges::add);
        cols.stream().map(c -> new Range(c.row, c.row)).forEach(rowRanges::add);
        colRanges = new ArrayList<>();
        cols.stream().map(r -> new Range(r.from, r.to)).forEach(colRanges::add);
        rows.stream().map(c -> new Range(c.col, c.col)).forEach(colRanges::add);

        rowRanges = splitToRanges(rowRanges);
        colRanges = splitToRanges(colRanges);
    }

    private List<Range> splitToRanges(List<Range> rangesList) {
        List<Long> indexList = rangesList.stream()
                .flatMap(r -> Stream.of(r.from, r.to))
                .distinct()
                .sorted()
                .toList();

        List<Range> newRanges = new ArrayList<>();

        for (Long index : indexList) {
            newRanges.add(new Range(index, index));
        }
        for (int i = 1; i < indexList.size(); i++) {
            if (indexList.get(i - 1) + 1 <= indexList.get(i) - 1) {
                newRanges.add(new Range(indexList.get(i - 1) + 1, indexList.get(i) - 1));
            }
        }
        newRanges.sort(Comparator.comparing(Range::from));

        return newRanges;
    }

    public Iterator<RowRange> rowRanges() {
        Iterator<Range> iterator = rowRanges.iterator();
        return new Iterator<RowRange>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public RowRange next() {
                return new RowRange(iterator.next());
            }
        };
    }

    record ColsLine(long row, long from, long to, Object element) {
    }

    record RowsLine(long col, long from, long to, Object element) {
    }

    record Range(long from, long to) {
    }

    public class RowRange {
        private final Range rowRangeRow;

        RowRange(Range rowRangeRow) {
            this.rowRangeRow = rowRangeRow;
        }

        public long numRows() {
            return rowRangeRow.to - rowRangeRow.from + 1;
        }

        public Iterator<ColRange> colRanges() {
            Iterator<Range> iterator = RangeMatrix.this.colRanges.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public ColRange next() {
                    return new ColRange(rowRangeRow, iterator.next());
                }
            };
        }


        public class ColRange {
            private final Range rowRangeCol;
            private final Range colRangeCol;

            ColRange(Range rowRangeCol, Range colRangeCol) {
                this.rowRangeCol = rowRangeCol;
                this.colRangeCol = colRangeCol;
            }

            public long numCols() {
                return colRangeCol.to - colRangeCol.from + 1;
            }

            public E value() {
                ColsLine foundRange1 = cols.stream().filter(r -> r.row >= rowRangeCol.from && r.row <= rowRangeCol.to)
                        .filter(r -> r.from <= colRangeCol.from && r.to >= colRangeCol.to)
                        .findAny()
                        .orElse(null);
                if (foundRange1 != null) return (E) foundRange1.element;

                RowsLine foundRange2 = rows.stream().filter(r -> r.col >= colRangeCol.from && r.col <= colRangeCol.to)
                        .filter(r -> r.from <= rowRangeCol.from && r.to >= rowRangeCol.to)
                        .findAny()
                        .orElse(null);
                if (foundRange2 != null) return (E) foundRange2.element;

                return null;
            }
        }
    }
}
