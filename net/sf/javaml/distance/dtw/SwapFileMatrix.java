/**
 * SwapFileMatrix.java
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SwapFileMatrix extends CostMatrix {

    SwapFileMatrix(SearchWindow searchWindow) throws IOException {
                window = searchWindow;
        if (window.maxI() > 0) {
            currCol = new double[(window.maxJforI(1) - window.minJforI(1)) + 1];
            currColIndex = 1;
            minLastRow = window.minJforI(currColIndex - 1);
        } else {
            currColIndex = 0;
        }
        minCurrRow = window.minJforI(currColIndex);
        lastCol = new double[(window.maxJforI(0) - window.minJforI(0)) + 1];
        File swapFile = File.createTempFile("swap", "matrix");
        swapFile.deleteOnExit();
        colOffsets = new long[window.maxI() + 1];
        cellValuesFile = new RandomAccessFile(swapFile, "rw");

    }

    public void put(int col, int row, double value) {
        if (row < window.minJforI(col) || row > window.maxJforI(col))
            throw new InternalError("CostMatrix is filled in a cell (col=" + col + ", row=" + row
                    + ") that is not in the " + "search window");
        if (col == currColIndex)
            currCol[row - minCurrRow] = value;
        else if (col == currColIndex - 1)
            lastCol[row - minLastRow] = value;
        else if (col == currColIndex + 1) {
            try {
                cellValuesFile.seek(cellValuesFile.length());
                colOffsets[currColIndex - 1] = cellValuesFile.getFilePointer();
                for(double d:lastCol){
                    cellValuesFile.writeDouble(d);
                }
            } catch (IOException e) {
                throw new InternalError("Unable to fill the CostMatrix in the Swap file (IOException)");
            }
            lastCol = currCol;
            minLastRow = minCurrRow;
            minCurrRow = window.minJforI(col);
            currColIndex++;
            currCol = new double[(window.maxJforI(col) - window.minJforI(col)) + 1];
            currCol[row - minCurrRow] = value;
        } else {
            throw new InternalError("A SwapFileMatrix can only fill in 2 adjacent columns at a time");
        }
    }

    public double get(int col, int row) {
        if (row < window.minJforI(col) || row > window.maxJforI(col))
            return (1.0D / 0.0D);
        if (col == currColIndex)
            return currCol[row - minCurrRow];
        if (col == currColIndex - 1)
            return lastCol[row - minLastRow];
        try {
            cellValuesFile.seek(colOffsets[col] + (long) (8 * (row - window.minJforI(col))));
            return cellValuesFile.readDouble();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (col > currColIndex)
            throw new InternalError(
                    "The requested value is in the search window but has not been entered into the matrix: (col=" + col
                            + "row=" + row + ").");
        else
            throw new InternalError("Unable to read CostMatrix in the Swap file (IOException)");
    }

    public int size() {
        return window.size();
    }

    private final SearchWindow window;

    private double lastCol[];

    private double currCol[];

    private int currColIndex;

    private int minLastRow;

    private int minCurrRow;

    private final RandomAccessFile cellValuesFile;

    private final long colOffsets[];

}