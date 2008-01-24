/**
 * InstanceFilter.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * The interface for filters that can be applied on an
 * {@link net.sf.javaml.core.Dataset}.
 * 
 * When applying a filter to a dataset it may modify the instances in the
 * dataset, and can alter the content of the dataset.
 * 
 * {@jmlSource}
 * 
 * @see Instance
 * @see Dataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface DatasetFilter {

    /**
     * This method can be used if the filter needs some training first
     * 
     * @param data
     *            the data used for training.
     */
    public void build(Dataset data);

    /**
     * Applies this filter to an dataset and return the modified dataset.
     * 
     * @param data
     *            the dataset to apply this filter to
     * @return the modified dataset
     */
    public Dataset filterDataset(Dataset data);

//    /**
//     * Tries to apply the reverse of this filter to the dataset to restore the
//     * original dataset.
//     * 
//     * @throws UnsupportedOperationException
//     *             if this filter does not work reverse
//     * @param data
//     *            the dataset to revert to its original
//     * @return the original dataset
//     */
//    public Dataset unfilterDataset(Dataset data);
}
