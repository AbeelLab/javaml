/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Filter to retain all wanted features and removal of all other features.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class RetainAttributes extends AbstractFilter {

    private Set<Integer> toKeep = new HashSet<Integer>();

    private List<Integer> toRemove = new ArrayList<Integer>();

    /**
     * Construct a filter that retains all the attributes with the indices given
     * in the array as parameter.
     * 
     * @param indices
     *            the indices of the columns that will be removed.
     */
    public RetainAttributes(int[] indices) {
        for (int i : indices) {
            this.toKeep.add(i);
        }
    }

    @Override
    public void filter(Dataset data) {
        for (Instance i : data)
            filter(i);
    }

    @Override
    public void filter(Instance instance) {
        if (toRemove.size() + toKeep.size() != instance.noAttributes())
            buildRemove(instance.noAttributes());
        for (int r : toRemove) {
            instance.removeAttribute(r);
        }
    }

    private void buildRemove(int noAttributes) {
        toRemove.clear();
        for (int i = 0; i < noAttributes; i++) {
            if (!toKeep.contains(i)) {
                toRemove.add(0, i);
            }
        }

    }
}
