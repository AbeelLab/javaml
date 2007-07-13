/**
 * Dataset.java
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

package net.sf.javaml.core;

public interface Dataset extends Iterable<Instance> {

    /**
     * Add an instance to this dataset. The compatibility of the new item with
     * the items in the dataset should be checked by the implementation.
     * Incompatible items should not be added to the dataset.
     * 
     * @param i
     *            the instance to be added
     * @return true if the instance was added, otherwise false
     */
    public boolean addInstance(Instance i);

    /**
     * Get the instance with a certain index.
     * 
     * @param index
     *            the index of the instance you want to retrieve.
     * @return
     */
    public Instance getInstance(int index);

    /**
     * Get the index of a certain instance.
     * 
     * @param inst
     *            the instance you want the index of
     * @return
     */
    public int getIndex(Instance inst);

    /**
     * Returns the size of the dataset
     * 
     * @return the number of instances in the dataset.
     */
    public int size();

    /**
     * Get the 'minimum instance' of this dataset. This is a virtual instance
     * with for each index the lowest value found in the dataset.
     * 
     * If this method is not supported by the implementation, the method should
     * return null.
     * 
     * @return an instance with for every index it's lowest value, null if the
     *         dataset is empty
     */
    public Instance getMinimumInstance();

    /**
     * Get the 'maximum instance' of this dataset. This is a virtual instance
     * with for each index the highest value found in the dataset.
     * 
     * If this method is not supported by the implementation, the method should
     * return null.
     * 
     * @return an instance with for every index it's highest value, null if the
     *         dataset is empty
     */
    public Instance getMaximumInstance();

    /**
     * Returns the number of different classes in the dataset. 
     * @return
     */
    public int getNumClasses();
}
