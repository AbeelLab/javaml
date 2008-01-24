/**
 * DatasetNormalizeMidrange.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter.normalize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.FilterUtils;
import net.sf.javaml.filter.InstanceFilter;
import net.sf.javaml.utils.MathUtils;

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
public class DatasetNormalizeMidrange extends AbstractFilter {

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

    public void build(Dataset data) {
        // Calculate the proper range and midrange
        Instance max = data.getMaximumInstance();
        Instance min = data.getMinimumInstance();
        int instanceLength = data.instance(0).size();
        this.midrange = new double[instanceLength];
        this.range = new double[instanceLength];
        for (int i = 0; i < instanceLength; i++) {
            range[i] = max.value(i) - min.value(i);
            midrange[i] = (max.value(i) + min.value(i)) / 2;
        }
    }

    public Instance filterInstance(Instance tmpInstance) {
        if (range == null || midrange == null)
            throw new TrainingRequiredException();

        double[] instance = tmpInstance.toArray();
        for (int j = 0; j < instance.length; j++) {
            if (MathUtils.zero(range[j])) {
                instance[j] = normalMiddle;
            } else {
                instance[j] = ((instance[j] - midrange[j]) / (range[j] / normalRange)) + normalMiddle;
                if(Double.isNaN(instance[j])){
                    System.out.println("I="+instance[j]);
                    System.out.println("mR="+midrange[j]);
                    System.out.println("r="+range[j]);
                    System.out.println("nR="+normalRange);
                    System.out.println("nM="+normalMiddle);
                    
                }
            }
        }
        return new SimpleInstance(instance, tmpInstance);
    }

//    public Instance unfilterInstance(Instance tmpInstance) {
//        if (range == null || midrange == null)
//            throw new TrainingRequiredException();
//
//        double[] instance = tmpInstance.toArray();
//        for (int j = 0; j < instance.length; j++) {
//            instance[j] = instance[j] * (range[j] / normalRange) + midrange[j];
//        }
//        return new SimpleInstance(instance, tmpInstance);
//    }

//    public Dataset unfilterDataset(Dataset data) {
//        if (range == null || midrange == null)
//            build(data);
//        return FilterUtils.removeFilter(this, data);
//    }

    public Dataset filterDataset(Dataset data) {
        if (range == null || midrange == null)
            build(data);
        return FilterUtils.applyFilter(this, data);
    }

}
