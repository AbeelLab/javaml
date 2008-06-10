/**
 * RecursiveFeatureEliminationSVM.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.eval;

import java.util.Arrays;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.filter.RemoveAttributes;
import net.sf.javaml.utils.ArrayUtils;
import external.libsvm.SelfOptimizingLinearLibSVM;

/**
 * Implements the SVM_RFE algorithm.
 * 
 * Starting with the full feature set, attributes are ranked according to their
 * weight with a linear SVM. Subsequently, the 10 % worst features are
 * eliminated, the SVM is retrained (C is again optimized), and the process is
 * repeated until only one feature is retained. The result is a feature ranking.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RecursiveFeatureEliminationSVM implements IAttributeRanking {

    private int[] ranking;

    private int positiveClass;

    private int folds;

    // public RecursiveFeatureEliminationSVM() {
    // }

    private double removePercentage = 0.20;

    public RecursiveFeatureEliminationSVM(int folds, int positiveClass, double removePercentage) {
        this.folds = folds;
        this.positiveClass = positiveClass;
        this.removePercentage = removePercentage;

    }

    public void build(Dataset data) {
        int[] ordering = new int[data.noAttributes()];
        SelfOptimizingLinearLibSVM svm = new SelfOptimizingLinearLibSVM();
        svm.setFolds(folds);
        svm.setPositiveClass(positiveClass);
        // bitmap of removed attributes
        boolean[] removedAttributes = new boolean[data.noAttributes()];
        // number of remove attributes, is equal to the number of trues in the
        // above bitmap
        int removed = 0;

        while (data.noAttributes() > 1) {
            svm.buildClassifier(data);
            double[] weights = svm.getWeights();
            // use absolute values of the weights
            ArrayUtils.abs(weights);
            // System.out.println("weights=" + Arrays.toString(weights));
            // order weights
            int[] order = ArrayUtils.sort(weights);
            // determine the number of attributes to prune 10% round up
            int numRemove = (int) (order.length * removePercentage + 1);
            if (numRemove > order.length)
                numRemove = order.length;
            // System.out.println("remove=" + numRemove);
            int[] toRemove = new int[numRemove];
            int[] trueIndices = new int[numRemove];

            for (int i = 0; i < numRemove; i++) {

                // System.out.println("Remove=" + order[i] + "/" +
                // data.noAttributes());
                toRemove[i] = order[i];
                // int trueIndex =
                trueIndices[i] = getTrueIndex(order[i], removedAttributes);
                // System.out.println("true remove=" + trueIndex);
                ordering[ordering.length - removed - 1] = trueIndices[i];
                removed++;
            }
            // This needs to be done afterwards, otherwise the getTrueIndex
            // method will fail.
            for (int i = 0; i < numRemove; i++) {
                // if (removedAttributes[trueIndices[i]])
                // System.err.println("WRONG = " + trueIndices);
                removedAttributes[trueIndices[i]] = true;

            }

            // System.out.println(Arrays.toString(toRemove));
            // System.out.println(Arrays.toString(removedAttributes));
            // actually remove the attributes
            RemoveAttributes filter = new RemoveAttributes(toRemove);
            data = filter.filterDataset(data);

        }
        int index = 0;
        if (data.noAttributes() == 1) {
            for (int i = 0; i < removedAttributes.length; i++) {
                if (!removedAttributes[i])
                    index = i;
            }
            ordering[0] = index;
        }
        ranking = new int[ordering.length];
        for (int i = 0; i < ranking.length; i++)
            ranking[ordering[i]] = i;
        // System.out.println("Ranking: " + Arrays.toString(ranking));

    }

    // public RecursiveFeatureEliminationSVM() {
    // }
    //
    // @Test
    // public void testTrueIndex() {
    // boolean[] test = { true, true, false, false, true, false, true, true,
    // false, false, false, true, false };
    // // System.out.println("Should be 9");
    // System.out.println(getTrueIndex(0, test));
    // System.out.println(getTrueIndex(1, test));
    // System.out.println(getTrueIndex(2, test));
    // System.out.println(getTrueIndex(3, test));
    // System.out.println(getTrueIndex(4, test));
    // System.out.println(getTrueIndex(5, test));
    // System.out.println(getTrueIndex(6, test));
    //
    // }

    private int getTrueIndex(int i, boolean[] removedAttributes) {
        // System.out.println("RA.length = " + removedAttributes.length);
        int index = 0;
        while (i >= 0) {

            if (!removedAttributes[index])
                i--;
            index++;

        }
        return index - 1;
    }

    public int getRank(int attIndex) {
        return ranking[attIndex];
    }
}
