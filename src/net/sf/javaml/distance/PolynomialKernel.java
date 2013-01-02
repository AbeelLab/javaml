/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

public class PolynomialKernel implements DistanceMeasure {

    /**
     * 
     */
    private static final long serialVersionUID = 113839833688979121L;
    private double exponent=1;
    
    public PolynomialKernel(double exponent){
        this.exponent=exponent;
    }
    
    public double measure(Instance i, Instance j) {
        double result;
        result = dotProd(i, j);
        if (exponent != 1.0) {
            result = Math.pow(result, exponent);
        }
        return result;
    }

    /**
     * Calculates a dot product between two instances
     */
    protected final double dotProd(Instance inst1, Instance inst2) {

        double result = 0;

        for (int i = 0; i < inst1.noAttributes(); i++) {
            result += inst1.value(i) * inst2.value(i);
        }
        return result;
    }

    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public double getMinValue() {
      throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public double getMaxValue() {
      throw new UnsupportedOperationException("Not implemented");
    }
   

}
