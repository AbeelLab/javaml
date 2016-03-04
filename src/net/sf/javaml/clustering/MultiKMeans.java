/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * This class implements an extension of KMeans (SKM). SKM will be run
 * several iterations with the same k value. Each clustering result is evaluated
 * with an evaluation score, the result with the best score will be returned as
 * final result.
 * 
 * XXX add references
 * XXX add pseudocode
 * 
 * @param clusters
 *            the number of clusters
 * @param iterations
 *            the number of iterations in SKM
 * @param repeats
 *            the number of SKM repeats
 * @param dm
 *            distance measure used for internal cluster evaluation
 * @param ce
 *            clusterevaluation methode used for internal cluster evaluation
 * 
 * @author Thomas Abeel
 * @author Andreas De Rijcke
 * 
 */
public class MultiKMeans implements Clusterer {
    /**
     * XXX add doc
     */
    private int repeats, clusters, iterations;
    /**
     * XXX add doc
     */
	private DistanceMeasure dm;
    /**
     * XXX add doc
     */
	private ClusterEvaluation ce;
    
	/**
     * default constructor
     * @param ClusterEvaluation ce
     */
    public MultiKMeans(ClusterEvaluation ce){
        this(4,100,10,new EuclideanDistance(),ce);
    }
    /**
     * XXX add doc
     * 
     * @param clusters
     * @param iterations
     * @param repeats
     * @param DistanceMeasure dm
     * @param ClusterEvaluation ce
     */
	public MultiKMeans(int clusters, int iterations, int repeats,
			DistanceMeasure dm, ClusterEvaluation ce) {
		this.clusters = clusters;
		this.iterations = iterations;
		this.repeats = repeats;
		this.dm = dm;
		this.ce = ce;

	}
    /**
     * XXX add doc
     */
	public Dataset[] cluster(Dataset data) {
		KMeans km = new KMeans(this.clusters, this.iterations,
				this.dm);
		Dataset[] bestClusters = km.cluster(data);
		double bestScore = this.ce.score(bestClusters);
		for (int i = 0; i < repeats; i++) {
			Dataset[] tmpClusters = km.cluster(data);
			double tmpScore = this.ce.score(tmpClusters);
			if (this.ce.compareScore(bestScore, tmpScore)) {
				bestScore = tmpScore;
				bestClusters = tmpClusters;
			}
		}
		return bestClusters;
	}
}
