/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
class WindowMatrix implements CostMatrix {

    WindowMatrix(SearchWindow searchWindow) {
        try {
            windowCells = new MemoryResidentMatrix(searchWindow);
        } catch (OutOfMemoryError e) {
            System.err
                    .println("Ran out of memory initializing window matrix, all cells in the window cannot fit into main memory.  Will use a swap file instead (will run ~50% slower)");
            System.gc();
            windowCells = new SwapFileMatrix(searchWindow);
        }
    }

    public void put(int col, int row, double value) {
        windowCells.put(col, row, value);
    }

    public double get(int col, int row) {
        return windowCells.get(col, row);
    }

    public int size() {
        return windowCells.size();
    }

    public void freeMem() {
        if (windowCells instanceof SwapFileMatrix)
            try {
                ((SwapFileMatrix) windowCells).freeMem();
            } catch (Throwable t) {
            }
    }

    private CostMatrix windowCells;
}