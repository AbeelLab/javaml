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

import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestRandomTree {
    @Test
    public void testKNN() {

        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0,",");
            System.out.println("Loader: "+data.classes());
            RandomTree knn=new RandomTree(5,new Random());
            CrossValidation cv = new CrossValidation(knn);
            System.out.println("Java-ML-0:"+cv.crossValidation(data, 5, new Random(10)));
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
