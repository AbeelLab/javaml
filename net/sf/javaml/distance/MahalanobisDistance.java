/**
 * MahalanobisDistance.java
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

import gov.nist.math.jama.Matrix;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class MahalanobisDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -5844297515283628612L;

    public double calculateDistance(Instance i, Instance j) {
        //XXX optimize
        double[][] del = new double[3][1];
        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 1; n++) {
                del[m][n] = i.value(m) - j.value(m);
            }
        }
        Matrix M1 = new Matrix(del);
        Matrix M2 = M1.transpose();

        double[][] covar = new double[3][3];
        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++) {
                covar[m][n] += (i.value(m) - j.value(m)) * (i.value(n) - j.value(n));
            }
        }
        Matrix cov = new Matrix(covar);
        Matrix covInv = cov.inverse();
        Matrix temp1 = M2.times(covInv);
        Matrix temp2 = temp1.times(M1);
        double dist = temp2.trace();
        if (dist > 0.)
            dist = Math.sqrt(dist);
        return dist;
    }

    public double getMaximumDistance(Dataset data) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public double getMinimumDistance(Dataset data) {
        // TODO implement
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
