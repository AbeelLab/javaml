/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Umbrella class for filters that implements both the
 * {@link net.sf.javaml.filter.InstanceFilter} and
 * {@link net.sf.javaml.filter.DatasetFilter} interfaces.
 * 
 * The <code>build</code> method has an empty implementation and the
 * <code>filterDataset</code> method applies the filter separately to each
 * instance.
 * 
 * 
 * @author Thomas Abeel
 */
public abstract class AbstractFilter implements DatasetFilter, InstanceFilter {

    public void build(Dataset data) {
        // do nothing
    }

    public void filter(Dataset data) {
        for (Instance i : data)
            filter(i);

    }

    public abstract void filter(Instance inst);

}
