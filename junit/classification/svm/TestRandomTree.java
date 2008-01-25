/**
 * TestRandomTree.java
 *
 * %SVN.HEADER%
 */
package junit.classification.svm;

import java.io.File;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestRandomTree {

    @Test
    public void testTree() {
        try {
            
            System.out.println("Iris data");
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4);
            process(data);
            
            System.out.println("-------------------");
            System.out.println("BUPA data");
            data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
            process(data);

            System.out.println("-------------------");
            System.out.println("Pima Indians");
            data = FileHandler.loadDataset(new File("devtools/data/pima.tsv"), 8);
            process(data);

            System.out.println("-------------------");
            System.out.println("Wisconson cancer");
            data = FileHandler.loadDataset(new File("devtools/data/wdbc.csv"), 1, ",");
            process(data);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }

    }

    private void process(Dataset data) {
        RandomTree tree = new RandomTree();
        tree.buildClassifier(data);

        CrossValidation cv = new CrossValidation(tree);
        PerformanceMeasure x = cv.crossValidation(data, 1, 4);
        System.out.println("RandomTree: ");
        System.out.println(x + "\n" + x.getFMeasure());

        RandomForest rf = new RandomForest(50, true,5);
        rf.buildClassifier(data);
        System.out.println("RF-OOBEE:\n" + rf.getOutOfBagErrorEstimate() + "\n"
                + rf.getOutOfBagErrorEstimate().getFMeasure());

        rf = new RandomForest(50, false,5);
        CrossValidation cv2 = new CrossValidation(rf);
        PerformanceMeasure pf = cv2.crossValidation(data, 1, 4);
        System.out.println("RF: " + pf + "\n" + pf.getFMeasure());
    }
}
