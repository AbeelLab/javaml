/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.normalize;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.tools.DatasetTools;

/**
 * This filter will normalize the data set with mean 0 and standard deviation 1
 * 
 * The normalization will be done on the attributes, so each attribute will have
 * mean 0 and std 1.
 * 
 * @linkplain http://www.faqs.org/faqs/ai-faq/neural-nets/part2/section-16.html
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizeMean extends AbstractFilter {

    private Instance mean = null;

    private Instance std = null;

    @Override
    public void filter(Dataset data) {
        if (data.size() == 0)
            return;
        mean = DatasetTools.average(data);// new double[instanceLength];
        std = DatasetTools.standardDeviation(data, mean);
        for (Instance i : data)
            filter(i);

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

}
