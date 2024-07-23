package com.github.kirias.adventofcode.y2023;

import com.github.kirias.adventofcode.Problem;
import com.github.kirias.adventofcode.common.GeometryUtil;
import com.github.kirias.adventofcode.common.GeometryUtil.Point;
import com.github.kirias.adventofcode.common.Pair;

import java.math.BigDecimal;
import java.util.*;

public class Day24 extends Problem {
    public Day24(String path) {
        super(path, 24);
    }

    static final long POS_FROM = 200000000000000L;
    static final long POS_TO = 400000000000000L;

    @Override
    public long getPart1Solution() {
        List<Pair<Point, Point>> list = inputLines().map(Traject::new)
                .map(Traject::testAreaPoints)
                .filter(Objects::nonNull)
                .toList();

        int countIntersect = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (GeometryUtil.doIntersect(list.get(i).getLeft(), list.get(i).getRight(),
                        list.get(j).getLeft(), list.get(j).getRight()
                )) {
                    countIntersect++;
                }
            }
        }

        return countIntersect;
    }

    @Override
    public long getPart2Solution() {
        List<Traject> list = inputLines().map(Traject::new).toList();
        Traject first = list.get(0);

        List<Traject> relativeTraj = list.stream()
                .map(t -> t.setVx(t.vx - first.vx)
                        .setVy(t.vy - first.vy)
                        .setVz(t.vz - first.vz)).toList();

        Traject p1 = list.get(0);
        Traject p2 = list.get(1);
        Traject p3 = list.get(2);

        BigDecimal t1 = BigDecimal.valueOf(p1.x - p2.x).divide(BigDecimal.valueOf(p2.vx - p1.vx)); // t1 - time when p1 and p2 met
        BigDecimal t2 = BigDecimal.valueOf(p1.x - p3.x).divide(BigDecimal.valueOf(p3.vx - p1.vx)); // t2 - time when p1 and p3 met


        int time = -1;

        List<Traject> found = new ArrayList<>();
        while (true) {
            time ++;
//            System.out.println(time);
            for (int i = 1; i < relativeTraj.size(); i++) {
                long x = relativeTraj.get(i).xAtTime(time);
                long y = relativeTraj.get(i).yAtTime(time);
                long z = relativeTraj.get(i).zAtTime(time);

                if (x == first.x && y == first.y && z == first.z) {
                    System.out.println("found one!");
                    found.add(relativeTraj.get(i));
                    if (found.size() > 1) break;
                }
            }
            if (found.size() > 1) break;
        }



        return 0;
    }

    public static class Traject {
        long x, y, z;
        long vx, vy, vz;

        public Traject(String desc) {
            Scanner scanner = new Scanner(desc).useDelimiter("[, @]+");
            x = scanner.nextLong();
            y = scanner.nextLong();
            z = scanner.nextLong();
            vx = scanner.nextLong();
            vy = scanner.nextLong();
            vz = scanner.nextLong();
        }

        public Traject(long x, long y, long z, long vx, long vy, long vz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
        }

        public Traject setVx(long vxNew) {
            return new Traject(x, y, z, vxNew, vy, vz);
        }

        public Traject setVy(long vyNew) {
            return new Traject(x, y, z, vx, vyNew, vz);
        }

        public Traject setVz(long vzNew) {
            return new Traject(x, y, z, vx, vy, vzNew);
        }

        public long xAtTime(long time) {
            return x + vx * time;
        }

        public long yAtTime(long time) {
            return y + vy * time;
        }

        public long zAtTime(long time) {
            return z + vz * time;
        }

        public Pair<Point, Point> testAreaPoints() {
            Set<Point> points = new HashSet<>();
            points.add(new Point(x, y));

            yAt(POS_FROM).ifPresent(points::add);
            yAt(POS_TO).ifPresent(points::add);
            xAt(POS_FROM).ifPresent(points::add);
            xAt(POS_TO).ifPresent(points::add);

            List<Point> list = points.stream().filter(p -> p.x() >= POS_FROM && p.x() <= POS_TO && p.y() >= POS_FROM && p.y() <= POS_TO).toList();
            if (list.size() == 2) {
                return new Pair<>(list.get(0), list.get(1));
            } else {
                return null;
            }
        }

        private Optional<Point> yAt(double xAt) {
            double time = (xAt - x) / vx;
            if (time < 0) {
                return Optional.empty();
            }
            Point p = new Point(xAt, y + vy * time);
            return Optional.of(p);
        }

        private Optional<Point> xAt(double yAt) {
            double time = (yAt - y) / vy;
            if (time < 0) {
                return Optional.empty();
            }
            Point p = new Point(x + vx * time, yAt);
            return Optional.of(p);
        }


    }
}
