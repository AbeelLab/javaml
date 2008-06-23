/**
 * Clusterer.java
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

package net.sf.javaml.clustering;

import net.sf.javaml.core.Dataset;

/**
 * A common interface for all clustering techniques. There is only one method
 * that should be implemented.
 * 
 * @author Thomas Abeel
 * 
 */
public interface Clusterer {
    /**
     * This method will execute the clustering algorithm on a particular
     * data set. The result will be an array of Dataset where each data set is a
     * cluster.
     * 
     * @param data
     *            the data set on which to execute the clustering.
     * @return the different clusters obtained by this clustering algorithm.
     *         Each cluster is represented as a separate data set.
     */
    public Dataset[] cluster(Dataset data);

}
