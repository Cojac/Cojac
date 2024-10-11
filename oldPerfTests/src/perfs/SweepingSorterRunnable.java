/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.cojac.perfs;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

public final class SweepingSorterRunnable implements Runnable {
    private final Point[] points = rndPointSet(new Random(), 50000, 100000);

    private static final Comparator<Point2D> VERTICAL_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(Point2D a, Point2D b) {
            if (a.getY() < b.getY()) {
                return -1;
            }

            if (a.getY() > b.getY()) {
                return 1;
            }

            return Double.compare(a.getX(), b.getX());

        }
    };

    private static final Comparator<Point2D> HORIZONTAL_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(Point2D a, Point2D b) {
            if (a.getX() < b.getX()) {
                return -1;
            }

            if (a.getX() > b.getX()) {
                return 1;
            }

            return Double.compare(a.getY(), b.getY());

        }
    };

    @Override
    public void run() {
        Point2D[] closestPair = new Point2D[2];

        //When we start the min distance is the infinity
        double crtMinDist = Double.POSITIVE_INFINITY;

        //Get the points and sort them
        Point[] sorted = Arrays.copyOf(points, points.length);
        Arrays.sort(sorted, HORIZONTAL_COMPARATOR);

        //When we start the left most candidate is the first one
        int leftMostCandidateIndex = 0;

        //Vertically sorted set of candidates
        SortedSet<Point2D> candidates = new TreeSet<>(VERTICAL_COMPARATOR);

        //For each point from left to right
        for (Point current : sorted) {
            //Shrink the candidates
            while (current.x - sorted[leftMostCandidateIndex].x > crtMinDist) {
                candidates.remove(sorted[leftMostCandidateIndex]);
                leftMostCandidateIndex++;
            }

            //Compute the y head and the y tail of the candidates set
            Point2D head = new Point.Double(current.x, current.y - crtMinDist);
            Point2D tail = new Point.Double(current.x, current.y + crtMinDist);

            //We take only the interesting candidates in the y axis
            for (Point2D point : candidates.subSet(head, tail)) {
                double distance = current.distance(point);

                //Simple min computation
                if (distance < crtMinDist) {
                    crtMinDist = distance;

                    closestPair[0] = current;
                    closestPair[1] = point;
                }
            }

            //The current point is now a candidate
            candidates.add(current);
        }

    }

    public static Point[] rndPointSet(Random r, int n, int maxCoord) {
        Point[] t = new Point[n];
        Point p;
        HashSet<Point> h = new HashSet<>();
        Iterator<Point> itr;
        while (h.size() < n) {
            p = new Point(r.nextInt(maxCoord), r.nextInt(maxCoord));
            h.add(p);
        }
        itr = h.iterator();
        for (int i = 0; i < n; i++) {
            p = itr.next();
            t[i] = p;
        }
        return t;
    }
}
