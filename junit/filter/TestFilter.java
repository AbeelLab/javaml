/**
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.GainRatio;
import net.sf.javaml.featureselection.RELIEF;
import net.sf.javaml.featureselection.SymmetricalUncertainty;
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
        System.out.println(su.score(0) + "\t" + relief.score(0) + "\t"
                + gr.score(0));
        System.out.println(su.score(1) + "\t" + relief.score(1) + "\t"
                + gr.score(1));
        System.out.println(su.score(2) + "\t" + relief.score(2) + "\t"
                + gr.score(2));

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
            System.out.println(su.score(0) + "\t" + relief.score(0) + "\t"
                    + gr.score(0));
            System.out.println(su.score(1) + "\t" + relief.score(1) + "\t"
                    + gr.score(1));
            System.out.println(su.score(2) + "\t" + relief.score(2) + "\t"
                    + gr.score(2));
            System.out.println(su.score(3) + "\t" + relief.score(3) + "\t"
                    + gr.score(3));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double vary(double i, double j) {
        return i + j * Math.random();
    }
}
