/**
 * SelfOptimizingLinearLibSVM.java
 *
 * %SVN.HEADER%
 */
package external.libsvm;

import java.util.Map;
import java.util.Random;

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

    private Object positiveClass = "1";

    private int folds = 5;

    private Random rg;

    public SelfOptimizingLinearLibSVM(Object positiveClass, Random rg) {
        this.positiveClass = positiveClass;
        this.rg = rg;
    }

    // commit test
    public void buildClassifier(Dataset data) {
        double[] result = new double[24];

        int min = -12, max = 12;
        for (int i = min; i < max; i++) {
            // System.out.println("I="+i);
            LibSVM svm = new LibSVM();
            svm.setC(Math.pow(2, i));
            CrossValidation cv = new CrossValidation(svm);
            Map<Object, PerformanceMeasure> score = cv.crossValidation(data, folds, rg);
            try {
                result[i - min] = score.get(positiveClass).getFMeasure();
            } catch (RuntimeException e) {
                // TODO Auto-generated catch block
                System.out.println(positiveClass.getClass());
                System.err.println(score.keySet().iterator().next().getClass());
                System.err.println(positiveClass);
                System.err.println(score);
                System.err.println(score.get(positiveClass));
                e.printStackTrace();
                System.exit(-1);
            }

        }

        int index = ArrayUtils.maxIndex(result);
        optimal = new LibSVM();
        optimalC = Math.pow(2, index + min);
        optimal.setC(optimalC);

        optimal.buildClassifier(data);
        // for (double d : result) {
        // System.out.println(d);
        // }
    }

    @Override
    public Object classify(Instance instance) {
        return optimal.classify(instance);
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        return optimal.classDistribution(instance);
    }

    public double getC() {
        return optimalC;
    }

    public double[] getWeights() {
        return optimal.getWeights();
    }

    public final void setPositiveClass(Object positiveClass) {
        this.positiveClass = positiveClass;
    }

    public final void setFolds(int folds) {
        this.folds = folds;
    }

}
