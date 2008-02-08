/**
 * Filter.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * A filter is procedure you can apply to a dataset which will return an altered
 * version of the dataset. Some filters may normalize the data, others may mask
 * missing values, etc.
 * 
 * {@jmlSource}
 * 
 * @see Instance
 * @see Dataset
 * @see InstanceFilter
 * @see DatasetFilter 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 * 
 * @deprecated This interface has been deprecated in favor of the
 *             {@link InstanceFilter} and {@link DatasetFilter} interfaces.
 */
@Deprecated
public interface Filter {

    /**
     * This method will apply the filter to all the Instances in the dataset.
     * Some filters may require to first call this method before you can filter
     * single instances with the <code>filterInstance</code> method.
     * 
     * @param data
     *            the dataset to be filtered
     * @return the filtered dataset
     */
    public Dataset filterDataset(Dataset data);

    /**
     * This method will filter an instance according to the rules of this
     * filter. Some filters may require to first call <code>filterDataset</code>
     * before you can use this method.
     * 
     * @param instance
     *            the instance to be filtered
     * @return the filtered instance
     */
    public Instance filterInstance(Instance instance);

    public Instance unfilterInstance(Instance instance);
}
