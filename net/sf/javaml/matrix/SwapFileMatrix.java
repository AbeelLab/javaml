/**
 * %SVN.HEADER%
 */
package net.sf.javaml.matrix;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A matrix that is stored in a file on disk.
 * 
 * @author Thomas Abeel
 * 
 */
final class SwapFileMatrix extends Matrix {

    private RandomAccessFile matrix;

    private int rows;

    private int cols;

    public SwapFileMatrix(int cols, int rows) throws IOException {
        this.cols = cols;
        this.rows = rows;
        File swapFile = File.createTempFile("swap", "matrix");
        swapFile.deleteOnExit();
        matrix = new RandomAccessFile(swapFile, "rw");
    }

    @Override
    public int columns() {
        return cols;
    }

    @Override
    public double get(int col, int row) {
        try {
            matrix.seek((col * row + row) * 8);
            return matrix.readDouble();
        } catch (IOException e) {
            System.err.println("Something went wrong, but we return 0 anyway.");
            return 0;
        }
    }

    @Override
    public void put(int col, int row, double value) {
        try {
            matrix.seek((col * row + row) * 8);
            matrix.writeDouble(value);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    @Override
    public int rows() {
        return rows;
    }

}
