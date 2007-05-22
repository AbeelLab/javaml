/**
 * Filter.java
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
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * A filter is procedure you can apply to a dataset which will return an altered
 * version of the dataset. Some filters may normalize the data, others may mask
 * missing values, etc.
 * 
 * @author Thomas Abeel
 * 
 */
public interface Filter {

    /**
     * This method will apply the filter to all the Instances in the dataset.
     * Some filters may require to first call this method before you can filter
     * single instances with the <code>filterInstance</code> method.
     * 
     * @param data
     *            the dataset to be filtered
     * @return the filtered dataset
     */
    public Dataset filterDataset(Dataset data);

    /**
     * This method will filter an instance according to the rules of this
     * filter. Some filters may require to first call <code>filterDataset</code>
     * before you can use this method. This is filter dependend.
     * 
     * @param instance
     *            the instance to be filtered
     * @return the filtered instance
     */
    public Instance filterInstance(Instance instance);
    
    public Instance unfilterInstance(Instance instance);
}
