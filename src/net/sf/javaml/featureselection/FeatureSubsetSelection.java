/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

import java.util.Set;

/**
 * Interface for all attribute subset selection algorithms. An attribute subset
 * selection algorithms consists of two parts. First an attribute evaluation
 * function that score an attribute and second a search algorithm to look for
 * the best combination of attributes.
 * 
 * @author Thomas Abeel
 * 
 */
public interface FeatureSubsetSelection extends FeatureSelection {

    /**
     * Returns the set of indices of attributes that are selected by the
     * algorithm.
     * 
     * @return set of selected attribute indices
     */
    public Set<Integer> selectedAttributes();
}
