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

import weka.attributeSelection.PrincipalComponents;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;

public class PCATest {

    Random rg = new Random(4);

    @Test
    public void testRemove() throws Exception {

        float[] vals1 = { 1, 2, 3.5f, 4, 1 };
        double[] vals1d = { 1, 2, 3.5, 4, 1 };
        float[] vals2 = { 1, 2, 4, 4, 2 };
        double[] vals2d = { 1, 2, 4, 4, 2 };
        float[] vals3 = { 1, 2, 4.5f, 4, 3 };
        double[] vals3d = { 1, 2, 4.5, 4, 3 };
        float[] vals4 = { 1, 2, 3.7f, 4, 4 };
        double[] vals4d = { 1, 2, 3.7, 4, 4 };
        float[] vals5 = { 1, 2, 4.1f, 4, 5 };
        double[] vals5d = { 1, 2, 4.1, 4, 5 };
        float[] vals6 = { 1, 2, 3.9f, 4, 6 };
        double[] vals6d = { 1, 2, 3.9, 4, 6 };

        FastVector atts = new FastVector();
        // - numeric
        atts.addElement(new Attribute("att1"));
        atts.addElement(new Attribute("att2"));
        atts.addElement(new Attribute("att3"));
        atts.addElement(new Attribute("att4"));
        atts.addElement(new Attribute("att5"));

        Instances dat = new Instances("Relation", atts, 0);
        dat.add(new weka.core.Instance(1.0, vals1d));
        dat.add(new weka.core.Instance(1.0, vals2d));
        dat.add(new weka.core.Instance(1.0, vals3d));
        dat.add(new weka.core.Instance(1.0, vals4d));
        dat.add(new weka.core.Instance(1.0, vals5d));
        dat.add(new weka.core.Instance(1.0, vals6d));
        PrincipalComponents pc = new PrincipalComponents();
        pc.setNormalize(true);
        pc.setTransformBackToOriginal(true);
        pc.setVarianceCovered(1.0);
        pc.buildEvaluator(dat);

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
        System.out.println("WEKA " + pc.transformedData());
        for (int i = 0; i < out.size(); i++) {
            System.out.println("JML: " + out.getInstance(i));
            System.out.println("JML back: " + rem.unfilterInstance(out.getInstance(i)));
        }

        // FIXME instead of printing out the instances, we should check them
        // whether they are the same using asserTrue.

    }

}
