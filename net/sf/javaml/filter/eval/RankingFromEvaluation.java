/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.eval;

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
public class RankingFromEvaluation implements IAttributeRanking {

    private int[] ranking;

    private IAttributeEvaluation ae;

    public RankingFromEvaluation(IAttributeEvaluation ae) {
        this.ae = ae;
    }

    public void build(Dataset data) {
    	int noAttributes=data.noAttributes();
        ae.build(data);
        double[] values = new double[noAttributes];
        for (int i = 0; i < values.length; i++)
            values[i] = ae.evaluateAttribute(i);

        ranking = new int[values.length];
        int[] order = ArrayUtils.sort(values);
        for (int i = 0; i < order.length; i++) {
            ranking[order[i]] = order.length - i - 1;
        }
    }

    public int getRank(int attIndex) {

        return ranking[attIndex];
    }

}
