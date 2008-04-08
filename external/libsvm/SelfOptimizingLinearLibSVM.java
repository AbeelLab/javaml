/**
 * SelfOptimizingLinearLibSVM.java
 *
 * %SVN.HEADER%
 */
package external.libsvm;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.utils.ArrayUtils;

public class SelfOptimizingLinearLibSVM implements Classifier {

    /**
     * 
     */
    private static final long serialVersionUID = 8643213412442061102L;

    private LibSVM optimal;

    private double optimalC;

    private int positiveClass = 1;

    private int folds = 5;
    
    //commit test
    public void buildClassifier(Dataset data) {
        double[] result = new double[24];

        int min = -12, max = 12;
        for (int i = min; i < max; i++) {
//            System.out.println("I="+i);
            LibSVM svm = new LibSVM();
            svm.setC(Math.pow(2, i));
            CrossValidation cv = new CrossValidation(svm);
            PerformanceMeasure pm = cv.crossValidation(data, positiveClass, folds);
            result[i - min] = pm.getFMeasure();

        }

        int index = ArrayUtils.maxIndex(result);
        optimal = new LibSVM();
        optimalC = Math.pow(2, index + min);
        optimal.setC(optimalC);

        optimal.buildClassifier(data);
//        for (double d : result) {
//            System.out.println(d);
//        }
    }

    public int classifyInstance(Instance instance) {
        return optimal.classifyInstance(instance);
    }

    public double[] distributionForInstance(Instance instance) {
        return optimal.distributionForInstance(instance);
    }

    public double getC() {
        return optimalC;
    }

    public double[] getWeights() {
        return optimal.getWeights();
    }

    public final void setPositiveClass(int positiveClass) {
        this.positiveClass = positiveClass;
    }

    public final void setFolds(int folds) {
        this.folds = folds;
    }

}
