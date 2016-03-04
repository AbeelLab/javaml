/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * The interface for filters that can be applied on an
 * {@link net.sf.javaml.core.Dataset}.
 * 
 * When applying a filter to a data set it may modify the instances in the
 * data set, and can alter the content of the data set.
 * 
 * 
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
     */
    public void filter(Dataset data);

}
