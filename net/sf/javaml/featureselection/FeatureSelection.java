/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

import net.sf.javaml.core.Dataset;

public interface FeatureSelection {

    /**
     * Build the attribute evaluation on the supplied data set.
     * 
     * Note: This method can change the data set that is supplied to the method!
     * 
     * @param data
     *            data set to train the attribute evaluation algorithm on.
     */
    public void build(Dataset data);

    /**
     * Returns the number of attributes that have been ranked and for which the
     * rank(int index) method will return a rank.
     * 
     * @return the number of ranked attributes
     */
    public int noAttributes();

}
