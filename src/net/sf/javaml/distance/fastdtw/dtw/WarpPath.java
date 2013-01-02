/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public class WarpPath {

    public WarpPath() {
        tsIindexes = new ArrayList();
        tsJindexes = new ArrayList();
    }

    public WarpPath(int initialCapacity) {
        this();
        tsIindexes.ensureCapacity(initialCapacity);
        tsJindexes.ensureCapacity(initialCapacity);
    }

    public WarpPath(String inputFile) {
        this();
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",", false);
                if (st.countTokens() == 2) {
                    tsIindexes.add(new Integer(st.nextToken()));
                    tsJindexes.add(new Integer(st.nextToken()));
                } else {
                    throw new InternalError("The Warp Path File '" + inputFile
                            + "' has an incorrect format.  There must be\n"
                            + "two numbers per line separated by commas");
                }
            }
        } catch (FileNotFoundException e) {
            throw new InternalError("ERROR:  The file '" + inputFile + "' was not found.");
        } catch (IOException e) {
            throw new InternalError("ERROR:  Problem reading the file '" + inputFile + "'.");
        }
    }

    public int size() {
        return tsIindexes.size();
    }

    public int minI() {
        return ((Integer) tsIindexes.get(0)).intValue();
    }

    public int minJ() {
        return ((Integer) tsJindexes.get(0)).intValue();
    }

    public int maxI() {
        return ((Integer) tsIindexes.get(tsIindexes.size() - 1)).intValue();
    }

    public int maxJ() {
        return ((Integer) tsJindexes.get(tsJindexes.size() - 1)).intValue();
    }

    public void addFirst(int i, int j) {
        tsIindexes.add(0, new Integer(i));
        tsJindexes.add(0, new Integer(j));
    }

    public void addLast(int i, int j) {
        tsIindexes.add(new Integer(i));
        tsJindexes.add(new Integer(j));
    }

    public ArrayList getMatchingIndexesForI(int i) {
        int index = tsIindexes.indexOf(new Integer(i));
        if (index < 0)
            throw new InternalError("ERROR:  index '" + i + " is not in the " + "warp path.");
        ArrayList matchingJs = new ArrayList();
        for (; index < tsIindexes.size() && tsIindexes.get(index).equals(new Integer(i)); matchingJs.add(tsJindexes
                .get(index++)))
            ;
        return matchingJs;
    }

    public ArrayList getMatchingIndexesForJ(int j) {
        int index = tsJindexes.indexOf(new Integer(j));
        if (index < 0)
            throw new InternalError("ERROR:  index '" + j + " is not in the " + "warp path.");
        ArrayList matchingIs = new ArrayList();
        for (; index < tsJindexes.size() && tsJindexes.get(index).equals(new Integer(j)); matchingIs.add(tsIindexes
                .get(index++)))
            ;
        return matchingIs;
    }

    public WarpPath invertedCopy() {
        WarpPath newWarpPath = new WarpPath();
        for (int x = 0; x < tsIindexes.size(); x++)
            newWarpPath.addLast(((Integer) tsJindexes.get(x)).intValue(), ((Integer) tsIindexes.get(x)).intValue());

        return newWarpPath;
    }

    public void invert() {
        for (int x = 0; x < tsIindexes.size(); x++) {
            Object temp = tsIindexes.get(x);
            tsIindexes.set(x, tsJindexes.get(x));
            tsJindexes.set(x, temp);
        }

    }

    public ColMajorCell get(int index) {
        if (index > size() || index < 0)
            throw new NoSuchElementException();
        else
            return new ColMajorCell(((Integer) tsIindexes.get(index)).intValue(), ((Integer) tsJindexes.get(index))
                    .intValue());
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

    private final ArrayList tsIindexes;

    private final ArrayList tsJindexes;
}