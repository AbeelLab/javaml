/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.normalize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.tools.DatasetTools;

/**
 * This filter will normalize the data set with mean 0 and standard deviation 1
 * 
 * The normalization will be done on the attributes, so each attribute will have
 * mean 0 and std 1.
 * 
 * Instead of using the true standard deviation, we use an estimator based on
 * the interquantile distance. This estimator assumes that he data follows a
 * normal distribution.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizeMeanIQR135 extends AbstractFilter {

    private Instance mean = null;

    private Instance std = null;

    @Override
    public void build(Dataset data) {
        mean = DatasetTools.percentile(data, 50);
        Dataset tmp = new DefaultDataset();
        for (Instance i : data)
            tmp.add(i.minus(mean));

        Instance q1 = DatasetTools.percentile(tmp, 25);
        Instance q3 = DatasetTools.percentile(tmp, 75);

        std = q3.minus(q1).divide(1.35);
    }

    @Override
    public void filter(Dataset data) {
        if (data.size() == 0)
            return;
        if (mean == null || std == null)
            build(data);
        super.filter(data);

    }

    @Override
    public void filter(Instance instance) {
        if (mean == null || std == null)
            throw new RuntimeException(
                    "You should first call filterDataset for this filter, some parameters are not yet set.");
        Instance tmp = instance.minus(mean).divide(std);
        for (int i = 0; i < instance.noAttributes(); i++)
            instance.put(i, tmp.value(i));
    }

    public Instance getStd() {
        return std;

    }

    public Instance getMean() {
        return mean;

    }

}
