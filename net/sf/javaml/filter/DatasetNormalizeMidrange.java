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
 * This filter will normalize the dataset with a certain midrange and a certain
 * range for each attribute. This class implements both the
 * {@link DatasetFilter} and {@link InstanceFilter} interfaces, but before you
 * can apply this filter to a single instance, you'll need to apply it to a
 * dataset to calculate the proper midranges for each attribute.
 * 
 * For example midrange 0 and range 2 would yield a dataset within the range
 * [-1,1].
 * 
 * Each attribute of each {@link Instance} is normalized separately. For
 * instance if you have three instances {-1;10}, {1;15} and {0;20} and you
 * normalize with midrange 0 and range 2, you would get {-1,-1}, {1,0} and
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
public class DatasetNormalizeMidrange extends AbstractFilter {

    private static final double EPSILON = 1.0e-6;

    /**
     * A normalization filter to the interval [-1,1]
     * 
     */
    public DatasetNormalizeMidrange() {
        this(0, 2);
    }

    private double normalMiddle;
    private double normalRange;

    public DatasetNormalizeMidrange(double middle, double range) {
        this.normalMiddle = middle;
        this.normalRange = range;

    }

    private double[] range = null;

    private double[] midrange = null;

    private boolean constructed = false;

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        if (data.size() == 0)
            return out;
        // Calculate the proper range and midrange
        Instance max = data.getMaximumInstance();
        Instance min = data.getMinimumInstance();
        int instanceLength = data.getInstance(0).size();
        this.midrange = new double[instanceLength];
        this.range = new double[instanceLength];
        for (int i = 0; i < instanceLength; i++) {
            range[i] = max.getValue(i) - min.getValue(i);
            midrange[i] = (max.getValue(i) + min.getValue(i)) / 2;
        }
        // Filter all instance and return the modified dataset
        for (int i = 0; i < data.size(); i++) {
            Instance tmpInstance = data.getInstance(i);
            out.addInstance(this.filter(tmpInstance));
        }
        this.constructed = true;
        return out;

    }

    private Instance filter(Instance tmpInstance) {
        double[] instance = tmpInstance.toArray();
        for (int j = 0; j < instance.length; j++) {
            if (range[j] < EPSILON) {
                instance[j] = normalMiddle;
            } else {
                instance[j] = ((instance[j] - midrange[j]) / (range[j] / normalRange)) + normalMiddle;
            }
        }
        return new SimpleInstance(instance, tmpInstance);
    }

    public Instance unfilterInstance(Instance tmpInstance) {
        double[] instance = tmpInstance.toArray();
        for (int j = 0; j < instance.length; j++) {
            instance[j] = instance[j] * (range[j] / normalRange) + midrange[j];
        }
        return new SimpleInstance(instance, tmpInstance);
    }

    public Instance filterInstance(Instance instance) {
        if (!constructed) {
            throw new RuntimeException(
                    "You should call filterDataset(Dataset data) before calling filterInstance. Some parameters are not yet set.");
        }
        return filter(instance);

    }

    public Dataset unfilterDataset(Dataset data) {
        if (!constructed) {
            throw new RuntimeException(
                    "You should call filterDataset(Dataset data) before calling unfilterDataset. Some parameters are not yet set.");
        }
        Dataset out = new SimpleDataset();
        if (data.size() == 0)
            return out;
        // Unfilter all instances of the dataset and return the modified dataset
        for (int i = 0; i < data.size(); i++) {
            Instance tmpInstance = data.getInstance(i);
            out.addInstance(this.unfilterInstance(tmpInstance));
        }
        return out;
    }
}
