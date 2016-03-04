/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.tools.DatasetTools;

/**
 * TODO uitleg I_2 from Zhao 2001
 * 
 * @author Andreas De Rijcke
 */
public class SumOfCentroidSimilarities implements ClusterEvaluation {

    /**
     * XXX DOC
     */
	private DistanceMeasure dm=new CosineSimilarity();
    /**
     * XXX DOC
     */
	public double score(Dataset[] datas) {

		Instance[] centroids = new Instance[datas.length];
		for (int i = 0; i < datas.length; i++) {
			centroids[i] = DatasetTools.average(datas[i]);
		}
		double sum = 0;
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				double error = dm.measure(datas[i].instance(j),
						centroids[i]);
				sum += error;
			}
		}
		return sum;
	}
    /**
     * XXX DOC
     */
	public boolean compareScore(double score1, double score2) {
		// TODO check right condition or code
		// should be minimized; in paper: maxed!!
		return score2 < score1;
	}
}
