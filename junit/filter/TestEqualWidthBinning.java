/**
 * TestEqualWidthBinning.java
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
 * Copyright (c) 2006-2008, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package junit.filter;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestEqualWidthBinning {

    @Test
    public void testNaNWhenAllSameValues() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            EqualWidthBinning eb = new EqualWidthBinning();
            // ga.setNumNeigbors(4);

            

            System.out.println("Before binning: ");
            System.out.println(data);
            System.out.println(DatasetTools.minAttributes(data));
            System.out.println(DatasetTools.maxAttributes(data));
            System.out.println("Binning...");
            eb.build(data);
            eb.filterDataset(data);
            System.out.println("--");
            System.out.println("After binning: ");
            System.out.println(data);
            Instance min=DatasetTools.minAttributes(data);
            Instance max =DatasetTools.maxAttributes(data);
            for(int i=0;i<data.noAttributes();i++){
            	Assert.assertTrue(min.value(i)==0);
            	Assert.assertTrue(max.value(i)==9);
            }
            data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
            eb.build(data);
            eb.filterDataset(data);
            min=DatasetTools.minAttributes(data);
            max =DatasetTools.maxAttributes(data);
            for(int i=0;i<data.noAttributes();i++){
            	Assert.assertTrue(min.value(i)==0);
            	Assert.assertTrue(max.value(i)==9);
            }
//            System.out.println(ga.evaluateAttribute(0));
//            System.out.println(ga.evaluateAttribute(1));
//            System.out.println(ga.evaluateAttribute(2));
//            System.out.println(ga.evaluateAttribute(3));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
