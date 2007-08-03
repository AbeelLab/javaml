/**
 * Classifier.java, 10-nov-2006
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
package net.sf.javaml.classification;

import java.io.Serializable;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public interface Classifier extends Serializable {
    /**
     * Create a classifier from the given dataset.
     * 
     * @param data
     *            the dataset to be used to create the clusterer
     */
    public void buildClassifier(Dataset data);

    /**
     * Classify the instance according to this classifier.
     * 
     * @param instance
     *            the instance to be classified
     * @return the index of the class to which this instance belongs
     */
    public int classifyInstance(Instance instance);

    /**
     * Generate the membership distribution for this instance using this
     * classifier.
     * 
     * @param instance
     *            the instance to be classified
     * @return an array with membership degress for all the various classes in
     *         the dataset
     */
    public double[] distributionForInstance(Instance instance);
}
