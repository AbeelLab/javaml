/**
 * ClassFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

/**
 * Removes all instances from a data set that have a specific class value
 * 
 * {@jmlSource}
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ClassRemoveFilter implements DatasetFilter {

    private int classValue;

    public ClassRemoveFilter() {
        this(0);
    }
    
    public ClassRemoveFilter(int classValue) {
        this.classValue = classValue;
    }

    public void setClass(int classValue) {
        this.classValue = classValue;
    }

    public void build(Dataset data) {
        // do nothing, requires no training

    }

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        for (Instance i : data)
            if (i.classValue() != classValue)
                out.add(i);
        return out;
    }

}
