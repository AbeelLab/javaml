/**
 * PearsonCorrelationCoefficient.java
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
package net.sf.javaml.distance;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Calculates the Pearson Correlation Coeffient between two vectors.
 * 
 * The returned value lies in the interval [-1,1]. A value of 1 shows that a
 * linear equation describes the relationship perfectly and positively, with all
 * data points lying on the same line and with Y increasing with X. A score of
 * ?1 shows that all data points lie on a single line but that Y increases as X
 * decreases. A value of 0 shows that a linear model is inappropriate – that
 * there is no linear relationship between the variables.
 * 
 * http://davidmlane.com/hyperstat/A56626.html
 * http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class PearsonCorrelationCoefficient extends AbstractSimilarity {
    /**
     * 
     */
    private static final long serialVersionUID = -5805322724874919246L;

    /**
     * XXX DOC
     */
    public double calculateDistance(Instance a, Instance b) {
        if (a.size() != b.size())
            throw new RuntimeException("Both instances should have the same length");
        double xy = 0, x = 0, x2 = 0, y = 0, y2 = 0;
        for (int i = 0; i < a.size(); i++) {
            xy += a.getValue(i) * b.getValue(i);
            x += a.getValue(i);
            y += b.getValue(i);
            x2 += a.getValue(i) * a.getValue(i);
            y2 += b.getValue(i) * b.getValue(i);
        }
        int n = a.size();
        return (xy - (x * y) / n) / Math.sqrt((x2 - (x * x) / n) * (y2 - (y * y) / n));
    }

    /**
     * XXX DOC
     */
    public double getMaximumDistance(Dataset data) {
        // TODO implement
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * XXX DOC
     */
    public double getMinimumDistance(Dataset data) {
        // TODO implement
        throw new UnsupportedOperationException("Method not implemented");
    }

}
