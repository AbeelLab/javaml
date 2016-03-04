/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.evaluation;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Tests a classifier on a data set
 * 
 * @author Thomas Abeel
 */
public class EvaluateDataset {

    /**
     * Tests a classifier on a data set
     * 
     * @param cls
     *            the classifier to test
     * @param data
     *            the data set to test on
     * @return the performance for each class
     */
    public static Map<Object, PerformanceMeasure> testDataset(Classifier cls, Dataset data) {
        Map<Object, PerformanceMeasure> out = new HashMap<Object, PerformanceMeasure>();
        for (Object o : data.classes()) {
            out.put(o, new PerformanceMeasure());
        }
        for (Instance instance : data) {
            Object prediction = cls.classify(instance);
            if (instance.classValue().equals(prediction)) {// prediction
                // ==class
                for (Object o : out.keySet()) {
                    if (o.equals(instance.classValue())) {
                        out.get(o).tp++;
                    } else {
                        out.get(o).tn++;
                    }

                }
            } else {// prediction != class
                for (Object o : out.keySet()) {
                    /* prediction is positive class */
                    if (prediction.equals(o)) {
                        out.get(o).fp++;
                    }
                    /* instance is positive class */
                    else if (o.equals(instance.classValue())) {
                        out.get(o).fn++;
                    }
                    /* none is positive class */
                    else {
                        out.get(o).tn++;
                    }

                }
            }

        }
        return out;
    }
}
