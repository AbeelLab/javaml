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

import org.apache.commons.math.stat.StatUtils;
import org.junit.Test;

import be.abeel.util.TimeInterval;

public class TestRandomForestSize {

    @Test
    public void testRandomForest2Size() {
        System.out.println("RF2");
        long sumTime = 0;
        try {
            double value;
            double dataval;
            long mem = 0;
            // for (int i = 0; i < value.length; i++) {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            // System.out.println("Loader: " + data.classes());
            long dataMem = checkMem();
            dataval = dataMem;
            // System.out.println(i);
            long time = System.currentTimeMillis();
            RandomForest rt = new RandomForest(5, false, 5, new Random());
            rt.buildClassifier(data);
            sumTime += System.currentTimeMillis() - time;
            mem = checkMem();
            value = mem;
            // }
            System.out.println("Total time: " + new TimeInterval(sumTime));
            System.out.println("Mean data memory size: " + (dataval / 1024.0) + " kb");
            System.out.println("Mean RF2 memory size: " + (value / 1024.0) + " kb");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private long checkMem() {
        try {
            System.gc();
            Thread.sleep(100);
            System.runFinalization();
            Thread.sleep(100);
            System.gc();
            Thread.sleep(500);
            System.gc();
            Thread.sleep(100);
            System.runFinalization();
            Thread.sleep(100);
            System.gc();
            Thread.sleep(100);
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            return totalMemory - freeMemory;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;

    }

}
