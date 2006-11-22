/**
 * Clusterer.java, 11-okt-06
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public interface Clusterer {
    /**
     * Create a clusterer with the give dataset.
     * 
     * @param data
     *            the dataset to be used to create the clusterer
     */
    public void buildClusterer(Dataset data);

    /**
     * Predict to which cluster the instance belongs.
     * 
     * @param instance
     *            the instance to be clustered
     * @return the index of the cluster to which the instance belongs
     */
    public int predictCluster(Instance instance);

    /**
     * Predict the memberschip distribution of the instance for all the clusters
     * in the clusterer.
     * 
     * @param instance
     * @return the distribution for the membership for all clusters
     */
    public double[] predictMembershipDistribution(Instance instance);

    /**
     * Gives the number of clusters in the clusterer.
     * 
     * @return
     */
    public int getNumberOfClusters();

}
