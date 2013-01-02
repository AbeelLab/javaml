/**
 * %SVN.HEADER%
 */
package tutorials.classification;

import java.io.File;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

/**
 * Tutorial for the random forest classifier.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialRandomForest {

    /**
     * Shows the default usage of the random forest algorithm.
     */
    public static void main(String[] args) throws Exception {
        /* Load a data set */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /*
         * Contruct a RF classifier that uses 5 neighbors to make a decision.
         */
        Classifier rf = new RandomForest(50, false, 3, new Random());
        rf.buildClassifier(data);

        /*
         * Load a data set, this can be a different one, but we will use the
         * same one.
         */
        Dataset dataForClassification = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /* Counters for correct and wrong predictions. */
        int correct = 0, wrong = 0;
        /* Classify all instances and check with the correct class values */
        for (Instance inst : dataForClassification) {
            Object predictedClassValue = rf.classify(inst);
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
