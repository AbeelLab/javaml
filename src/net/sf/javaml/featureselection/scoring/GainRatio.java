/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection.scoring;

import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.FeatureScoring;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import net.sf.javaml.utils.ContingencyTables;

/**
 * Implements the Gain Ratio evaluation method for attributes.
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @author Mark Hall
 * @author Thomas Abeel
 * 
 */
public class GainRatio implements FeatureScoring {

    private Dataset training;

    private int bins = 10;

    public void build(Dataset data) {
        AbstractFilter discretize = new EqualWidthBinning(bins);
        discretize.filter(data);
        this.training = data;

    }

    /**
     * Evaluates an individual attribute by measuring the gain ratio between it
     * and the class label.
     * 
     * @param attribute
     *            the index of the attribute to be evaluated
     * @return the symmetrical uncertainty
     */
    public double score(int attribute) {
        // int ii, jj;
        // int ni = training.numValues(attribute) + 1;
        // int nj = training.numClasses() + 1;
        double[][] counts = new double[bins][training.classes().size()];
        List<Object> classes = new Vector<Object>();
        classes.addAll(training.classes());
        for (Instance inst : training) {
            // ii = (int) inst.value(attribute);
            // jj = (int) inst.classValue();
            counts[(int) inst.value(attribute)][classes.indexOf(inst.classValue())]++;
        }
        return ContingencyTables.gainRatio(counts);
    }

    @Override
    public int noAttributes() {
        return training.noAttributes();
    }
}
