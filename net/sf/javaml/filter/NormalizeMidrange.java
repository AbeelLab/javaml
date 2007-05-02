/**
 * NormalizeMidrange.java, 4-dec-2006
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

/**
 * This filter will normalize the dataset with a certain midrange and a certain
 * range. Before you can apply this filter to a single instance, you'll need to
 * apply it to a dataset.
 * 
 * For example midrange 0 and range 2 would yield a dataset within the range
 * [-1,1].
 * 
 * Each attribute of each instance is normalized seperately. For instance if you
 * have three instances {-1;10}, {1,15} and {0,20} and you normalize with
 * midrange 0 and range 2, you would get {-1,-1}, {1,0} and {0,1}.
 * 
 * The default is normalization in the interval [-1,1].
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizeMidrange implements Filter {

    /**
     * A normalization filter to the interval [-1,1]
     * 
     */
    public NormalizeMidrange() {
        this(0, 2);
    }

    private float normalMiddle, normalRange;

    public NormalizeMidrange(double middle, double range) {
        this.normalMiddle = (float) middle;
        this.normalRange = (float) range;

    }

    private float[] range, midrange;

    private boolean constructed = false;

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        if (data.size() == 0)
            return out;
        Instance max = data.getMaximumInstance();
        Instance min = data.getMinimumInstance();
        int instanceLength = data.getInstance(0).size();
        this.midrange = new float[instanceLength];
        this.range = new float[instanceLength];
        for (int i = 0; i < instanceLength; i++) {
            range[i] = max.getValue(i) - min.getValue(i);
            midrange[i] = (max.getValue(i) + min.getValue(i)) / 2;
        }
        for (int i = 0; i < data.size(); i++) {
            Instance tmpInstance = data.getInstance(i);
            out.addInstance(this.filter(tmpInstance));
        }
        this.constructed = true;
        return out;

    }

    private Instance filter(Instance tmpInstance) {
        float[] instance = tmpInstance.toArray();
        // FIXME if an attribute always has the same value, the range is zero
        // and you will divide by zero. This should be fixed such that those
        // values that are always the same are mapped to the midrange value.

        for (int j = 0; j < instance.length; j++) {
            instance[j] = ((instance[j] - midrange[j]) / (range[j] / normalRange)) + normalMiddle;
        }
        return new SimpleInstance(instance, tmpInstance.getWeight(), tmpInstance.isClassSet(), tmpInstance
                .getClassValue());
    }

    public Instance unfilterInstance(Instance tmpInstance) {
        float[] instance = tmpInstance.toArray();
        for (int j = 0; j < instance.length; j++) {
            // instance[j] = ((instance[j] - midrange[j]) / (range[j] /
            // normalRange)) + normalMiddle;
            instance[j] = instance[j] * (range[j] / normalRange) + midrange[j];
        }
        return new SimpleInstance(instance, tmpInstance.getWeight(), tmpInstance.isClassSet(), tmpInstance
                .getClassValue());
    }

    public Instance filterInstance(Instance instance) {
        if (!constructed) {
            throw new RuntimeException(
                    "You should call filterDataset(Dataset data) before calling filterInstance. Some parameters are not yet set.");
        }
        return filter(instance);

    }
}
