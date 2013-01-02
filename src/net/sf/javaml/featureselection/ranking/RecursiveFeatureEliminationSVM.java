/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection.ranking;

import java.util.HashSet;
import java.util.Set;

import libsvm.LibSVM;
import libsvm.SelfOptimizingLinearLibSVM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.FeatureRanking;
import net.sf.javaml.filter.RemoveAttributes;
import net.sf.javaml.utils.ArrayUtils;


/**
 * Implements the recursive feature elimination procedure for linear support
 * vector machines.
 * 
 * Starting with the full feature set, attributes are ranked according to the
 * weights they get in a linear SVM. Subsequently, a percentage of the worst
 * ranked features are eliminated and the SVM is retrained with the left-over
 * attributes. The process is repeated until only one feature is retained. The
 * result is a feature ranking.
 * 
 * The C-parameter of the internal SVM can be optimized or can be fixed.
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RecursiveFeatureEliminationSVM implements FeatureRanking {

    /*
     * The final ranking of the attributes, this is constructed with the build
     * method.
     */
    private int[] ranking;

    /*
     * The percentage of worst feature that should be removed in each iteration.
     */
    private double removePercentage = 0.20;

    /* In case of optimization of the C-parameter, the number of folds to use */
    private int internalFolds;

    /*
     * Whether the internal SVM should optimize the C-parameter, when this is
     * not done it defaults to 1
     */
    private boolean optimize;

    /**
     * 
     * @param folds
     *            the number of internal folds to eliminate features
     * @param positiveClass
     * @param removePercentage
     * @param rg
     */
    public RecursiveFeatureEliminationSVM(double removePercentage) {
        this(removePercentage, false);

    }

    public RecursiveFeatureEliminationSVM(double removePercentage, boolean optimize) {
        this(removePercentage, optimize, 4);
    }

    public RecursiveFeatureEliminationSVM(double removePercentage, boolean optimize, int internalFolds) {
        this.removePercentage = removePercentage;
        this.optimize = optimize;
        this.internalFolds = internalFolds;

    }

    public void build(Dataset data) {
        /* The order of the importance of the features */
        int[] ordering = new int[data.noAttributes()];

        /* Bitmap of removed attributes */
        boolean[] removedAttributes = new boolean[data.noAttributes()];
        /*
         * Number of removed attributes, is always equal to the number of true
         * values in the above bitmap
         */
        int removed = 0;

        while (data.noAttributes() > 1) {
            Dataset training = data;
            LibSVM svm;
            if (optimize)
                svm = new SelfOptimizingLinearLibSVM(-4, 4, internalFolds);
            else {
                svm = new LibSVM();
                svm.getParameters().C=1;
            }
            svm.buildClassifier(training);
            double[] weights = svm.getWeights();

            /* Use absolute values of the weights */
            ArrayUtils.abs(weights);

            /* Order weights */
            int[] order = ArrayUtils.sort(weights);

            /* Determine the number of attributes to prune, round up */
            int numRemove = (int) (order.length * removePercentage + 1);
            if (numRemove > order.length)
                numRemove = order.length - 1;

            Set<Integer> toRemove = new HashSet<Integer>();
            int[] trueIndices = new int[numRemove];

            for (int i = 0; i < numRemove; i++) {
                toRemove.add(order[i]);
                trueIndices[i] = getTrueIndex(order[i], removedAttributes);
                ordering[ordering.length - removed - 1] = trueIndices[i];
                removed++;
            }
            // This needs to be done afterwards, otherwise the getTrueIndex
            // method will fail.
            for (int i = 0; i < numRemove; i++) {
                removedAttributes[trueIndices[i]] = true;
            }
            RemoveAttributes filter = new RemoveAttributes(toRemove);
            Dataset filtered=new DefaultDataset();
            for(Instance i:data){
            	filter.filter(i);
            	filtered.add(i);
            }
            data=filtered;
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
    }

    private int getTrueIndex(int i, boolean[] removedAttributes) {
        int index = 0;
        while (i >= 0) {

            if (!removedAttributes[index])
                i--;
            index++;

        }
        return index - 1;
    }

    public int rank(int attIndex) {
        return ranking[attIndex];
    }

    @Override
    public int noAttributes() {
        return ranking.length;

    }
}
