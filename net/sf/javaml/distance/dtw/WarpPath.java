/**
 * WarpPath.java
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
import java.util.NoSuchElementException;

import net.sf.javaml.core.Pair;

final class WarpPath {

    WarpPath() {
        tsIindexes = new ArrayList<Integer>();
        tsJindexes = new ArrayList<Integer>();
    }
 WarpPath(int initialCapacity) {
        this();
        tsIindexes.ensureCapacity(initialCapacity);
        tsJindexes.ensureCapacity(initialCapacity);
    }

     int size() {
        return tsIindexes.size();
    }

     int minI() {
        return tsIindexes.get(0);
    }

     int minJ() {
        return  tsJindexes.get(0);
    }

     int maxI() {
        return tsIindexes.get(tsIindexes.size() - 1);
    }
 int maxJ() {
        return tsJindexes.get(tsJindexes.size() - 1);
    }

     void addFirst(int i, int j) {
        tsIindexes.add(0, new Integer(i));
        tsJindexes.add(0, new Integer(j));
    }

     void addLast(int i, int j) {
        tsIindexes.add(new Integer(i));
        tsJindexes.add(new Integer(j));
    }

     ArrayList getMatchingIndexesForI(int i) {
        int index = tsIindexes.indexOf(i);
        if (index < 0)
            throw new InternalError("ERROR:  index '" + i + " is not in the " + "warp path.");
        ArrayList<Integer> matchingJs = new ArrayList<Integer>();
        while (index < tsIindexes.size() && tsIindexes.get(index) == i) {
            matchingJs.add(tsJindexes.get(index));
            index++;
        }
        return matchingJs;
    }

     ArrayList getMatchingIndexesForJ(int j) {
        int index = tsJindexes.indexOf(j);
        if (index < 0)
            throw new InternalError("ERROR:  index '" + j + " is not in the " + "warp path.");
        ArrayList<Integer> matchingIs = new ArrayList<Integer>();
        while (index < tsJindexes.size() && tsJindexes.get(index) == j) {
            matchingIs.add(tsIindexes.get(index));
            index++;
        }
        return matchingIs;
    }

     WarpPath invertedCopy() {
        WarpPath newWarpPath = new WarpPath();
        for (int x = 0; x < tsIindexes.size(); x++)
            newWarpPath.addLast(tsJindexes.get(x), tsIindexes.get(x));

        return newWarpPath;
    }

     void invert() {
        for (int x = 0; x < tsIindexes.size(); x++) {
            int temp = tsIindexes.get(x);
            tsIindexes.set(x, tsJindexes.get(x));
            tsJindexes.set(x, temp);
        }

    }

     Pair<Integer,Integer> get(int index) {
        if (index > size() || index < 0)
            throw new NoSuchElementException();
        else
            return new Pair<Integer,Integer>(tsIindexes.get(index), tsJindexes.get(index));
    }

    public String toString() {
        StringBuffer outStr = new StringBuffer("[");
        for (int x = 0; x < tsIindexes.size(); x++) {
            outStr.append("(" + tsIindexes.get(x) + "," + tsJindexes.get(x) + ")");
            if (x < tsIindexes.size() - 1)
                outStr.append(",");
        }

        return new String(outStr.append("]"));
    }

    public boolean equals(Object obj) {
        if (obj instanceof WarpPath) {
            WarpPath p = (WarpPath) obj;
            if (p.size() == size() && p.maxI() == maxI() && p.maxJ() == maxJ()) {
                for (int x = 0; x < size(); x++)
                    if (!get(x).equals(p.get(x)))
                        return false;

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return tsIindexes.hashCode() * tsJindexes.hashCode();
    }

    private final ArrayList<Integer> tsIindexes;

    private final ArrayList<Integer> tsJindexes;
}