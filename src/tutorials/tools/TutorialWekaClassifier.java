/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClassifier;
import weka.classifiers.functions.SMO;

/**
 * Tutorial how to use a Weka classifier in Java-ML.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialWekaClassifier {

    public static void main(String[] args) throws Exception {
        /* Load data */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /* Create Weka classifier */
        SMO smo = new SMO();
        /* Wrap Weka classifier in bridge */
        Classifier javamlsmo = new WekaClassifier(smo);
        /* Initialize cross-validation */
        CrossValidation cv = new CrossValidation(javamlsmo);
        /* Perform cross-validation */
        Map<Object, PerformanceMeasure> pm = cv.crossValidation(data);
        /* Output results */
        System.out.println(pm);
    }
}
