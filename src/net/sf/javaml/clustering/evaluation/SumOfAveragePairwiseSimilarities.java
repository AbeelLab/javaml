/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * I_1 from the Zhao 2001 paper
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */
public class SumOfAveragePairwiseSimilarities implements ClusterEvaluation{
    
    /**
     * XXX DOC
     */
    private DistanceMeasure dm=new CosineSimilarity();
    /**
     * XXX DOC
     */
    public double score(Dataset[] datas) {
       
        double sum=0;
        for(int i=0;i<datas.length;i++){
            double tmpSum=0;
            for(int j=0;j<datas[i].size();j++){
                for(int k=0;k<datas[i].size();k++){
                    double error=dm.measure(datas[i].instance(j),datas[i].instance(k));
                    tmpSum+=error;
                }  
            }
            sum+=tmpSum/datas[i].size();
        }
       return sum;
    }
    /**
     * XXX DOC
     */
    public boolean compareScore(double score1, double score2) {
        // TODO check right condition or code
    	//should be minimized; in paper: maxed!!
        return score2 < score1;
    }

}
