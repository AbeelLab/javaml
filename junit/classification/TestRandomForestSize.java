/*
 * TestKNN.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.classification.tree.RandomForest2;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.apache.commons.math.stat.StatUtils;
import org.junit.Test;

public class TestRandomForestSize {

    @Test
    public void testRandomForest2Size() {
        try {
            double[] value = new double[20];
            double[] dataval = new double[value.length];
            long mem = 0;
            for (int i = 0; i < value.length; i++) {
                Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
                // System.out.println("Loader: " + data.classes());
                long dataMem = checkMem();
                dataval[i] = dataMem;
                // System.out.println(i);
                RandomForest2 rt = new RandomForest2(10, false, 5, new Random());
                rt.buildClassifier(data);
                mem = checkMem();
                value[i] = mem;
            }
            System.out.println("Mean data memory size: " + (StatUtils.mean(dataval)/1024.0)+" kb");
            System.out.println("Mean RF2 memory size: " + (StatUtils.mean(value)/1024.0)+" kb");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testRandomForestSize() {
        try {
            double[] value = new double[20];
            double[] dataval = new double[value.length];
            long mem = 0;
            for (int i = 0; i < value.length; i++) {
                Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
                // System.out.println("Loader: " + data.classes());
                long dataMem = checkMem();
                dataval[i] = dataMem;
                // System.out.println(i);
                RandomForest rt = new RandomForest(10, false, 5, new Random());
                rt.buildClassifier(data);
                mem = checkMem();
                value[i] = mem;
            }
            System.out.println("Mean data memory size: " + (StatUtils.mean(dataval)/1024.0)+" kb");
            System.out.println("Mean RF memory size: " + (StatUtils.mean(value)/1024.0)+" kb");

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
