/**
 * CostMatrix.java
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
