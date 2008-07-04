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

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.classification.tree.RandomForest2;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestRandomForest {
    @Test
    public void testRFPerformance() {

        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            System.out.println("Loader: " + data.classes());
            RandomForest2 rf2 = new RandomForest2(10, false, 10, new Random());
            CrossValidation cv = new CrossValidation(rf2);
            System.out.println("RF2:" + cv.crossValidation(data, 5, new Random(10)));

            RandomForest rf = new RandomForest(10, false, 10, new Random());
            cv = new CrossValidation(rf);
            System.out.println("RF:" + cv.crossValidation(data, 5, new Random(10)));
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
