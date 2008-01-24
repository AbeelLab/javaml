/**
 * WekaBinarySMO.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package junit.classification.svm;

import java.io.File;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.svm.BinaryLinearSMO;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class BinaryLinearSMOTest {

    @Test
    public void testConstructor() {
        BinaryLinearSMO smo = new BinaryLinearSMO ();
        try {
            CrossValidation cv=new CrossValidation(smo);
            System.out.println("Iris dataset");
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4);
            System.out.println(cv.crossValidation(data, 1,5));
//            smo.setVerbose(true);
//            smo.buildClassifier(data);
//            
//            System.out.println("SMO\ttrue");
//            for (Instance instance : data) {
//                System.out.print(smo.classifyInstance(instance));
//                System.out.println("\t" + instance.getClassValue());
//            }
            
            System.out.println();
            System.out.println("BUPA dataset");
            data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
          
//            smo.buildClassifier(data);
//            System.out.println("SMO\ttrue");
//            for (Instance instance : data) {
//                System.out.print(smo.classifyInstance(instance));
//                System.out.println("\t" + instance.getClassValue());
//            }
            
            
            System.out.println(cv.crossValidation(data, 1,5));
            System.out.println(data.size());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        }
    }
}
