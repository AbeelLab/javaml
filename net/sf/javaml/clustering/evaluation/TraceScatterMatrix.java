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
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * E_1 from the Zhao 2001 paper 
 * 
 * XXX DOC 
 * 
 * Distance measure has to be
 * CosineSimilarity 
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */

public class TraceScatterMatrix implements ClusterEvaluation {

    /**
     * XXX DOC
     */
    private DistanceMeasure dm = new CosineSimilarity();

    /**
     * XXX DOC
     */
    public double score(Dataset[] clusters) {
        Instance [] clusterCentroid= new Instance[clusters.length];
        Instance overAllCentroid;
        int [] clusterSizes = new int[clusters.length];
       
        // calculate centroids of each cluster
        for (int i = 0; i < clusters.length; i++) {
            clusterCentroid[i] = DatasetTools.getCentroid(clusters[i]);
            clusterSizes[i]=clusters[i].size();
        }

        // calculate centroid all instances
        // firs put all cluster back together
        Dataset data = new SimpleDataset();
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].size(); j++) {
                data.add(clusters[i].instance(j));
            }
        }
        overAllCentroid = DatasetTools.getCentroid(data);
        // calculate trace of the between-cluster scatter matrix.
        double sum = 0;
        for (int i = 0; i < clusters.length; i++) {
            double cos = dm.calculateDistance(clusterCentroid[i], overAllCentroid);
            sum += cos * clusterSizes[i];
        }
        return sum;
    }

    /**
     * XXX DOC
     */
    public boolean compareScore(double score1, double score2) {
        // should be minimized
        return score2 < score1;
    }
}
