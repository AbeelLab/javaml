/**
 * %SVN.HEADER%
 */
package junit.distance;

import junit.framework.Assert;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;

import org.junit.Test;

public class TestPearsonCorrelation {

    @Test
    public void testPearsonCorrelation2() {
        double[] val1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1 };
        double[] val2 = new double[] { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
        DistanceMeasure dm = new PearsonCorrelationCoefficient();
        double d = dm.measure(new DenseInstance(val1), new DenseInstance(val2));
        Assert.assertTrue(Double.isNaN(d));

    }

    @Test
    public void testPearsonCorrelation() {
        double[] val1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1 };
        double[] val2 = new double[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        DistanceMeasure dm = new PearsonCorrelationCoefficient();
        double d = dm.measure(new DenseInstance(val1), new DenseInstance(val2));
        Assert.assertTrue(Double.isNaN(d));

    }

    @Test
    public void testDavidMLaneExample() {
        double[] val1 = new double[] { 1, 2, 3 };
        double[] val2 = new double[] { 2, 5, 6 };
        DistanceMeasure dm = new PearsonCorrelationCoefficient();
        double d = dm.measure(new DenseInstance(val1), new DenseInstance(val2));
        Assert.assertEquals(0.9608, d, 0.0001);
        System.out.println(d);

    }

    @Test
    public void testWEExample() {
        double[] val1 = new double[] { 56, 56, 65, 65, 50, 25, 87, 44, 35 };
        double[] val2 = new double[] { 87, 91, 85, 91, 75, 28, 122, 66, 58 };
        DistanceMeasure dm = new PearsonCorrelationCoefficient();
        double d = dm.measure(new DenseInstance(val1), new DenseInstance(val2));
        Assert.assertEquals(0.966, d, 0.001);
        System.out.println(d);

    }

}
