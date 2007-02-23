/**
 * SumOfCentroidSimilarities.java
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
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;

/**
 * TODO uitleg I_2 from Zhao 2001
 * 
 * @author Thomas Abeel
 * 
 */
public class SumOfCentroidSimilarities implements ClusterEvaluation {
    public double score(Dataset[] datas) {
        
        // calculate centroids
        int instanceLength = data.getInstance(0).size();
        double[][] sumPosition = new double[c.getNumberOfClusters()][instanceLength];
        int[] countPosition = new int[c.getNumberOfClusters()];
        for (int i = 0; i < data.size(); i++) {
            Instance in = data.getInstance(i);
            int predictedIndex = c.predictCluster(in);
            for (int j = 0; j < instanceLength; j++) {

                sumPosition[predictedIndex][j] += in.getWeight() * in.getValue(j);

            }
            countPosition[predictedIndex]++;
        }
        // DistanceMeasure
        // cdm=DistanceMeasureFactory.getEuclideanDistanceMeasure();
        Instance[] centroids = new Instance[c.getNumberOfClusters()];
        for (int i = 0; i < c.getNumberOfClusters(); i++) {
            float[] tmp = new float[instanceLength];
            for (int j = 0; j < instanceLength; j++) {
                tmp[j] = (float) sumPosition[i][j] / countPosition[i];
            }
            centroids[i] = new SimpleInstance(tmp);

        }
        // calculate sum of centroid similarities.
        DistanceMeasure dm = DistanceMeasureFactory.getCosineSimilarity();
        double sum = 0;
        for (int i = 0; i < c.getNumberOfClusters(); i++) {
            for (int j = 0; j < datas[i].size(); j++) {
                double error = dm.calculateDistance(datas[i].getInstance(j), centroids[i]);
                sum += error;

            }

        }
        return sum;
    }

    public boolean compareScore(double score1, double score2) {
        // should be maxed
        return score2 > score1;
    }
}
