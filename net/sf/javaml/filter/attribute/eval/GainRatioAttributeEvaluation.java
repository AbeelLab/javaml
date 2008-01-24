/**
 * GainRationAttributeEvaluation.java
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
 * 
 * Implements the gain ration attribute evaluation technique. This technique
 * evaluation the worth of an attribute by the measuring the gain ration with
 * respect to the class.
 * 
 * GainR(Class, Attribute) = (H(Class) - H(Class | Attribute)) / H(Attribute).
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class GainRatioAttributeEvaluation implements IAttributeEvaluation {

    private Dataset training;

    /**
     * Evaluates an individual attribute by measuring the gain ratio of the
     * class given the attribute.
     * 
     * @param attribute
     *            the index of the attribute to be evaluated
     * @return the gain ratio
     */
    public double evaluateAttribute(int attribute) {
        int ni = training.numValues(attribute) + 1;
        int nj = training.numClasses() + 1;
        double[][] counts = new double[ni][nj];

        // Fill the contingency table
        for (int i = 0; i < training.size(); i++) {
            Instance inst = training.instance(i);

            int ii = (int) inst.value(attribute);
            int jj = (int) inst.classValue();

            counts[ii][jj]++;
        }

        return ContingencyTables.gainRatio(counts);
    }

    public void build(Dataset data) {
        AbstractFilter discretize = new EqualFrequencyBinning();
        discretize.build(data);
        training = discretize.filterDataset(data);
    }
}
