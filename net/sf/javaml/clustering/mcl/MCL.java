/**
 * MCL.java
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
 * Copyright (c) 2006-2007, Thomas Abeel
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.mcl;

import java.util.Vector;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.AbstractSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

public class MCL implements Clusterer {

    /**
     * XXX doc
     * 
     * @param dm
     */
    public MCL(DistanceMeasure dm) {
        this(dm, 0.001, 2.0, 0, 0.001);

    }

    /**
     * XXX doc
     * 
     * @param dm
     * @param maxResidual
     * @param gamma
     * @param loopGain
     * @param maxZero
     */
    public MCL(DistanceMeasure dm, double maxResidual, double pGamma, double loopGain, double maxZero) {
        if (!(dm instanceof AbstractSimilarity))
            throw new RuntimeException("MCL requires the distance measure to be a Similarity measure");

        
        this.dm = dm;
        this.maxResidual = maxResidual;
        this.pGamma = pGamma;
        this.loopGain = loopGain;
        this.maxZero = maxZero;
    }

    private DistanceMeasure dm;

    // Maximum difference between row elements and row square sum (measure of
    // idempotence)
    private double maxResidual = 0.001;

    // inflation exponent for Gamma operator
    private double pGamma = 2.0;

    // loopGain values for cycles
    private double loopGain = 0.;

    // maximum value considered zero for pruning operations
    private double maxZero = 0.001;

    public Dataset[] executeClustering(Dataset data) {
        SparseMatrix dataSparseMatrix = new SparseMatrix();
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j <= i; j++) {
                Instance x = data.getInstance(i);
                Instance y = data.getInstance(j);
                double dist = dm.calculateDistance(x, y);
                if (dist > maxZero)
                    dataSparseMatrix.add(i, j, dm.calculateDistance(x, y));
            }
        }

        MarkovClustering mcl = new MarkovClustering();
        SparseMatrix matrix = mcl.run(dataSparseMatrix, maxResidual, pGamma, loopGain, maxZero);
        
        // convert matrix to output dataset:
        int[] sparseMatrixSize = matrix.getSize();
        // find number of attractors (non zero values) in diagonal
        int attractors = 0;
        for (int i = 0; i < sparseMatrixSize[0]; i++) {
            double val = matrix.get(i, i);
            if (val != 0) {
                attractors++;
            }
        }
        // create cluster for each attractor with value close to 1
        Vector<Vector<Instance>> finalClusters = new Vector<Vector<Instance>>();

        for (int i = 0; i < sparseMatrixSize[0]; i++) {
            Vector<Instance> cluster = new Vector<Instance>();
            double val = matrix.get(i, i);
            if (val >= 0.98) {
                for (int j = 0; j < sparseMatrixSize[0]; j++) {
                    double value = matrix.get(j, i);
                    if (value != 0) {
                        cluster.add(data.getInstance(j));
                    }
                }
                finalClusters.add(cluster);
            }
        }
       
        Dataset[] output = new Dataset[finalClusters.size()];
        for (int i = 0; i < finalClusters.size(); i++) {
            output[i] = new SimpleDataset();
        }
        for (int i = 0; i < finalClusters.size(); i++) {
            Vector<Instance> getCluster = new Vector<Instance>();
            getCluster = finalClusters.get(i);
            for (int j = 0; j < getCluster.size(); j++) {
                output[i].addInstance(getCluster.get(j));
            }
        }

        return output;

    }

}
