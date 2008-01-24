/**
 * RandomForestAttributeEvaluation.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.attribute.eval;

import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.utils.ArrayUtils;
import net.sf.javaml.utils.MathUtils;

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
 * We can use the differences in importance to rank the features or use the differences to
 * give an importance measure to all attributes
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RandomForestAttributeEvaluation implements IAttributeEvaluation {

    private int numTrees;

    private int positiveClass;

    public RandomForestAttributeEvaluation(int numTrees, int positiveClassIndex) {
        this.numTrees = numTrees;
        this.positiveClass = positiveClassIndex;
    }

    /*
     * Number of times each attribute is perturbed
     */
    private int randomFolds = 10;

    public void build(Dataset data) {

        int tp = 0, fp = 0, fn = 0, tn = 0;
        int[][] tpR = new int[data.numAttributes()][randomFolds];
        int[][] fpR = new int[data.numAttributes()][randomFolds];
        int[][] tnR = new int[data.numAttributes()][randomFolds];
        int[][] fnR = new int[data.numAttributes()][randomFolds];

        for (int k = 0; k < data.numAttributes(); k++) {
            tpR[k] = new int[randomFolds];
            fpR[k] = new int[randomFolds];
            tnR[k] = new int[randomFolds];
            fnR[k] = new int[randomFolds];
        }

        for (int i = 0; i < numTrees; i++) {

            /*
             * Train a tree and calculate the oob error for the unperturbed oob
             * samples.
             */
            RandomTree tree = new RandomTree();
            Dataset sample = DatasetTools.randomSample(data, data.size());
            tree.buildClassifier(sample);

            Dataset outOfBag = data.copy();
            outOfBag.removeAll(sample);

            for (Instance inst : outOfBag) {
                int predClass = tree.classifyInstance(inst);
                if (predClass == positiveClass) {
                    if (inst.classValue() == positiveClass)
                        tp++;
                    else
                        fp++;
                } else {
                    if (inst.classValue() == positiveClass)
                        fn++;
                    else
                        tn++;
                }

            }
            /*
             * For each attribute we run the perturbation process.
             */
            for (int k = 0; k < data.numAttributes(); k++) {
                /*
                 * While one perturbation of the attribute would give a first
                 * idea of the importance, more runs for the same attribute
                 * would give a more accurate image of the importance.
                 */
                for (int j = 0; j < randomFolds; j++) {

                    Dataset perturbed = new SimpleDataset();
                    for (Instance inst : outOfBag) {
                        perturbed.add(InstanceTools.perturb(inst, k));

                    }
                    for (Instance inst : perturbed) {
                        int predClass = tree.classifyInstance(inst);
                        if (predClass == positiveClass) {
                            if (inst.classValue() == positiveClass)
                                tpR[k][j]++;
                            else
                                fpR[k][j]++;
                        } else {
                            if (inst.classValue() == positiveClass)
                                fnR[k][j]++;
                            else
                                tnR[k][j]++;
                        }

                    }

                }

            }

        }
        double originalF = new PerformanceMeasure(tp, tn, fp, fn).getFMeasure();
        importance = new double[data.numAttributes()];
        for (int k = 0; k < data.numAttributes(); k++) {
            double[] g = new double[randomFolds];
            for (int i = 0; i < randomFolds; i++) {
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

    public double evaluateAttribute(int attribute) {
        return importance[attribute];
    }

}
