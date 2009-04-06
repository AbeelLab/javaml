/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;


import net.sf.javaml.core.Instance;
import Jama.Matrix;

public class MahalanobisDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -5844297515283628612L;

    public double measure(Instance i, Instance j) {
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

   

}
