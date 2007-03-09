/**
 * TraceScatterMatrix.java
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
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * E_1 from the Zhao 2001 paper
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 */

public class TraceScatterMatrix implements ClusterEvaluation {
    public TraceScatterMatrix(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm = new CosineSimilarity();

    public double score(Dataset[] datas) {

        // calculate centroid complete dataset
        int instanceLength = datas[0].getInstance(0).size();
        float[] sumVector = new float[instanceLength];
        int count = 0;
        for (int i = 0; i < datas.length; i++) {
            for (int j = 0; j < datas[i].size(); j++) {
                Instance in = datas[i].getInstance(j);
                for (int k = 0; k < instanceLength; k++) {
                    sumVector[k] += in.getValue(k) * in.getWeight();
                    count++;
                }
            }
        }
        for (int j = 0; j < instanceLength; j++) {
            sumVector[j] /= count;
        }
        Instance mCentroid = new SimpleInstance(sumVector);
        System.out.println(mCentroid);
        // calculate centroids
        double[][] sumPosition = new double[datas.length][instanceLength];
        int[] countPosition = new int[datas.length];
        for (int i = 0; i < datas.length; i++) {
            for (int j = 0; j < datas[i].size(); j++) {
                Instance in = datas[i].getInstance(i);

                for (int k = 0; k < instanceLength; k++) {
                	// TODO check why crash here...???
                    sumPosition[i][k] += in.getWeight() * in.getValue(j);

                }
                countPosition[i]++;
            }
        }
        // DistanceMeasure
        Instance[] centroids = new Instance[datas.length];
        for (int i = 0; i < datas.length; i++) {
            float[] tmp = new float[instanceLength];
            for (int j = 0; j < instanceLength; j++) {
                tmp[j] = (float) sumPosition[i][j] / countPosition[i];
            }
            centroids[i] = new SimpleInstance(tmp);

        }
        // calculate trace of the between-cluster scatter matrix.
        double sum = 0;
        for (int i = 0; i < datas.length; i++) {
            double error = dm.calculateDistance(centroids[i], mCentroid);
            sum += error * datas[i].size();
        }
        return sum;
    }

    public boolean compareScore(double score1, double score2) {
        // should be minimized
        return score2 < score1;
    }
}
