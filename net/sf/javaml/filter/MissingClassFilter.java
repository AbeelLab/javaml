/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Filters all instances from a data set that have their class value not set
 * 
 * 
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

    public void filter(Dataset data) {
        Vector<Instance> toRemove = new Vector<Instance>();
        for (Instance i : data)
            if (i.classValue() == null)
                toRemove.add(i);
        data.removeAll(toRemove);
    }

}
