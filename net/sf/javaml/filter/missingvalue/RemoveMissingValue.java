/**
 * RemoveMissingValue.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.missingvalue;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.filter.DatasetFilter;

/**
 * Removes all instances that have missing values.
 * 
 * @{jmlSource}
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

    public Dataset filterDataset(Dataset data) {
        Dataset output = new SimpleDataset();
        for (Instance i : data) {
            if (!InstanceTools.hasMissingValues(i))
                output.add(i);
        }
        return output;
    }

}
