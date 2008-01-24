/**
 * SymmetricalUncertainty.java
 *
 * %SVN.HEADER%
 * 
 * Based on work by Mark Hall
 */

package net.sf.javaml.filter.attribute.eval;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.discretize.EqualFrequencyBinning;
import net.sf.javaml.utils.ContingencyTables;

/**
 * Implements the Symmetrical Uncertainty (SU) evaluation method for attributes.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 *
 */
public class SymmetricalUncertaintyAttributeEvaluation implements IAttributeEvaluation {

    private Dataset training;

    public void build(Dataset data) {
        // TODO use more advanced binning technique
        AbstractFilter discretize = new EqualFrequencyBinning();
        discretize.build(data);
        training = discretize.filterDataset(data);
    }

    /**
     * Evaluates an individual attribute by measuring the symmetrical
     * uncertainty between it and the class.
     * 
     * @param attribute
     *            the index of the attribute to be evaluated
     * @return the symmetrical uncertainty
     */
    public double evaluateAttribute(int attribute) {
        int ii, jj;
        int ni = training.numValues(attribute) + 1;
        int nj = training.numClasses() + 1;
        double[][] counts = new double[ni][nj];
        for (Instance inst : training) {
            ii = (int) inst.value(attribute);
            jj = (int) inst.classValue();
            counts[ii][jj]++;
        }
        return ContingencyTables.symmetricalUncertainty(counts);
    }
}
