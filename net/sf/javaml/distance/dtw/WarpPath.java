/**
 * %SVN.HEADER%
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
