/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.evaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

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

    // private PerformanceMeasure[] foldPerformance = null;
    // TODO use evaluateDataset
    public Map<Object, PerformanceMeasure> crossValidation(Dataset data, int numFolds, Random rg) {
        // System.out.println("CV:" + data.size());
        Dataset[] folds = data.folds(numFolds, rg);
        // for(Dataset d:folds){
        // System.out.println("\t"+d.size());
        // }
        Map<Object, PerformanceMeasure> out = new HashMap<Object, PerformanceMeasure>();
        for (Object o : data.classes()) {
            out.put(o, new PerformanceMeasure());
        }
        for (int i = 0; i < numFolds; i++) {
            // System.out.println("Fold: "+i);
            Dataset validation = folds[i];
            Dataset training = new DefaultDataset();
            for (int j = 0; j < numFolds; j++) {
                if (j != i)
                    training.addAll(folds[j]);

            }
            // System.out.println("training: "+training.classes());
            // System.out.println("validation: "+validation.classes());
            // System.out.println("\tTraining:\t"+training.size());
            // System.out.println("\tValidation\t"+validation.size());
            classifier.buildClassifier(training);

            // int tp = 0, tn = 0, fn = 0, fp = 0;
            // System.out.println("real
            // class\tprediction\tpositive\tclassification");
            for (Instance instance : validation) {
                Object prediction = classifier.classify(instance);
                if (instance.classValue().equals(prediction)) {// prediction
                    // ==class
                    for (Object o : out.keySet()) {
                        if (o.equals(instance.classValue())) {
                            // System.out.println(instance.classValue()+"\t"+prediction+"\t"+o+"\tTP");
                            out.get(o).tp++;
                        } else {
                            out.get(o).tn++;
                            // System.out.println(instance.classValue()+"\t"+prediction+"\t"+o+"\tTN");
                        }

                    }
                } else {// prediction != class
                    for (Object o : out.keySet()) {
                        /* prediction is positive class */
                        if (prediction.equals(o)) {
                            out.get(o).fp++;
                            // System.out.println(instance.classValue()+"\t"+prediction+"\t"+o+"\tFP");
                        }
                        /* instance is positive class */
                        else if (o.equals(instance.classValue())) {
                            out.get(o).fn++;
                            // System.out.println(instance.classValue()+"\t"+prediction+"\t"+o+"\tFN");
                        }
                        /* none is positive class */
                        else {
                            out.get(o).tn++;
                            // System.out.println(instance.classValue()+"\t"+prediction+"\t"+o+"\tTN");
                        }

                    }
                }
            }

        }
        return out;

    }

}
