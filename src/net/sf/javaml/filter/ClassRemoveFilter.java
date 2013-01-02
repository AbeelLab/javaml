/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Removes all instances from a data set that have a specific class value
 * 
 * 
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ClassRemoveFilter implements DatasetFilter {

    private Object classValue;

    public ClassRemoveFilter() {
        this(null);
    }

    public ClassRemoveFilter(Object classValue) {
        this.classValue = classValue;
    }

    public void setClass(Object classValue) {
        this.classValue = classValue;
    }

    public void build(Dataset data) {
        // do nothing, requires no training

    }

    public void filter(Dataset data) {
        Vector<Instance> toRemove = new Vector<Instance>();
        for (Instance i : data)
            if (i.classValue().equals(classValue))
                toRemove.add(i);
        data.removeAll(toRemove);
    }

}
