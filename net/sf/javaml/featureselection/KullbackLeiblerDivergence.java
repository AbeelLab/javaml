/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.normalize.NormalizeMidrange;

/**
 * Feature scoring algorithm based on Kullback-Leibler divergence of the value
 * distributions of features.
 * 
 * 
 * Note: Calling the build method is destructive to the supplied data set.
 * 
 * @author Thomas Abeel
 * 
 */
public class KullbackLeiblerDivergence implements AttributeEvaluation {

    /*
     * Positive class. Q in the formula.
     */
    private Object positive;

    public KullbackLeiblerDivergence(Object positiveClass) {
        this.positive = positiveClass;
    }

    private double[] divergence;

    @Override
    public void build(Dataset data) {
        divergence = new double[data.noAttributes()];
        /* Normalize to [0,100[ */
        NormalizeMidrange nm = new NormalizeMidrange(50, 99.99999);
        nm.build(data);
        nm.filter(data);
        /*
         * For probability distributions P and Q of a discrete random variable
         * the K–L divergence of Q from P is defined to be:
         * 
         * D_KL(P|Q)=sum_i(P(i)log(P(i)/Q(i)))
         */
        double maxSum = 0;
        for (int i = 0; i < data.noAttributes(); i++) {
            // System.out.println("Attribute " + i);
            double sum = 0;
            double[] countQ = new double[100];
            double[] countP = new double[100];
            double pCount = 0, qCount = 0;
            for (Instance inst : data) {
                if (inst.classValue().equals(positive)) {
                    countQ[(int) inst.value(i)]++;
                    qCount++;
                } else {
                    countP[(int) inst.value(i)]++;
                    pCount++;
                }
            }

            for (int j = 0; j < countP.length; j++) {
                countP[j] /= pCount;
                countQ[j] /= qCount;
                /*
                 * Probabilities should never be really 0, they can be small
                 * though
                 */
                if (countP[j] == 0)
                    countP[j] = 0.0000001;
                if (countQ[j] == 0)
                    countQ[j] = 0.0000001;
                sum += countP[j] * Math.log(countP[j] / countQ[j]);
            }
            divergence[i] = sum;
            /* Keep track of highest value */
            if (sum > maxSum)
                maxSum = sum;
        }
        /* Normalize to [0,1] */
        for (int i = 0; i < data.noAttributes(); i++) {
            divergence[i] /= maxSum;
        }

    }

    @Override
    public double score(int attribute) {
        return divergence[attribute];
    }

}
