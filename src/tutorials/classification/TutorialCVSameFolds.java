/**
 * %SVN.HEADER%
 */
package tutorials.classification;

import java.io.File;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

/**
 * This tutorial shows how you can do multiple cross-validations with the same
 * folds.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialCVSameFolds {
    /**
     * Default cross-validation with the same folds for multiple runs.
     */
    public static void main(String[] args) throws Exception {
        /* Load data */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /* Construct KNN classifier */
        Classifier knn = new KNearestNeighbors(5);
        /* Construct new cross validation instance with the KNN classifier, */
        CrossValidation cv = new CrossValidation(knn);
        /*
         * Perform 5-fold cross-validation on the data set with a user-defined
         * random generator
         */
        Map<Object, PerformanceMeasure> p = cv.crossValidation(data, 5, new Random(1));

        Map<Object, PerformanceMeasure> q = cv.crossValidation(data, 5, new Random(1));

        Map<Object, PerformanceMeasure> r = cv.crossValidation(data, 5, new Random(25));

        System.out.println("Accuracy=" + p.get("Iris-virginica").getAccuracy());
        System.out.println("Accuracy=" + q.get("Iris-virginica").getAccuracy());
        System.out.println("Accuracy=" + r.get("Iris-virginica").getAccuracy());
        System.out.println(p);
        System.out.println(q);
        System.out.println(r);

    }
}
