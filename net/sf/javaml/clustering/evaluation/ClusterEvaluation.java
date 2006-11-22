    /**
 * ClusterEvaluation.java, 16-nov-2006
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
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;

/**
 * This interface provides a frame for all measure that can be used to evaluate
 * the quality of a clusterer.
 * 
 * @author Thomas Abeel
 * 
 */
public interface ClusterEvaluation {

    /**
     * Returns the score the current clusterer obtains on the dataset.
     * 
     * @param c
     *            the clusterer (the call to buildClusterer should be made)
     * @param d
     *            the dataset to test the clusterer on.
     * @return the score the clusterer obtains on this particular dataset
     */
    public double score(Clusterer c, Dataset d);

    /**
     * Compares the two scores according to the criterion in the implementation.
     * Some score should be maxed, others should be minimized. This method
     * returns true if the second score is 'better' than the first score.
     * 
     * @param score1
     *            the first score
     * @param score2
     *            the second score
     * @return true if the second score is better than the first, false in all
     *         other cases
     */
    public boolean compareScore(double score1, double score2);
}
