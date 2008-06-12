/**
 * RemoveAttributes.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class RemoveAttributes extends AbstractFilter {

 
    private List<Integer> indices;

    /**
     * Construct a remove filter that removes all the attributes with the
     * indices given in the array as parameter.
     * 
     * @param indices
     *            the indices of the columns that will be removed.
     */
    public RemoveAttributes(java.util.Set<Integer> indices) {
        this.indices = new Vector<Integer>();
        this.indices.addAll(indices);
        Collections.sort(this.indices);
       
    }

    @Override
    public void filterDataset(Dataset data) {
        for (Instance i : data)
            filterInstance(i);
    }

    @Override
    public void filterInstance(Instance instance) {
        for (int i = indices.size() - 1; i >= 0; i--) {
            instance.removeAttribute(i);
        }

    }

}
