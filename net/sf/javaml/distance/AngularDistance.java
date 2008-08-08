/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

public class AngularDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = 4669057353119949494L;

    public double measure(Instance i, Instance j) {
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

    private static double calcTheta(Instance pos) {
        double theta = 0.0;
        double r = Math.sqrt(pos.value(0) * pos.value(0) + pos.value(1) * pos.value(1));
        theta = Math.atan2(r, pos.value(2));
        return theta;
    }

    private static double calcPhi(Instance pos) {
        double phi = 0.0;
        phi = Math.atan2(pos.value(1), pos.value(0));
        if (phi < 0.0)
            phi += 2.0 * Math.PI;
        return phi;
    }
}
