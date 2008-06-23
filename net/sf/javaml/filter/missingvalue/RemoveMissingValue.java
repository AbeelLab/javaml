/**
 * RemoveMissingValue.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.filter.DatasetFilter;

/**
 * Removes all instances that have missing values.
 * 
 * {@jmlSource}
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

    public void filterDataset(Dataset data) {

        Vector<Instance> toRemove = new Vector<Instance>();
        for (Instance i : data) {
            if (InstanceTools.hasMissingValues(i))
                toRemove.add(i);
        }
        for (Instance i : toRemove)
            data.remove(i);

    }
}
