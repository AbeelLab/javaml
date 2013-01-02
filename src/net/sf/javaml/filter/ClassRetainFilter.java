/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Keeps all instances from a data set that have a specific class value
 * 
 * 
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ClassRetainFilter implements DatasetFilter {

    private Object classValue;

    public ClassRetainFilter() {
        this("0");
    }

    public ClassRetainFilter(Object classValue) {
        this.classValue = classValue;
    }

    public void setClass(Object classValue) {
        this.classValue = classValue;
    }

    public void build(Dataset data) {
        // do nothing, requires no training

    }

    public void filter(Dataset data) {
        Vector<Instance> tor = new Vector<Instance>();
        for (Instance i : data)
            if (!i.classValue().equals(classValue))
                tor.add(i);
        for (Instance i : tor)
            data.remove(i);
    }
}
