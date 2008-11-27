/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestRandomTree {
    @Test
    public void testRTSparse() {
        try {
            Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.txt.gz"), 0, ";", ":");
            System.out.println("noAttributes: " + data.noAttributes());
            System.out.println("instances: " + data.size());
            RandomTree rt = new RandomTree(5, new Random());
            CrossValidation cv = new CrossValidation(rt);
            System.out.println(cv.crossValidation(data, 2, new Random()));
        } catch (IOException e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testRT() {

        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            System.out.println("Loader: " + data.classes());
            RandomTree knn = new RandomTree(5, new Random());
            CrossValidation cv = new CrossValidation(knn);
            System.out.println("Java-ML-0:" + cv.crossValidation(data, 5, new Random(10)));

        } catch (IOException e) {
            Assert.assertTrue(false);
        }

    }

}
