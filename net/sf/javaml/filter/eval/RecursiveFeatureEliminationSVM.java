/**
 * RecursiveFeatureEliminationSVM.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.eval;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import be.abeel.util.Copier;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.filter.RemoveAttributes;
import net.sf.javaml.utils.ArrayUtils;
import external.libsvm.SelfOptimizingLinearLibSVM;

/**
 * Implements the SVM_RFE algorithm.
 * 
 * Starting with the full feature set, attributes are ranked according to their
 * weight with a linear SVM. Subsequently, the 20 % worst features are
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

    private Object positiveClass;

    private int folds;

    // public RecursiveFeatureEliminationSVM() {
    // }

    private double removePercentage = 0.20;

    private Random rg;

    public RecursiveFeatureEliminationSVM(int folds, Object positiveClass, double removePercentage, Random rg) {
        this.folds = folds;
        this.positiveClass = positiveClass;
        this.removePercentage = removePercentage;
        this.rg = rg;

    }

    public void build(Dataset data) {
        int[] ordering = new int[data.noAttributes()];
        SelfOptimizingLinearLibSVM svm = new SelfOptimizingLinearLibSVM(positiveClass, rg);
        svm.setFolds(folds);
        // svm.setPositiveClass(positiveClass);
        // bitmap of removed attributes
        boolean[] removedAttributes = new boolean[data.noAttributes()];
        // number of remove attributes, is equal to the number of trues in the
        // above bitmap
        int removed = 0;

        while (data.noAttributes() > 1) {
            Dataset training = new Copier<Dataset>().copy(data);
            svm.buildClassifier(training);
            double[] weights = svm.getWeights();
            // use absolute values of the weights
            ArrayUtils.abs(weights);
            // System.out.println("weights=" + Arrays.toString(weights));
            // order weights
            int[] order = ArrayUtils.sort(weights);
            // determine the number of attributes to prune 10%, round up
            int numRemove = (int) (order.length * removePercentage + 1);
            if (numRemove > order.length)
                numRemove = order.length;
            // System.out.println("remove=" + numRemove);
            Set<Integer> toRemove = new HashSet<Integer>();// new
            // int[numRemove];
            int[] trueIndices = new int[numRemove];

            for (int i = 0; i < numRemove; i++) {

                // System.out.println("Remove=" + order[i] + "/" +
                // data.noAttributes());
                toRemove.add(order[i]);
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
            filter.filter(data);

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
