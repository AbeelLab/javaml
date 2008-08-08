/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.dtw;

import java.io.IOException;

abstract class CostMatrix {

    
    public static CostMatrix create(SearchWindow window){
        try {
            return new MemoryResidentMatrix(window);
        } catch (OutOfMemoryError e) {
            // System.err.println("Ran out of memory initializing window matrix,
            // all cells in the window cannot fit into main memory. Will use a
            // swap file instead (will run ~50% slower)");
            System.gc();
            try {
               return new SwapFileMatrix(window);
            } catch (IOException e1) {
                return null;
            }
        }
    }
    
    abstract void put(int col, int row, double value);
    abstract double get(int col, int row) ;
    abstract int size() ;

   
}
