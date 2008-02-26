/**
 * AbstractFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

/**
 * Umbrella class for filters that implements both the
 * {@link net.sf.javaml.filter.InstanceFilter} and
 * {@link net.sf.javaml.filter.DatasetFilter} interfaces. 
 * 
 * The <code>build</code>
 * method has an empty implementation and the <code>filterDataset</code>
 * method applies the filter separately to each instance.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 */
public abstract class AbstractFilter implements DatasetFilter, InstanceFilter {

	public void build(Dataset data) {
	}

	public Dataset filterDataset(Dataset data) {
		Dataset out = new SimpleDataset();
		for (Instance i : data)
			out.add(filterInstance(i));
		return out;
	}

	public abstract Instance filterInstance(Instance inst);

}
