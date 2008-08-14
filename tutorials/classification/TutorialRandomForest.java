/**
 * %SVN.HEADER%
 */
package tutorials.classification;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

import be.abeel.util.TimeInterval;

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
    @Test
    public void testDefaultRF() {

        try {
            /* Load a data set */
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            /*
             * Contruct a RF classifier that uses 5 neighbors to make a
             * decision.
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

        } catch (IOException e) {
            Assert.assertTrue(false);
        }

    }

    /**
     * Shows the default usage of the random forest algorithm.
     */
    @Test
    public void testRFClassDistribution() {

        try {
            /* Load a data set */
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            /*
             * Contruct a RF classifier.
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
                Map<Object, Double> distribution = rf.classDistribution(inst);
                Assert.assertTrue(distribution.size() == 3);
                Object predictedClassValue = rf.classify(inst);
                Object realClassValue = inst.classValue();
                if (predictedClassValue.equals(realClassValue))
                    correct++;
                else
                    wrong++;

                for (Object o : distribution.keySet()) {
                    if (o.equals(predictedClassValue))
                        Assert.assertTrue(distribution.get(o) > 0.5);
                    else
                        Assert.assertTrue(distribution.get(o) < 0.5);
                }
            }
            System.out.println("Correct predictions  " + correct);
            System.out.println("Wrong predictions " + wrong);

        } catch (IOException e) {
            Assert.assertTrue(false);
        }

    }

    @Test
    public void testRFPerformance() {
        long seed = System.currentTimeMillis();
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            System.out.println("Loader: " + data.classes());
            RandomForest rf2 = new RandomForest(10, false, 20, new Random(seed));
            CrossValidation cv = new CrossValidation(rf2);
            Map<Object, PerformanceMeasure> p = cv.crossValidation(data, 5, new Random(10));
            System.out.println("Performance: " + p);
            System.out.println("Accuracy: " + p.get("0").getAccuracy());

        } catch (IOException e) {
            Assert.assertTrue(false);
        }
        System.out.println(new TimeInterval(System.currentTimeMillis() - seed));

    }

}
