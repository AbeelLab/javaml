/**
 * Dataset.java, 11-okt-06
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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

package net.sf.javaml.core;

public interface Dataset {

    /**
     * Add an instance to this dataset.
     * 
     * @param i
     *            the instance to be added
     */
    public void addInstance(Instance i);

    /**
     * Check whether all instances in this dataset are compatible with each
     * other. This method will return true is all instances are compatible with
     * each other, otherwise it will return false.
     * 
     * @return true if all instances are compatible, false if not all instances
     *         are compatible.
     */
    public boolean checkCompatibility();

    /**
     * Return the index associated with an Instance.
     * @param i the instance where you want to know the index for.
     * @return
     */
    public int getIndex(Instance i);
    /**
     * Get the instance with a certain index.
     * @param index the index of the instance you want to retrieve.
     * @return
     */
    public Instance getInstance(int index);
    /**
     * Remove the instance that is given as parameter from the dataset.
     * @param i the instance to be removed from the dataset.
     */
    public void removeInstance(Instance i);
    /**
     * Return the instance with the given index.
     * @param index the index of the instance to be deleted
     */
    public void removeInstance(int index);
    /**
     * Remove all instances from the dataset.
     *
     */
    public void clear();
    /**
     * Returns the size of the dataset
     * @return the number of instances in the dataset.
     */
    public int size();
}
