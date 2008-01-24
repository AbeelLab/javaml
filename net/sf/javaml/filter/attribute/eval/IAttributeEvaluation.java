/**
 * IAttributeEvaluation.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.attribute.eval;

import net.sf.javaml.core.Dataset;

/**
 * Interface for all attribute evaluation methods. Attribute evaluation methods
 * can be used to calculate the worth of a certain attribute. This is
 * interesting for removing attributes with little information to make your
 * algorithms run faster.
 * 
 * @{jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface IAttributeEvaluation {

    /**
     * Build the attribute evaluation on the supplied data set.
     * 
     * @param data
     *            data set to train the attribute evaluation algorithm on.
     */
    public void build(Dataset data);

    /**
     * Evaluate a single attribute. This should return a value between 0 and 1.
     * 
     * The higher the value, the better the feature is.
     * 
     * @param attribute
     *            the index of the attribute to evaluate
     * 
     * @return the worth of that attribute, a value between 0 and 1.
     */
    public double evaluateAttribute(int attribute);

}
