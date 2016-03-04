/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import java.util.Set;

import net.sf.javaml.core.Instance;

public class RemoveAttributes implements InstanceFilter{

    private Set<Integer> indices;

    /**
     * Construct a remove filter that removes all the attributes with the
     * indices given in the array as parameter.
     * 
     * @param indices
     *            the indices of the columns that will be removed.
     */
    public RemoveAttributes(java.util.Set<Integer> indices) {
        this.indices = indices;
    }

    @Override
    public void filter(Instance instance) {
        instance.removeAttributes(indices);
    }

    
}
