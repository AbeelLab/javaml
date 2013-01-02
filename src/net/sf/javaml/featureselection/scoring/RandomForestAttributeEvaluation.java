/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection.scoring;

import java.util.Random;

import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.FeatureScoring;
import net.sf.javaml.tools.DatasetTools;
import net.sf.javaml.utils.ArrayUtils;
import net.sf.javaml.utils.MathUtils;
import be.abeel.io.Copier;

/**
 * Random Forest based attribute evaluation.
 * 
 * Procedure: make Random Forest, use out-of-bag (oob) samples to calculate
 * error estimate. For each attribute, perturb the values of this attribute in
 * the oob samples and recalculate the error estimate for the perturbed samples.
 * The difference between the error estimate of the original oob error estimate
 * and the oob error estimate of the perturbed samples is a measure for the
 * importance of the perturbed attribute.
 * 
 * We can use the differences in importance to rank the features or use the
 * differences to give an importance measure to all attributes
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RandomForestAttributeEvaluation implements FeatureScoring {

    private int numTrees;

    private Object positiveClass;

    private int k;

    private Random rg;

    public void setK(int k) {
        this.k = k;
    }

    public void setPerturbations(int p) {
        this.numPerturbations = p;
    }

    public RandomForestAttributeEvaluation(int numTrees, Object positiveClass, Random rg) {
        this.rg = rg;
        this.numTrees = numTrees;
        this.positiveClass = positiveClass;
        this.k = 5;
        this.numPerturbations = 1;
    }

    /*
     * Number of times each attribute is perturbed
     */
    private int numPerturbations;

    public void build(Dataset data) {
        Copier<Instance> instCopier = new Copier<Instance>();
        int tp = 0, fp = 0, fn = 0, tn = 0;
        int[][] tpR = new int[data.noAttributes()][numPerturbations];
        int[][] fpR = new int[data.noAttributes()][numPerturbations];
        int[][] tnR = new int[data.noAttributes()][numPerturbations];
        int[][] fnR = new int[data.noAttributes()][numPerturbations];

        for (int k = 0; k < data.noAttributes(); k++) {
            tpR[k] = new int[numPerturbations];
            fpR[k] = new int[numPerturbations];
            tnR[k] = new int[numPerturbations];
            fnR[k] = new int[numPerturbations];
        }

        for (int i = 0; i < numTrees; i++) {

            /*
             * Train a tree and calculate the oob error for the unperturbed oob
             * samples.
             */
            RandomTree tree = new RandomTree(k, rg);

            Dataset sample = DatasetTools.bootstrap(data, data.size(), rg);
            tree.buildClassifier(sample);

            Dataset outOfBag = new DefaultDataset();
            outOfBag.addAll(data);
            outOfBag.removeAll(sample);

            for (Instance inst : outOfBag) {
                Object predClass = tree.classify(inst);
                if (predClass.equals(positiveClass)) {
                    if (inst.classValue().equals(positiveClass))
                        tp++;
                    else
                        fp++;
                } else {
                    if (inst.classValue().equals(positiveClass))
                        fn++;
                    else
                        tn++;
                }

            }
            /*
             * For each attribute we run the perturbation process.
             */
            for (int k = 0; k < data.noAttributes(); k++) {
                /*
                 * While one perturbation of the attribute would give a first
                 * idea of the importance, more runs for the same attribute
                 * would give a more accurate image of the importance.
                 */
                for (int j = 0; j < numPerturbations; j++) {

                    Dataset perturbed = new DefaultDataset();
                    for (Instance inst : outOfBag) {
                        Instance per = instCopier.copy(inst);
                        per.put(k, Math.random());
                        perturbed.add(per);

                    }
                    for (Instance inst : perturbed) {
                        Object predClass = tree.classify(inst);
                        if (predClass.equals(positiveClass)) {
                            if (inst.classValue().equals(positiveClass))
                                tpR[k][j]++;
                            else
                                fpR[k][j]++;
                        } else {
                            if (inst.classValue().equals(positiveClass))
                                fnR[k][j]++;
                            else
                                tnR[k][j]++;
                        }

                    }

                }

            }

        }
        double originalF = new PerformanceMeasure(tp, tn, fp, fn).getFMeasure();
        importance = new double[data.noAttributes()];
        for (int k = 0; k < data.noAttributes(); k++) {
            double[] g = new double[numPerturbations];
            for (int i = 0; i < numPerturbations; i++) {
                g[i] = new PerformanceMeasure(tpR[k][i], tnR[k][i], fpR[k][i], tnR[k][i]).getFMeasure();
            }
            double avg = MathUtils.arithmicMean(g);
            importance[k] = originalF - avg;

        }
        /*
         * Translate above zero
         */
        ArrayUtils.add(importance, -ArrayUtils.min(importance));
        /*
         * Scale between 0 and 1
         */
        ArrayUtils.normalize(importance, ArrayUtils.max(importance));
    }

    private double[] importance;

    public double score(int attribute) {
        return importance[attribute];
    }

    @Override
    public int noAttributes() {
        return importance.length;
    }

}
