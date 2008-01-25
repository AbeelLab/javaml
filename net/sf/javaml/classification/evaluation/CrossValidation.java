/**
 * CrossValidation.java
 *
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.evaluation;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

/**
 * Implementation of the cross-validation evaluation technique.
 * 
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class CrossValidation {

    private Classifier classifier;

    public CrossValidation(Classifier classifier) {
        this.classifier = classifier;
    }

    private PerformanceMeasure[] foldPerformance = null;

    public PerformanceMeasure crossValidation(Dataset data, int positiveClassValue, int numFolds) {
        Dataset[] folds = DatasetTools.createFolds(data, positiveClassValue, numFolds);
        foldPerformance = new PerformanceMeasure[numFolds];
        for (int i = 0; i < numFolds; i++) {
            Dataset validation = folds[i];
            Dataset training = new SimpleDataset();
            for (int j = 0; j < numFolds; j++) {
                if (j != i)
                   training.addAll(folds[j]);

            }
            classifier.buildClassifier(training);
            int tp = 0, tn = 0, fn = 0, fp = 0;
            for (Instance instance : validation) {
                int prediction = classifier.classifyInstance(instance);
                if (instance.classValue() == positiveClassValue) {
                    if (prediction == positiveClassValue)
                        tp++;
                    else
                        fn++;
                } else {
                    if (prediction == positiveClassValue)
                        fp++;
                    else
                        tn++;
                }
            }
            foldPerformance[i] = new PerformanceMeasure(tp, tn, fp, fn);

        }
        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < numFolds; i++) {
            tp += foldPerformance[i].truePositives;
            tn += foldPerformance[i].trueNegatives;
            fp += foldPerformance[i].falsePositives;
            fn += foldPerformance[i].falseNegatives;
        }

        return new PerformanceMeasure(tp, tn, fp, fn);
    }

    public PerformanceMeasure[] getFoldPerformance() {
        return foldPerformance;
    }

}
