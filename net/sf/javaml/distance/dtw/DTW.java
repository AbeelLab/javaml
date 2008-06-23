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

import java.util.Iterator;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.Pair;
import net.sf.javaml.distance.AbstractDistance;

public class DTW extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = 4169875204448943908L;

    static Pair<Double, WarpPath> dtw(Instance tsI, Instance tsJ) {
        double costMatrix[][] = new double[tsI.noAttributes()][tsJ.noAttributes()];
        int maxI = tsI.noAttributes() - 1;
        int maxJ = tsJ.noAttributes() - 1;
        costMatrix[0][0] = euclideanDist(tsI.value(0), tsJ.value(0));
        for (int j = 1; j <= maxJ; j++)
            costMatrix[0][j] = costMatrix[0][j - 1] + euclideanDist(tsI.value(0), tsJ.value(j));

        for (int i = 1; i <= maxI; i++) {
            costMatrix[i][0] = costMatrix[i - 1][0] + euclideanDist(tsI.value(i), tsJ.value(0));
            for (int j = 1; j <= maxJ; j++) {
                double minGlobalCost = Math.min(costMatrix[i - 1][j], Math.min(costMatrix[i - 1][j - 1],
                        costMatrix[i][j - 1]));
                costMatrix[i][j] = minGlobalCost + euclideanDist(tsI.value(i), tsJ.value(j));
            }

        }

        double minimumCost = costMatrix[maxI][maxJ];
        WarpPath minCostPath = new WarpPath((maxI + maxJ) - 1);
        int i = maxI;
        int j = maxJ;
        minCostPath.addFirst(i, j);
        for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
            double diagCost;
            if (i > 0 && j > 0)
                diagCost = costMatrix[i - 1][j - 1];
            else
                diagCost = (1.0D / 0.0D);
            double leftCost;
            if (i > 0)
                leftCost = costMatrix[i - 1][j];
            else
                leftCost = (1.0D / 0.0D);
            double downCost;
            if (j > 0)
                downCost = costMatrix[i][j - 1];
            else
                downCost = (1.0D / 0.0D);
            if (diagCost <= leftCost && diagCost <= downCost) {
                i--;
                j--;
                continue;
            }
            if (leftCost < diagCost && leftCost < downCost) {
                i--;
                continue;
            }
            if (downCost < diagCost && downCost < leftCost) {
                j--;
                continue;
            }
            if (i <= j)
                j--;
            else
                i--;
        }

        return new Pair<Double, WarpPath>(minimumCost, minCostPath);
    }

    static Pair<Double, WarpPath> dtw(Instance tsI, Instance tsJ, SearchWindow window) {
        CostMatrix costMatrix = CostMatrix.create(window);
        int maxI = tsI.noAttributes() - 1;
        int maxJ = tsJ.noAttributes() - 1;
        for (Iterator<Pair<Integer, Integer>> matrixIterator = window.iterator(); matrixIterator.hasNext();) {
            Pair<Integer, Integer> currentCell = matrixIterator.next();
            int i = currentCell.x();
            int j = currentCell.y();
            if (i == 0 && j == 0)
                costMatrix.put(i, j, euclideanDist(tsI.value(0), tsJ.value(0)));
            else if (i == 0)
                costMatrix.put(i, j, euclideanDist(tsI.value(0), tsJ.value(j)) + costMatrix.get(i, j - 1));
            else if (j == 0) {
                costMatrix.put(i, j, euclideanDist(tsI.value(i), tsJ.value(0)) + costMatrix.get(i - 1, j));
            } else {
                double minGlobalCost = Math.min(costMatrix.get(i - 1, j), Math.min(costMatrix.get(i - 1, j - 1),
                        costMatrix.get(i, j - 1)));
                costMatrix.put(i, j, minGlobalCost + euclideanDist(tsI.value(i), tsJ.value(j)));
            }
        }

        double minimumCost = costMatrix.get(maxI, maxJ);
        WarpPath minCostPath = new WarpPath((maxI + maxJ) - 1);
        int i = maxI;
        int j = maxJ;
        minCostPath.addFirst(i, j);
        for (; i > 0 || j > 0; minCostPath.addFirst(i, j)) {
            double diagCost;
            if (i > 0 && j > 0)
                diagCost = costMatrix.get(i - 1, j - 1);
            else
                diagCost = (1.0D / 0.0D);
            double leftCost;
            if (i > 0)
                leftCost = costMatrix.get(i - 1, j);
            else
                leftCost = (1.0D / 0.0D);
            double downCost;
            if (j > 0)
                downCost = costMatrix.get(i, j - 1);
            else
                downCost = (1.0D / 0.0D);
            if (diagCost <= leftCost && diagCost <= downCost) {
                i--;
                j--;
                continue;
            }
            if (leftCost < diagCost && leftCost < downCost) {
                i--;
                continue;
            }
            if (downCost < diagCost && downCost < leftCost) {
                j--;
                continue;
            }
            if (i <= j)
                j--;
            else
                i--;
        }

        return new Pair<Double, WarpPath>(minimumCost, minCostPath);
    }

    private static double euclideanDist(double vector1, double vector2) {
        return Math.sqrt((vector1 - vector2) * (vector1 - vector2));
    }

    public double measure(Instance i, Instance j) {
        return dtw(i, j).x();
    }

    public WarpPath getWarpPath(Instance tsI, Instance tsJ) {
        return dtw(tsI, tsJ).y();
    }
}
