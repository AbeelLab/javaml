/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import be.abeel.util.TimeInterval;

public class TestRandomTreeBuild {
    @Test
    public void testRTPerformance() {
        long seed = System.currentTimeMillis();
        try {
            Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, "\t", ":");
            System.out.println("Loader: " + data.classes());
            RandomTree rt=new RandomTree(10,new Random());
            System.out.println("Building 1");
            rt.buildClassifier(data);
            System.out.println("Building 2");
            rt.buildClassifier(data);
            System.out.println("Building 3");
            rt.buildClassifier(data);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new TimeInterval(System.currentTimeMillis() - seed));

    }

}
