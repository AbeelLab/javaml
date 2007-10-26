/**
 * NormalizeMidrangeTest.java
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

import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.filter.DatasetNormalizeMidrange;

import org.junit.Test;

public class NormalizeMidrangeTest {

    @Test
    public void testNaNWhenAllSameValues() {
        Dataset data = new SimpleDataset();
        add(data, 0, 0, 1);
        add(data, 1, 1, 1);
        add(data, 2, 2, 1);
        add(data, 0, 1, 1);

        DatasetFilter  f = new DatasetNormalizeMidrange(0.5, 1);
        data = f.filterDataset(data);
        Assert.assertFalse(new Float(data.getInstance(0).getValue(2)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(1).getValue(2)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(2).getValue(2)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(3).getValue(2)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(0).getValue(0)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(1).getValue(1)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(2).getValue(0)).equals(Float.NaN));
        Assert.assertFalse(new Float(data.getInstance(3).getValue(1)).equals(Float.NaN));

    }

    private void add(Dataset data, float x, float y, float z) {
        double[] values = { x, y, z };
        SimpleInstance in = new SimpleInstance(values);
        data.addInstance(in);
    }

}
