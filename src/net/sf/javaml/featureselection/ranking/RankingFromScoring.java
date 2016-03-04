/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection.ranking;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.featureselection.FeatureRanking;
import net.sf.javaml.featureselection.FeatureScoring;
import net.sf.javaml.utils.ArrayUtils;

/**
 * Creates an attribute ranking from an attribute evaluation technique.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class RankingFromScoring implements FeatureRanking {

    private int[] ranking=null;

    private FeatureScoring ae;

    public RankingFromScoring(FeatureScoring ae) {
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
    	if(ranking==null)
    		throw new TrainingRequiredException();
        return ranking[attIndex];
    }

    @Override
    public int noAttributes() {
    	if(ranking==null)
    		throw new TrainingRequiredException();
       return ranking.length;
        
    }

}
