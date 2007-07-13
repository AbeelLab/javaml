/**
 * RemoveAttributes.java
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
package junit.filter;

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.filter.NormalizeMidrange;
import net.sf.javaml.filter.PrincipalComponentsAnalysis;

import org.junit.Test;

public class PCATest {

    Random rg = new Random(4);

    @Test
    public void testRemove() throws Exception {

        double[] vals1 = { 1, 2, 3.5f, 4, 1 };
        double[] vals2 = { 1, 2, 4, 4, 2 };
        double[] vals3 = { 1, 2, 4.5f, 4, 3 };
        double[] vals4 = { 1, 2, 3.7f, 4, 4 };
        double[] vals5 = { 1, 2, 4.1f, 4, 5 };
        double[] vals6 = { 1, 2, 3.9f, 4, 6 };
       
      

        

        Dataset data = new SimpleDataset();
        Instance inst = new SimpleInstance(vals1);
        data.addInstance(inst);
        inst = new SimpleInstance(vals2);
        data.addInstance(inst);
        inst = new SimpleInstance(vals3);
        data.addInstance(inst);
        inst = new SimpleInstance(vals4);
        data.addInstance(inst);
        inst = new SimpleInstance(vals5);
        data.addInstance(inst);
        inst = new SimpleInstance(vals6);
        data.addInstance(inst);

        Filter norm = new NormalizeMidrange(0.5, 1);
        Dataset tmp = norm.filterDataset(data);

        Filter rem = new PrincipalComponentsAnalysis();
        Dataset out = rem.filterDataset(tmp);
        // System.out.println("JML "+out);
        for (int i = 0; i < out.size(); i++) {
            System.out.println("PCATest-JML: " + out.getInstance(i));
            System.out.println("PCATest-JML back: " + rem.unfilterInstance(out.getInstance(i)));
        }

        

    }

}
