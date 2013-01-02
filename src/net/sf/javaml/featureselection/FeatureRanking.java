/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;


/**
 * Interface for algorithms that can generate an attribute ranking. Higher
 * values in the ranking means that the attribute is less informative or is
 * worth less.
 * 
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface FeatureRanking extends FeatureSelection {

    /**
     * Get the ranking of the given attribute. The first rank is 0 and is the
     * best. The higher the rank, the worse the attribute.
     * 
     * @param attIndex
     *            the index of the attribute to rank
     * @return the rank of the attribute
     */
    public int rank(int attIndex);

}
