/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.Instance;

/**
 * Filter to retain a set of wanted attributes and remove all others
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class RetainAttributes extends AbstractFilter {

	private Set<Integer> toKeep = new HashSet<Integer>();

	private Set<Integer> toRemove = new HashSet<Integer>();

	/**
	 * Construct a filter that retains all the attributes with the indices given
	 * in the array as parameter.
	 * 
	 * @param indices
	 *            the indices of the columns that will be retained.
	 */
	public RetainAttributes(int[] indices) {
		for (int i : indices) {
			this.toKeep.add(i);
		}
	}

	/**
	 * Construct a filter that retains all the attributes with the indices given
	 * in the array as parameter.
	 * 
	 * @param indices
	 *            the indices of the columns that will be retained.
	 */
	public RetainAttributes(Set<Integer> indices) {
		this.toKeep.addAll(indices);
	}

	@Override
	public void filter(Instance instance) {
		if (toRemove.size() + toKeep.size() != instance.noAttributes())
			buildRemove(instance.noAttributes());
		instance.removeAttributes(toRemove);
	}

	private void buildRemove(int noAttributes) {
		toRemove.clear();
		for (int i = 0; i < noAttributes; i++) {
			if (!toKeep.contains(i)) {
				toRemove.add(i);
			}
		}

	}
}
