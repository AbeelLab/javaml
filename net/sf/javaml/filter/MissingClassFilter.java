/**
 * MissingClassFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

/**
 * Filters all instances from a data set that have their class value not set
 * 
 * {@jmlSource}
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class MissingClassFilter implements DatasetFilter {

    public void build(Dataset data) {
        // do nothing, requires no training

    }

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        for (Instance i : data)
            if (i.isClassSet())
                out.add(i);
        return out;
    }

}
