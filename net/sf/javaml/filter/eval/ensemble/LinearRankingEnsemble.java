/**
 * LinearEnsemble.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.eval.ensemble;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.filter.eval.IAttributeRanking;
import net.sf.javaml.utils.ArrayUtils;

public class LinearRankingEnsemble implements IAttributeRanking {

    private IAttributeRanking[] aes;

    public LinearRankingEnsemble(IAttributeRanking[] aes) {
        this.aes = aes;
    }

    private int[] ranking;

    public void build(Dataset data) {
        int numAtt = data.noAttributes();
        double[] sum = new double[numAtt];
        for (IAttributeRanking ae : aes) {
            ae.build(DatasetTools.randomSample(data, (int) (data.size() * 0.9 + 1)));
            for (int i = 0; i < numAtt; i++)
                sum[i] += ae.getRank(i);
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
