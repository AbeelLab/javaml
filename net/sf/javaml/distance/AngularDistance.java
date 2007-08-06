/**
 * AngularDistance.java
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

public class AngularDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = 4669057353119949494L;

    public double calculateDistance(Instance i, Instance j) {
        double theta1 = calcTheta(i);
        double phi1 = calcPhi(i);
        double theta2 = calcTheta(j);
        double phi2 = calcPhi(j);
        double delTheta = Math.abs(theta1 - theta2);
        double delPhi = Math.abs(phi1 - phi2);
        if (delPhi > Math.PI)
            delPhi = 2.0 * Math.PI - delPhi;
        double dist = delTheta * delTheta + delPhi * delPhi;
        if (dist != 0.0)
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

    private static double calcTheta(Instance pos) {
        double theta = 0.0;
        double r = Math.sqrt(pos.getValue(0) * pos.getValue(0) + pos.getValue(1) * pos.getValue(1));
        theta = Math.atan2(r, pos.getValue(2));
        return theta;
    }

    private static double calcPhi(Instance pos) {
        double phi = 0.0;
        phi = Math.atan2(pos.getValue(1), pos.getValue(0));
        if (phi < 0.0)
            phi += 2.0 * Math.PI;
        return phi;
    }
}
