/**
 * FilterUtils.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

/**
 * This class provides utility methods for Filtering techniques.
 * 
 * {@jmlSource}
 * 
 * @see net.sf.javaml.filter.InstanceFilter
 * @see net.sf.javaml.filter.DatasetFilter
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class FilterUtils {

    /**
     * Applies an instance filter to a complete data set. The output contains
     * all the filtered instances.
     * 
     * @param filter
     *            the InstanceFilter to apply to all instances of the data set
     * @param data
     *            the data set to apply the filter to
     * @return the data set containing the filtered instances
     */
    public static Dataset applyFilter(InstanceFilter filter, Dataset data) {
        Dataset out = new SimpleDataset();
        for (Instance i : data) {
            out.addInstance(filter.filterInstance(i));
        }
        return out;
    }
}
