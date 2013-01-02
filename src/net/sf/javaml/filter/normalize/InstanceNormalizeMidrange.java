/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.normalize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.InstanceFilter;

/**
 * This filter will normalize all the attributes in an instance to a certain
 * interval determined by a mid-range and a range. This class implements both
 * the {@link DatasetFilter} and {@link InstanceFilter} interfaces. When you
 * apply this filter to a whole data set, each instance will be normalized
 * separately.
 * 
 * For example mid-range 0 and range 2 would yield instances with attributes
 * within the range [-1,1].
 * 
 * Each {@link Instance} is normalized separately. For example if you have three
 * instances {-5;0;5} and {0;40;20} and you normalize with mid-range 0 and range
 * 2, you would get {-1;0;1} and {-1;1;0}.
 * 
 * The default is normalization in the interval [-1,1].
 * 
 * 
 * 
 * @see InstanceFilter
 * @see DatasetFilter
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class InstanceNormalizeMidrange extends AbstractFilter {

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

    @Override
    public void filter(Dataset data) {
        for (Instance i : data)
            filter(i);
    }

    @Override
    public void filter(Instance instance) {
        // Find min and max values
        double min = instance.value(0);
        double max = min;
        for (Double d:instance) {
            if (d > max)
                max = d;
            if (d < min)
                min = d;
        }

        // Calculate the proper range and midrange
        double midrange = (max + min) / 2;
        double range = max - min;

       for(int i=0;i<instance.noAttributes();i++){
            if (range < EPSILON) {
                instance.put(i, normalMiddle);
            } else {
                instance.put(i, ((instance.value(i) - midrange) / (range / normalRange)) + normalMiddle);
            }
        }
        
    }

    public void build(Dataset data) {
        // do nothing, not required for this filter

    }
}
