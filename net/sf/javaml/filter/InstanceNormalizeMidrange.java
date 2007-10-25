/**
 * DatasetNormalizeMidrange.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

/**
 * This filter will normalize the all the attributes in an instance to a certain
 * interval determined by a midrange and a range. This class implements both the
 * {@link DatasetFilter} and {@link InstanceFilter} interfaces. When you apply
 * this filter to a whole dataset, each instance will be normalized separately.
 * 
 * For example midrange 0 and range 2 would yield instances with attributes
 * within the range [-1,1].
 * 
 * Each {@link Instance} is normalized separately. For example if you have three
 * instances {-5;0;5} and {0;40;20} and you normalize with midrange 0 and range
 * 2, you would get {-1;0;1} and {-1;1;0}.
 * 
 * The default is normalization in the interval [-1,1].
 * 
 * {@jmlSource}
 * 
 * @see InstanceFilter
 * @see DatasetFilter
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class InstanceNormalizeMidrange implements DatasetFilter, InstanceFilter {

    private static final double EPSILON = 1.0e-6;

    /**
     * A normalization filter to the interval [-1,1]
     * 
     */
    public InstanceNormalizeMidrange() {
        this(0, 2);
    }

    private double normalMiddle;

    private double normalRange;

    public InstanceNormalizeMidrange(double middle, double range) {
        this.normalMiddle = middle;
        this.normalRange = range;

    }

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        if (data.size() == 0)
            return out;

        // Filter all instance and return the modified dataset
        for (int i = 0; i < data.size(); i++) {
            Instance tmpInstance = data.getInstance(i);
            out.addInstance(this.filterInstance(tmpInstance));
        }
        return out;

    }

    public Instance filterInstance(Instance tmpInstance) {
        // Find min and max values
        double min = tmpInstance.getValue(0);
        double max = min;
        for (double d : tmpInstance.toArray()) {
            if (d > max)
                max = d;
            if (d < min)
                min = d;
        }

        // Calculate the proper range and midrange
        double midrange = (max + min) / 2;
        double range = max - min;

        double[] instance = tmpInstance.toArray();
        for (int j = 0; j < instance.length; j++) {
            if (range < EPSILON) {
                instance[j] = normalMiddle;
            } else {
                instance[j] = ((instance[j] - midrange) / (range / normalRange)) + normalMiddle;
            }
        }
        return new SimpleInstance(instance, tmpInstance);
    }

    public Instance unfilterInstance(Instance tmpInstance) {
        throw new UnsupportedOperationException("Cannot recover original instance.");
    }

    public Dataset unfilterDataset(Dataset data) {
        throw new UnsupportedOperationException("Cannot recover original instances in the dataset.");
    }
}
