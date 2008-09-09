/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Creates an attribute ranking from an attribute evaluation technique.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RankingFromEvaluation implements AttributeRanking {

    private int[] ranking;

    private AttributeEvaluation ae;

    public RankingFromEvaluation(AttributeEvaluation ae) {
        this.ae = ae;
    }

    public void build(Dataset data) {
    	int noAttributes=data.noAttributes();
        ae.build(data);
        double[] values = new double[noAttributes];
        for (int i = 0; i < values.length; i++)
            values[i] = ae.score(i);

        ranking = new int[values.length];
        int[] order = ArrayUtils.sort(values);
        for (int i = 0; i < order.length; i++) {
            ranking[order[i]] = order.length - i - 1;
        }
    }

    public int rank(int attIndex) {

        return ranking[attIndex];
    }

    @Override
    public int noFeatures() {
       return ranking.length;
        
    }

}
