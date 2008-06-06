/**
 * NormalizeMidrange.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter.normalize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.InstanceFilter;
import net.sf.javaml.filter.instance.ReplaceValueFilter;

/**
 * This filter will normalize the data set with a certain mid-range and a
 * certain range for each attribute. This class implements both the
 * {@link DatasetFilter} and {@link InstanceFilter} interfaces, but before you
 * can apply this filter to a single instance, you'll need to apply it to a data
 * set to calculate the proper mid-ranges for each attribute.
 * 
 * For example mid-range 0 and range 2 would yield a data set within the range
 * [-1,1].
 * 
 * Each attribute of each {@link Instance} is normalized separately. For
 * instance if you have three instances {-1;10}, {1;15} and {0;20} and you
 * normalize with mid-range 0 and range 2, you would get {-1,-1}, {1,0} and
 * {0,1}.
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
public class NormalizeMidrange extends AbstractFilter {

    /**
     * A normalization filter to the interval [-1,1]
     * 
     */
    public NormalizeMidrange() {
        this(0, 2);
    }

    private double normalMiddle;

    private double normalRange;

    public NormalizeMidrange(double middle, double range) {
        this.normalMiddle = middle;
        this.normalRange = range;

    }

    private Instance currentRange = null;

    private Instance currentMiddle = null;

    public void build(Dataset data) {
        // Calculate the proper range and midrange
        Instance max = DatasetTools.maxAttributes(data);
        Instance min = DatasetTools.minAttributes(data);
        currentRange = max.minus(min);
        currentMiddle = min.plus(max).divide(2);

    }

    @Override
    public void filterInstance(Instance instance) {
        if (currentRange == null || currentMiddle == null)
            throw new TrainingRequiredException();
        Instance tmp = instance.minus(currentMiddle).divide(currentRange).multiply(normalRange).plus(normalMiddle);
        instance.clear();
        instance.putAll(tmp);
        new ReplaceValueFilter(Double.NaN,normalMiddle).filterInstance(instance);
    }

    public void filterDataset(Dataset data) {
        if (currentRange == null || currentMiddle == null)
            build(data);
        for (Instance i : data)
            filterInstance(i);
    }

}
