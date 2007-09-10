/**
 * SearchWindow.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2004 Stan Salvador
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 * Based on work by Stan Salvador and Philip Chan.
 * 
 */
package net.sf.javaml.distance.dtw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.Pair;

final class SearchWindow {
    private int minValues[];

    private int maxValues[];

    private int maxJ;

    private int maxI;

    private int minI = 0;

    private int minJ = 0;

    private int size;

    private int modCount;

    SearchWindow(Instance tsI, Instance tsJ, PAA shrunkI, PAA shrunkJ, WarpPath shrunkWarpPath,
            int searchRadius) {

        minValues = new int[tsI.size()];
        maxValues = new int[tsI.size()];
        Arrays.fill(minValues, -1);
        maxJ = tsJ.size() - 1;
        size = 0;
        modCount = 0;
        int currentI = shrunkWarpPath.minI();
        int currentJ = shrunkWarpPath.minJ();
        int lastWarpedI = 0x7fffffff;
        int lastWarpedJ = 0x7fffffff;
        for (int w = 0; w < shrunkWarpPath.size(); w++) {
            Pair<Integer, Integer> currentCell = shrunkWarpPath.get(w);
            int warpedI = currentCell.x();
            int warpedJ = currentCell.y();
            int blockISize = shrunkI.aggregatePtSize(warpedI);
            int blockJSize = shrunkJ.aggregatePtSize(warpedJ);
            if (warpedJ > lastWarpedJ)
                currentJ += shrunkJ.aggregatePtSize(lastWarpedJ);
            if (warpedI > lastWarpedI)
                currentI += shrunkI.aggregatePtSize(lastWarpedI);
            if (warpedJ > lastWarpedJ && warpedI > lastWarpedI) {
                markVisited(currentI - 1, currentJ);
                markVisited(currentI, currentJ - 1);
            }
            for (int x = 0; x < blockISize; x++) {
                markVisited(currentI + x, currentJ);
                markVisited(currentI + x, (currentJ + blockJSize) - 1);
            }

            lastWarpedI = warpedI;
            lastWarpedJ = warpedJ;
        }

        expandWindow(searchRadius);
    }

    final void expandWindow(int radius) {
        if (radius > 0) {

            // XXX can be done in one step?
            expandSearchWindow(1);
            expandSearchWindow(radius - 1);
        }
    }

    final int minJforI(int i) {
        return minValues[i];
    }

    final int maxJforI(int i) {
        return maxValues[i];
    }

    private final class SearchWindowIterator implements Iterator<Pair<Integer, Integer>> {

        public boolean hasNext() {
            return hasMoreElements;
        }

        public Pair<Integer, Integer> next() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (!hasMoreElements)
                throw new NoSuchElementException();
            Pair<Integer, Integer> cell = new Pair<Integer, Integer>(currentI, currentJ);
            if (++currentJ > window.maxJforI(currentI))
                if (++currentI <= window.maxI)
                    currentJ = window.minJforI(currentI);
                else
                    hasMoreElements = false;
            return cell;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private int currentI;

        private int currentJ;

        private final SearchWindow window;

        private boolean hasMoreElements;

        private final int expectedModCount;

        private SearchWindowIterator(SearchWindow w) {
            window = w;
            hasMoreElements = w.size > 0;// window.size() > 0;
            currentI = 0;// window.minI();
            currentJ = 0;// window.minJ();
            expectedModCount = w.modCount;
        }

    }

    private final void expandSearchWindow(int radius) {
        if (radius > 0) {
            ArrayList<Pair<Integer, Integer>> windowCells = new ArrayList<Pair<Integer, Integer>>(size);

            Iterator<Pair<Integer, Integer>> cellIter = this.iterator();
            while (cellIter.hasNext()) {
                windowCells.add(cellIter.next());
            }
            for (int cell = 0; cell < windowCells.size(); cell++) {
                Pair<Integer, Integer> currentCell = windowCells.get(cell);
                int tarx;
                int tary;
                if (currentCell.x() != minI && currentCell.y() != maxJ) {
                    tarx = currentCell.x() - radius;
                    tary = currentCell.y() + radius;
                    if (tarx >= minI && tary <= maxJ) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = Math.max(minI - tarx, tary - maxJ);
                        markVisited(tarx + cellsPastEdge, tary - cellsPastEdge);
                    }
                }
                if (currentCell.y() != maxJ) {
                    tarx = currentCell.x();
                    tary = currentCell.y() + radius;
                    if (tary <= maxJ) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = tary - maxJ;
                        markVisited(tarx, tary - cellsPastEdge);
                    }
                }
                if (currentCell.x() != maxI && currentCell.y() != maxJ) {
                    tarx = currentCell.x() + radius;
                    tary = currentCell.y() + radius;
                    if (tarx <= maxI && tary <= maxJ) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = Math.max(tarx - maxI, tary - maxJ);
                        markVisited(tarx - cellsPastEdge, tary - cellsPastEdge);
                    }
                }
                if (currentCell.x() != minI) {
                    tarx = currentCell.x() - radius;
                    tary = currentCell.y();
                    if (tarx >= minI) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = minI - tarx;
                        markVisited(tarx + cellsPastEdge, tary);
                    }
                }
                if (currentCell.x() != maxI) {
                    tarx = currentCell.x() + radius;
                    tary = currentCell.y();
                    if (tarx <= maxI) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = tarx - maxI;
                        markVisited(tarx - cellsPastEdge, tary);
                    }
                }
                if (currentCell.x() != minI && currentCell.y() != minJ) {
                    tarx = currentCell.x() - radius;
                    tary = currentCell.y() - radius;
                    if (tarx >= minI && tary >= minJ) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = Math.max(minI - tarx, minJ - tary);
                        markVisited(tarx + cellsPastEdge, tary + cellsPastEdge);
                    }
                }
                if (currentCell.y() != minJ) {
                    tarx = currentCell.x();
                    tary = currentCell.y() - radius;
                    if (tary >= minJ) {
                        markVisited(tarx, tary);
                    } else {
                        int cellsPastEdge = minJ - tary;
                        markVisited(tarx, tary + cellsPastEdge);
                    }
                }
                if (currentCell.x() == maxI || currentCell.y() == minJ)
                    continue;
                tarx = currentCell.x() + radius;
                tary = currentCell.y() - radius;
                if (tarx <= maxI && tary >= minJ) {
                    markVisited(tarx, tary);
                } else {
                    int cellsPastEdge = Math.max(tarx - maxI, minJ - tary);
                    markVisited(tarx - cellsPastEdge, tary + cellsPastEdge);
                }
            }

        }
    }

    final void markVisited(int col, int row) {
        if (minValues[col] == -1) {
            minValues[col] = row;
            maxValues[col] = row;
            size++;
            modCount++;
        } else if (minValues[col] > row) {
            size += minValues[col] - row;
            minValues[col] = row;
            modCount++;
        } else if (maxValues[col] < row) {
            size += row - maxValues[col];
            maxValues[col] = row;
            modCount++;
        }
    }

    int size() {
        return size;
    }

    int maxI() {
        return maxI;
    }

    Iterator<Pair<Integer, Integer>> iterator() {
        return new SearchWindowIterator(this);
    }

}