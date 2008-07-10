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
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.classification.tree.RandomForest2;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import be.abeel.util.TimeInterval;

public class TestRandomForest {
    @Test
    public void testRF2Performance() {
        long seed = System.currentTimeMillis();
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            System.out.println("Loader: " + data.classes());
            RandomForest2 rf2 = new RandomForest2(10, false, 20, new Random(seed));
            CrossValidation cv = new CrossValidation(rf2);
            Map<Object,PerformanceMeasure>p=cv.crossValidation(data, 5, new Random(10));
            System.out.println("RF2:" + p);
            System.out.println("RF2:--" + p.get("0").getAccuracy());
           

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new TimeInterval(System.currentTimeMillis()-seed));

    }

    @Test
    public void testRFPerformance() {
        long seed = System.currentTimeMillis();
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            System.out.println("Loader: " + data.classes());

            RandomForest rf = new RandomForest(10, false, 20, new Random(seed));
            CrossValidation cv = new CrossValidation(rf);
            Map<Object,PerformanceMeasure>p=cv.crossValidation(data, 5, new Random(10));
            System.out.println("RF:" + p);
            System.out.println("RF:--" + p.get("0").getAccuracy());
          

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new TimeInterval(System.currentTimeMillis()-seed));

    }

}
