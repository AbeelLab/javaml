/**
 * InstanceFilter.java
 *
 * %SVN.HEADER%
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Instance;

/**
 * The interface for filters that can be applied on an
 * {@link net.sf.javaml.core.Instance} without the need for a reference
 * {@link net.sf.javaml.core.Dataset}.
 * 
 * When applying a filter to an instance it may modify the instance and will
 * return the modified version of the instance.
 * 
 * {@jmlSource}
 * 
 * @see Instance
 * @see Dataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface InstanceFilter {

    /**
     * Applies this filter to an instance and return the modified instance.
     * 
     * @param inst
     *            the instance to apply this filter to
     * @return the modified instance
     */
    public Instance filterInstance(Instance inst);

    /**
     * Tries to apply the reverse of this filter to the instance to restore the
     * original instance.
     * 
     * @throws UnsupportedOperationException
     *             if this filter does not work reverse
     * @param inst
     *            the instance to revert to its original
     * @return the original instance
     */
    public Instance unfilterInstance(Instance inst);
}
