/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.javaml.distance.fastdtw.matrix.ColMajorCell;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
public abstract class SearchWindow {

    private final class SearchWindowIterator implements Iterator {

        public boolean hasNext() {
            return hasMoreElements;
        }

        public Object next() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (!hasMoreElements)
                throw new NoSuchElementException();
            ColMajorCell cell = new ColMajorCell(currentI, currentJ);
            if (++currentJ > window.maxJforI(currentI))
                if (++currentI <= window.maxI())
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
            hasMoreElements = window.size() > 0;
            currentI = window.minI();
            currentJ = window.minJ();
            expectedModCount = w.modCount;
        }

    }

    public SearchWindow(int tsIsize, int tsJsize) {
        minValues = new int[tsIsize];
        maxValues = new int[tsIsize];
        Arrays.fill(minValues, -1);
        maxJ = tsJsize - 1;
        size = 0;
        modCount = 0;
    }

    public final boolean isInWindow(int i, int j) {
        return i >= minI() && i <= maxI() && minValues[i] <= j && maxValues[i] >= j;
    }

    public final int minI() {
        return 0;
    }

    public final int maxI() {
        return minValues.length - 1;
    }

    public final int minJ() {
        return 0;
    }

    public final int maxJ() {
        return maxJ;
    }

    public final int minJforI(int i) {
        return minValues[i];
    }

    public final int maxJforI(int i) {
        return maxValues[i];
    }

    public final int size() {
        return size;
    }

    public final Iterator iterator() {
        return new SearchWindowIterator(this);
    }

    public final String toString() {
        StringBuffer outStr = new StringBuffer();
        for (int i = minI(); i <= maxI(); i++) {
            outStr.append("i=" + i + ", j=" + minValues[i] + "..." + maxValues[i]);
            if (i != maxI())
                outStr.append("\n");
        }

        return outStr.toString();
    }

    protected int getModCount() {
        return modCount;
    }

    protected final void expandWindow(int radius) {
        if (radius > 0) {
            expandSearchWindow(1);
            expandSearchWindow(radius - 1);
        }
    }

    private final void expandSearchWindow(int radius) {
        if (radius > 0) {
            ArrayList windowCells = new ArrayList(size());
            for (Iterator cellIter = iterator(); cellIter.hasNext(); windowCells.add(cellIter.next()))
                ;
            for (int cell = 0; cell < windowCells.size(); cell++) {
                ColMajorCell currentCell = (ColMajorCell) windowCells.get(cell);
                int targetCol;
                int targetRow;
                if (currentCell.getCol() != minI() && currentCell.getRow() != maxJ()) {
                    targetCol = currentCell.getCol() - radius;
                    targetRow = currentCell.getRow() + radius;
                    if (targetCol >= minI() && targetRow <= maxJ()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = Math.max(minI() - targetCol, targetRow - maxJ());
                        markVisited(targetCol + cellsPastEdge, targetRow - cellsPastEdge);
                    }
                }
                if (currentCell.getRow() != maxJ()) {
                    targetCol = currentCell.getCol();
                    targetRow = currentCell.getRow() + radius;
                    if (targetRow <= maxJ()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = targetRow - maxJ();
                        markVisited(targetCol, targetRow - cellsPastEdge);
                    }
                }
                if (currentCell.getCol() != maxI() && currentCell.getRow() != maxJ()) {
                    targetCol = currentCell.getCol() + radius;
                    targetRow = currentCell.getRow() + radius;
                    if (targetCol <= maxI() && targetRow <= maxJ()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = Math.max(targetCol - maxI(), targetRow - maxJ());
                        markVisited(targetCol - cellsPastEdge, targetRow - cellsPastEdge);
                    }
                }
                if (currentCell.getCol() != minI()) {
                    targetCol = currentCell.getCol() - radius;
                    targetRow = currentCell.getRow();
                    if (targetCol >= minI()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = minI() - targetCol;
                        markVisited(targetCol + cellsPastEdge, targetRow);
                    }
                }
                if (currentCell.getCol() != maxI()) {
                    targetCol = currentCell.getCol() + radius;
                    targetRow = currentCell.getRow();
                    if (targetCol <= maxI()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = targetCol - maxI();
                        markVisited(targetCol - cellsPastEdge, targetRow);
                    }
                }
                if (currentCell.getCol() != minI() && currentCell.getRow() != minJ()) {
                    targetCol = currentCell.getCol() - radius;
                    targetRow = currentCell.getRow() - radius;
                    if (targetCol >= minI() && targetRow >= minJ()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = Math.max(minI() - targetCol, minJ() - targetRow);
                        markVisited(targetCol + cellsPastEdge, targetRow + cellsPastEdge);
                    }
                }
                if (currentCell.getRow() != minJ()) {
                    targetCol = currentCell.getCol();
                    targetRow = currentCell.getRow() - radius;
                    if (targetRow >= minJ()) {
                        markVisited(targetCol, targetRow);
                    } else {
                        int cellsPastEdge = minJ() - targetRow;
                        markVisited(targetCol, targetRow + cellsPastEdge);
                    }
                }
                if (currentCell.getCol() == maxI() || currentCell.getRow() == minJ())
                    continue;
                targetCol = currentCell.getCol() + radius;
                targetRow = currentCell.getRow() - radius;
                if (targetCol <= maxI() && targetRow >= minJ()) {
                    markVisited(targetCol, targetRow);
                } else {
                    int cellsPastEdge = Math.max(targetCol - maxI(), minJ() - targetRow);
                    markVisited(targetCol - cellsPastEdge, targetRow + cellsPastEdge);
                }
            }

        }
    }

    protected final void markVisited(int col, int row) {
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

    private final int minValues[];

    private final int maxValues[];

    private final int maxJ;

    private int size;

    private int modCount;

}