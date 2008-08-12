/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

import net.sf.javaml.core.Dataset;

/**
 * Interface for algorithms that can generate an attribute ranking. Higher
 * values in the ranking means that the attribute is less informative or is
 * worth less.
 * 
 * {@jmlSource}
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface AttributeRanking {

    /**
     * Build the attribute ranking on the supplied data set.
     * 
     * @param data
     *            data set to train the attribute ranking algorithm on.
     */
    public void build(Dataset data);

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
