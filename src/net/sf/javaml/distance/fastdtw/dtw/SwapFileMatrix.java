/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import net.sf.javaml.distance.fastdtw.lang.TypeConversions;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
class SwapFileMatrix implements CostMatrix {

    SwapFileMatrix(SearchWindow searchWindow) {
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
        try {
            swapFile = File.createTempFile("swap", "dat");
            swapFile.deleteOnExit();
            isSwapFileFreed = false;
            colOffsets = new long[window.maxI() + 1];

            cellValuesFile = new RandomAccessFile(swapFile, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                if (isSwapFileFreed)
                    throw new InternalError("The SwapFileMatrix has been freeded by the freeMem() method");
                cellValuesFile.seek(cellValuesFile.length());
                colOffsets[currColIndex - 1] = cellValuesFile.getFilePointer();
                cellValuesFile.write(TypeConversions.doubleArrayToByteArray(lastCol));
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
            throw new InternalError("A SwapFileMatrix can only fill in 2 adjacentcolumns at a time");
        }
    }

    public double get(int col, int row) {
        try {
            if (row < window.minJforI(col) || row > window.maxJforI(col))
                return (1.0D / 0.0D);
            if (col == currColIndex)
                return currCol[row - minCurrRow];
            if (col == currColIndex - 1)
                return lastCol[row - minLastRow];
            if (isSwapFileFreed)
                throw new InternalError("The SwapFileMatrix has been freeded by the freeMem() method");
            cellValuesFile.seek(colOffsets[col] + (long) (8 * (row - window.minJforI(col))));
            return cellValuesFile.readDouble();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (col > currColIndex)
            throw new InternalError(
                    "The requested value is in the search window but has not been entered into the matrix: (col=" + col
                            + "row=" + row + ").");
        else
            throw new InternalError("Unable to read CostMatrix in the Swap file (IOException)");
    }

    // protected void finalize()
    // throws Throwable
    // {
    // try{
    // if(!isSwapFileFreed)
    // cellValuesFile.close();
    // swapFile.delete();
    // super.finalize();
    // break MISSING_BLOCK_LABEL_96;
    // }catch(Exception e){
    // e.printStackTrace();
    // }
    // System.err.println("unable to close swap file '" + swapFile.getPath() +
    // "' during finialization");
    // swapFile.delete();
    // super.finalize();
    // break MISSING_BLOCK_LABEL_96;
    // Exception exception;
    // exception;
    // swapFile.delete();
    // super.finalize();
    // throw exception;
    // }

    public int size() {
        return window.size();
    }

    public void freeMem() {
        try {
            cellValuesFile.close();
        } catch (IOException e) {
            System.err.println("unable to close swap file '" + swapFile.getPath() + "'");
        } finally {
            if (!swapFile.delete())
                System.err.println("unable to delete swap file '" + swapFile.getPath() + "'");
        }
    }

    private static final double OUT_OF_WINDOW_VALUE = (1.0D / 0.0D);

    private static final Random RAND_GEN = new Random();

    private final SearchWindow window;

    private double lastCol[];

    private double currCol[];

    private int currColIndex;

    private int minLastRow;

    private int minCurrRow;

    private File swapFile;

    private RandomAccessFile cellValuesFile;

    private boolean isSwapFileFreed;

    private long colOffsets[];

}