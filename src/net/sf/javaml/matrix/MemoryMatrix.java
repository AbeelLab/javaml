/**
 * %SVN.HEADER%
 */
package net.sf.javaml.matrix;

/**
 * A memory resident matrix.
 * 
 * @author Thomas Abeel
 * 
 */
final class MemoryMatrix extends Matrix {

    double[][] matrix;

    public MemoryMatrix(int cols, int rows) {
        matrix = new double[cols][rows];
    }

    @Override
    public int columns() {
        return matrix.length;
    }

    @Override
    public double get(int col, int row) {
        return matrix[col][row];
    }

    @Override
    public void put(int col, int row, double value) {
        matrix[col][row] = value;

    }

    @Override
    public int rows() {
        return matrix[0].length;
    }

}
