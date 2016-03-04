/**
 * %SVN.HEADER%
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
 * 
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
     * Applies this filter to an instance 
     * 
     * @param inst
     *            the instance to apply this filter to
     * @return the modified instance
     */
    public void filter(Instance inst);


}
