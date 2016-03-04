/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

import net.sf.javaml.core.Dataset;

/**
 * Top-level interface for feature selection algorithms. There are three main
 * types of features selection: (i) feature scoring, (ii) feature ranking and
 * (iii) feature subset selection. Feature scoring is the most general method
 * and can be converted in the latter two, while feature ranking can only be
 * turned into feature subset selection methods.
 * 
 * Each type of feature selection has its own interface that inherits from this
 * one.
 * 
 * @see net.sf.javaml.featureselection.FeatureScoring
 * @see net.sf.javaml.featureselection.FeatureRanking
 * @see net.sf.javaml.featureselection.FeatureSubsetSelection
 * 
 * @author Thomas Abeel
 * 
 */
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
     * Returns the number of attributes that have been ranked, scored or
     * selected.
     * 
     * @return the number of ranked, scored or selected attributes
     */
    public int noAttributes();

}
