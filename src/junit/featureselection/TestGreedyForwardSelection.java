/**
 * %SVN.HEADER%
 */
package junit.featureselection;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestGreedyForwardSelection {
    @Test
    public void testGreedyForwardSelectionSynthetic() {

        Dataset data = new DefaultDataset();
        for (int i = 0; i < 100; i++) {
            double[] vals = { i / 50,vary(1, 0), Math.random(), vary(5 * (i / 50), 1) };
            Instance inst = new DenseInstance(vals, i / 50);
            data.add(inst);

        }
        GreedyForwardSelection ga = new GreedyForwardSelection(2, new PearsonCorrelationCoefficient());

        ga.build(data);
        Assert.assertTrue(ga.selectedAttributes().contains(0));
        Assert.assertTrue(ga.selectedAttributes().contains(3));
        System.out.println(ga.selectedAttributes());

    }

    @Test
    public void testGreedyForwardSelectionReal() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            GreedyForwardSelection ga = new GreedyForwardSelection(1, new PearsonCorrelationCoefficient());
            ga.build(data);

            System.out.println(ga.selectedAttributes());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    private double vary(double i, double j) {
        return i + j * Math.random();
    }
}
