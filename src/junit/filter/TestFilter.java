/**
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.ranking.RecursiveFeatureEliminationSVM;
import net.sf.javaml.featureselection.scoring.GainRatio;
import net.sf.javaml.featureselection.scoring.KullbackLeiblerDivergence;
import net.sf.javaml.featureselection.scoring.RELIEF;
import net.sf.javaml.featureselection.scoring.SymmetricalUncertainty;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestFilter {
   

    @Test
    public void testComparisonSynthetic() {

        System.out.println("==");
        System.out.println("Synthetic data set");
        Dataset data = new DefaultDataset();
        for (int i = 0; i < 100; i++) {
            double[] vals = { vary(5 * (i / 50), 1), vary(1, 0), Math.random() };
            Instance inst = new DenseInstance(vals, i / 50);
            data.add(inst);

        }

        SymmetricalUncertainty su = new SymmetricalUncertainty();
        RELIEF relief = new RELIEF();
        GainRatio gr = new GainRatio();
        KullbackLeiblerDivergence kl = new KullbackLeiblerDivergence();
        RecursiveFeatureEliminationSVM rfe = new RecursiveFeatureEliminationSVM(0.20);
        rfe.build(data.copy());
        kl.build(data.copy());
        su.build(data);
        relief.build(data);
        gr.build(data);

        System.out.println("SU\tRELIEF\tGR\tKL\tSVMRFE");
        System.out.println(su.score(0) + "\t" + relief.score(0) + "\t" + gr.score(0) + "\t" + kl.score(0) + "\t"
                + rfe.rank(0));
        System.out.println(su.score(1) + "\t" + relief.score(1) + "\t" + gr.score(1) + "\t" + kl.score(1) + "\t"
                + rfe.rank(1));
        System.out.println(su.score(2) + "\t" + relief.score(2) + "\t" + gr.score(2) + "\t" + kl.score(2) + "\t"
                + rfe.rank(2));

    }

    @Test
    public void testComparisonReal() {
        try {
            System.out.println("==");
            System.out.println("Iris data set");
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            Dataset data2 = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            SymmetricalUncertainty su = new SymmetricalUncertainty();
            RELIEF relief = new RELIEF();
            GainRatio gr = new GainRatio();
            // ga.setNumNeigbors(4);
            KullbackLeiblerDivergence kl = new KullbackLeiblerDivergence();
            RecursiveFeatureEliminationSVM rfe = new RecursiveFeatureEliminationSVM(0.20);
            rfe.build(data.copy());
            su.build(data);
            relief.build(data);
            gr.build(data);
            kl.build(data2);
            System.out.println("SU\tRELIEF\tGR\tKL\t");
            System.out.println(su.score(0) + "\t" + relief.score(0) + "\t" + gr.score(0) + "\t" + kl.score(0)+ "\t"
                    + rfe.rank(0));
            System.out.println(su.score(1) + "\t" + relief.score(1) + "\t" + gr.score(1) + "\t" + kl.score(1)+ "\t"
                    + rfe.rank(1));
            System.out.println(su.score(2) + "\t" + relief.score(2) + "\t" + gr.score(2) + "\t" + kl.score(2)+ "\t"
                    + rfe.rank(2));
            System.out.println(su.score(3) + "\t" + relief.score(3) + "\t" + gr.score(3) + "\t" + kl.score(3)+ "\t"
                    + rfe.rank(3));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double vary(double i, double j) {
        return i + j * Math.random();
    }
//    @Test
//    public void testSparseSU() throws IOException {
//        Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, "\t", ":");
//
//        SymmetricalUncertainty su = new SymmetricalUncertainty();
//
//        su.build(data);
//
//        System.out.println("SU\tRELIEF\tGR");
//        System.out.println(su.score(0));
//        System.out.println(su.score(1));
//        System.out.println(su.score(2));
//
//    }

//    @Test
//    public void testSparseGR() throws IOException {
//        Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, "\t", ":");
//
//        GainRatio gr = new GainRatio();
//
//        gr.build(data);
//        System.out.println("SU\tRELIEF\tGR");
//        System.out.println(gr.score(0));
//        System.out.println(gr.score(1));
//        System.out.println(gr.score(2));
//
//    }

    @Test
    public void testSparseRELIEF() throws IOException {
        Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, "\t", ":");

        RELIEF relief = new RELIEF();

        relief.build(data);

        System.out.println("SU\tRELIEF\tGR");
        System.out.println(relief.score(0));
        System.out.println(relief.score(1));
        System.out.println(relief.score(2));

    }

    @Test
    public void testSparseKL() throws IOException {
        Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, "\t", ":");

        KullbackLeiblerDivergence kl = new KullbackLeiblerDivergence();
        kl.build(data.copy());

        System.out.println("SU\tRELIEF\tGR");
        System.out.println(kl.score(0));
        System.out.println(kl.score(1));
        System.out.println(kl.score(2));

    }
}
