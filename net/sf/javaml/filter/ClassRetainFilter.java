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
 * Keeps all instances from a data set that have a specific class value
 * 
 * {@jmlSource}
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ClassRetainFilter implements DatasetFilter {

    private int classValue;

    public ClassRetainFilter() {
        this(0);
    }

    public ClassRetainFilter(int classValue) {
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
            if (i.classValue() == classValue)
                out.add(i);
        return out;
    }

}
