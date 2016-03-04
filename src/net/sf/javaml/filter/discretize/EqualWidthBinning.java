/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.discretize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.instance.FloorValueFilter;
import net.sf.javaml.tools.DatasetTools;

/**
 * A filter that discretizes a range of numeric attributes in the data set into
 * nominal attributes. Discretization is done by binning.
 * 
 * @author Thomas Abeel
 */
public class EqualWidthBinning extends AbstractFilter {
    /**
     * The number of bins
     */
    private int numBins = 10;

//    private Instance currentMiddle;

    private Instance range;

    private Instance min;

    public EqualWidthBinning() {
        this(10);
    }

    public EqualWidthBinning(int numBins) {
        this.numBins = numBins;
    }

    public void build(Dataset data) {
        min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);
//        currentMiddle = min;
        range = max.minus(min);
//        System.out.println("range: "+range);
    }

    private FloorValueFilter rvf = new FloorValueFilter();

    @Override
    public void filter(Instance instance) {
        if (range == null )
            throw new TrainingRequiredException();

        Instance tmp = instance.minus(min).divide(range).multiply(numBins - 1);// .plus((numBins-1)
                                                                                                // /
                                                                                                // 2);
        instance.clear();
        instance.putAll(tmp);
        rvf.filter(instance);

    }

    public void filter(Dataset data) {
        if (range == null)
            build(data);
        for (Instance i : data)
            filter(i);
    }

}
