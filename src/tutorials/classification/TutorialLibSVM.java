/**
 * %SVN.HEADER%
 */
package tutorials.classification;

import java.io.File;

import libsvm.LibSVM;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;


/**
 * This tutorial show how to use a the LibSVM classifier.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialLibSVM {
    /**
     * Shows the default usage of the LibSVM algorithm.
     */
    public static void main(String[] args) throws Exception {

        /* Load a data set */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /*
         * Contruct a LibSVM classifier with default settings.
         */
        Classifier svm = new LibSVM();
        svm.buildClassifier(data);

        /*
         * Load a data set, this can be a different one, but we will use the
         * same one.
         */
        Dataset dataForClassification = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /* Counters for correct and wrong predictions. */
        int correct = 0, wrong = 0;
        /* Classify all instances and check with the correct class values */
        for (Instance inst : dataForClassification) {
            Object predictedClassValue = svm.classify(inst);
            Object realClassValue = inst.classValue();
            if (predictedClassValue.equals(realClassValue))
                correct++;
            else
                wrong++;
        }
        System.out.println("Correct predictions  " + correct);
        System.out.println("Wrong predictions " + wrong);

    }

}
