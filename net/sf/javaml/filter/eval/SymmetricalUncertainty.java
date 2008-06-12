/**
 * SymmetricalUncertainty.java
 *
 * %SVN.HEADER%
 * 
 * Based on work by Mark Hall
 */

package net.sf.javaml.filter.eval;

import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.AbstractFilter;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
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
public class SymmetricalUncertainty implements IAttributeEvaluation {

    private Dataset training;

    private int bins = 10;

    public void build(Dataset data) {
        AbstractFilter discretize = new EqualWidthBinning(bins);
        discretize.build(data);
        discretize.filterDataset(data);
        this.training = data;
        

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
        // int ii, jj;
        // int ni = training.numValues(attribute) + 1;
        // int nj = training.numClasses() + 1;
        double[][] counts = new double[bins][training.classes().size()];
        List<Object> classes = new Vector<Object>();
        classes.addAll(training.classes());
//        System.out.println(training);
        for (Instance inst : training) {
//            ii = (int) inst.value(attribute);
//            jj = (int) inst.classValue();
            if((int) inst.value(attribute)>=bins){
                System.err.println("Exceeding bins: "+bins);
            }
            if(classes.indexOf(inst.classValue())>=training.classes().size())
                System.err.println("Exceeding classes: "+training.classes().size());
            counts[(int) inst.value(attribute)][classes.indexOf(inst.classValue())]++;
        }
        return ContingencyTables.symmetricalUncertainty(counts);
    }
}
