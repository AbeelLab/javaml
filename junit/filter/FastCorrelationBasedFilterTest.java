/**
 * FastCorrelationBasedFilterTest.java
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

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.FastCorrelationBasedFilter;

import org.junit.Test;

public class FastCorrelationBasedFilterTest {

    @Test
    public void testBasic() {
        double[] tmp = { 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 7, 5, 3 };
        Instance in = new SimpleInstance(tmp, 1, 1);
        FastCorrelationBasedFilter filter = new FastCorrelationBasedFilter();
        Dataset data = new SimpleDataset();
        data.addInstance(in);
        filter.filterDataset(data);
    }

    @Test
    public void testRandom() {
        Dataset data = new SimpleDataset();
        for (int i = 0; i < 10; i++) {
            double[] tmp = { 4, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 7, 5, 3 };
            Instance in = new SimpleInstance(tmp, 1, 1);
            data.addInstance(in);
        }
        for (int i = 0; i < 10; i++) {
            double[] tmp = { 4, 2, 2, 2, 3, 3, 5, 4, 5, 6, 7, 8, 9, 7, 5, 3 };
            Instance in = new SimpleInstance(tmp, 1, 7);
            data.addInstance(in);
        }
        for (int i = 0; i < 10; i++) {
            double[] tmp = { 4, 1, 3, 3, 3, 3, 6, 4, 5, 6, 7, 8, 9, 7, 5, 3 };
            Instance in = new SimpleInstance(tmp, 1, 6);
            data.addInstance(in);
        }
        for (int i = 0; i < 10; i++) {
            double[] tmp = { 4, 1, 2, 2, 3, 3, 7, 5, 5, 6, 7, 8, 9, 7, 5, 3 };
            Instance in = new SimpleInstance(tmp, 1, 1);
            data.addInstance(in);
        }
        for (int i = 0; i < 10; i++) {
            double[] tmp = { 4, 1, 2, 2, 3, 3, 7, 5, 5, 6, 7, 5, 4, 7, 5, 1 };
            Instance in = new SimpleInstance(tmp, 1, 0);
            data.addInstance(in);
        }

        for (int i = 0; i < 10; i++) {
            double[] tmp = { 4, 1, 2, 2, 3, 3, 7, 5, 5, 6, 7, 5, 4, 7, 5, 5 };
            Instance in = new SimpleInstance(tmp, 1, 2);
            data.addInstance(in);
        }
        FastCorrelationBasedFilter filter = new FastCorrelationBasedFilter();
        filter.filterDataset(data);

        double[] tmp = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        System.out.println(filter.filterInstance(new SimpleInstance(tmp)));
    }
}
