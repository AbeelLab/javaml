/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import be.abeel.util.TimeInterval;

public class TestRandomForestBuild {
    @Test
    public void testRF2Performance() {
        long seed = System.currentTimeMillis();
        try {
            Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.txt.gz"), 0, ";", ":");
            System.out.println("Loader: " + data.classes());
            RandomForest rf2 = new RandomForest(5, false, 2000, new Random(seed));
            System.out.println("Building 1");
            rf2.buildClassifier(data);
            System.out.println("Building 2");
            rf2.buildClassifier(data);
            System.out.println("Building 3");
            rf2.buildClassifier(data);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new TimeInterval(System.currentTimeMillis() - seed));

    }

}
