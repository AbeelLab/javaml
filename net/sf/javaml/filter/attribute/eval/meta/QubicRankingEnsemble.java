/**
 * LinearEnsemble.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.attribute.eval.meta;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.filter.attribute.eval.IAttributeRanking;
import net.sf.javaml.utils.ArrayUtils;

public class QubicRankingEnsemble implements IAttributeRanking {

    private IAttributeRanking[] aes;

    public QubicRankingEnsemble(IAttributeRanking[] aes) {
        this.aes = aes;
    }

    private int[] ranking;

    public void build(Dataset data) {
        int numAtt = data.numAttributes();
        double[] sum = new double[numAtt];
        for (IAttributeRanking ae : aes) {
            ae.build(DatasetTools.randomSample(data, (int) (data.size() * 0.9 + 1)));
            for (int i = 0; i < numAtt; i++)
                sum[i] += ae.getRank(i) * ae.getRank(i) * ae.getRank(i);
        }
        toRank(sum);

    }

    // @Test
    // public void testRescale() {
    // double[] tmp = { 1000, 2000, 1500, 4000, 500 };
    // rescale(tmp);
    // System.out.println(Arrays.toString(ranking));
    // }

    private void toRank(double[] sum) {
        // int[]r= ArrayUtils.sort(ranking);
        int[] order = ArrayUtils.sort(sum);
        ranking = new int[order.length];
        // som[i] bevat de som van alle ranks die attribuut i gehaald heeft
        //
        // lage values zijn beter
        for (int i = 0; i < order.length; i++) {
            ranking[order[i]] = i;
        }
    }

    public int getRank(int attIndex) {
       return ranking[attIndex];
    }

}
