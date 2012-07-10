/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.tools.InstanceTools;

/**
 * Removes all instances that have missing values.
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RemoveMissingValue implements DatasetFilter {

    public void build(Dataset data) {
        // do nothing

    }

    public void filter(Dataset data) {

        Vector<Instance> toRemove = new Vector<Instance>();
        for (Instance i : data) {
            if (InstanceTools.hasMissingValues(i))
                toRemove.add(i);
        }
        for (Instance i : toRemove)
            data.remove(i);

    }
}
