/**
 * FeatureSelector.java, 10-nov-2006
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
package net.sf.javaml.featureselection;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public interface FeatureSelector {

    /**
     * Build a feature selector using the provide dataset.
     * 
     * @param data
     *            the dataset to build the feature selector
     */
    public void buildFeatureSelector(Dataset data);

    /**
     * Return the number of feature that have been selected during the last call
     * to build.
     */
    public int getNumberSelectedFeatures();

    /**
     * Create a new instance that only represents the features that have been
     * selected in the last build call.
     * 
     * @param instance
     * @return
     */
    public Instance selectFeatures(Instance instance);

    /**
     * Reduce the dimensionality of a whole dataset using the feature selector
     * that has been build during the last build call.
     * 
     * @param data
     *            the dataset to reduce
     * @return the dataset with reduced dimensionality
     */
    public Dataset selectFeatures(Dataset data);

}
