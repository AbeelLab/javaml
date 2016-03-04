/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

/**
 * Interface for all attribute evaluation methods. Attribute evaluation methods
 * can be used to calculate the worth of a certain attribute. This is
 * interesting for removing attributes with little information to make your
 * algorithms run faster.
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface FeatureScoring extends FeatureSelection {

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
    public double score(int attribute);

}
