/**
 * %SVN.HEADER%
 */
package net.sf.javaml.matrix;

import java.io.IOException;

public abstract class Matrix {

    public static Matrix create(int cols, int rows) {
        try {
            return new MemoryMatrix(cols, rows);
        } catch (OutOfMemoryError e) {
            System.gc();

            try {
                return new SwapFileMatrix(cols, rows);
            } catch (IOException e1) {
                return null;
            }

        }
    }

    public abstract void put(int col, int row, double value);

    public abstract double get(int col, int row);

    public abstract int rows();

    public abstract int columns();
}
