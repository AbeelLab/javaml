/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.matrix;

public class ColMajorCell {

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public ColMajorCell(int column, int row) {
        col = column;
        this.row = row;
    }

    public boolean equals(Object o) {
        return (o instanceof ColMajorCell) && ((ColMajorCell) o).col == col && ((ColMajorCell) o).row == row;
    }

    public int hashCode() {
        return (1 << col) + row;
    }

    public String toString() {
        return "(" + col + "," + row + ")";
    }

    private final int col;

    private final int row;
}