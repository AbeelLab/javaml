/**
 * BlastSimilarity.java
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
package net.sf.javaml.tools.bioinformatics;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractSimilarity;

/**
 * This class provides a similarity measure based on the distance functions from a BlastResult.
 *
 * @author Thomas Abeel
 *
 */
public class BlastSimilarity extends AbstractSimilarity {

    private BlastResult data;
    public BlastSimilarity(BlastResult data){
        this.data=data;
    }
    public double calculateDistance(Instance i, Instance j) {
        return 1-data.calculateDistance(i, j);
    }

    public double getMaximumDistance(Dataset data) {
        return 1-this.data.getMaximumDistance(null);
    }

    public double getMinimumDistance(Dataset data) {
        return 1-this.data.getMinimumDistance(null);
    }

}
