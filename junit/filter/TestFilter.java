/**
 * TestGainRatio.java
 *
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.eval.GainRatio;
import net.sf.javaml.filter.eval.RELIEF;
import net.sf.javaml.filter.eval.SymmetricalUncertainty;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestFilter {
    @Test
    public void testCompareSUandRELIEF() {
        // double[] vals = { 1, 2, 3, 4, 5, 6 };
        System.out.println("==");
        System.out.println("Synthetic data set");
        Dataset data = new DefaultDataset();
        for (int i = 0; i < 100; i++) {
            double[] vals = { vary(5 * (i / 50), 1), vary(1, 0), Math.random() };
            Instance inst = new DenseInstance(vals, i / 50);
            data.add(inst);

        }
        // NOrma dnm=new DatasetNormalizeMidrange(0.5,1);
        // dnm.build(data);
        // Dataset test=dnm.filterDataset(data);

        // System.out.println(data);
        SymmetricalUncertainty su = new SymmetricalUncertainty();
        RELIEF relief = new RELIEF();
        // ga.setNumNeigbors(4);
        GainRatio gr = new GainRatio();
        su.build(data);
        relief.build(data);
        gr.build(data);
        System.out.println("SU\tRELIEF\tGR");
        System.out.println(su.evaluateAttribute(0) + "\t" + relief.evaluateAttribute(0) + "\t"
                + gr.evaluateAttribute(0));
        System.out.println(su.evaluateAttribute(1) + "\t" + relief.evaluateAttribute(1) + "\t"
                + gr.evaluateAttribute(1));
        System.out.println(su.evaluateAttribute(2) + "\t" + relief.evaluateAttribute(2) + "\t"
                + gr.evaluateAttribute(2));

    }

    @Test
    public void testSymmetricalUncertaintyAttributeEvaluationReal() {
        try {
            System.out.println("==");
            System.out.println("Iris data set");
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            SymmetricalUncertainty su = new SymmetricalUncertainty();
            RELIEF relief = new RELIEF();
            GainRatio gr = new GainRatio();
            // ga.setNumNeigbors(4);

            su.build(data);
            relief.build(data);
            gr.build(data);
            System.out.println("SU\tRELIEF\tGR");
            System.out.println(su.evaluateAttribute(0) + "\t" + relief.evaluateAttribute(0) + "\t"
                    + gr.evaluateAttribute(0));
            System.out.println(su.evaluateAttribute(1) + "\t" + relief.evaluateAttribute(1) + "\t"
                    + gr.evaluateAttribute(1));
            System.out.println(su.evaluateAttribute(2) + "\t" + relief.evaluateAttribute(2) + "\t"
                    + gr.evaluateAttribute(2));
            System.out.println(su.evaluateAttribute(3) + "\t" + relief.evaluateAttribute(3) + "\t"
                    + gr.evaluateAttribute(3));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double vary(double i, double j) {
        return i + j * Math.random();
    }
}
