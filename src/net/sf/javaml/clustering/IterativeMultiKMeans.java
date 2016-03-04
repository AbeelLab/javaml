/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * This class implements an extension of KMeans, combining Iterative- en
 * MultiKMeans. SKM will be run several iterations with a different k value,
 * starting from kMin and increasing to kMax, and several iterations for each k.
 * Each clustering result is evaluated with an evaluation score, the result with
 * the best score will be returned as final result.
 * 
 * XXX add reference XXX add pseudo code
 * 
 * @param kMin
 *            minimal value for k (the number of clusters)
 * @param kMax
 *            maximal value for k (the number of clusters)
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
public class IterativeMultiKMeans implements Clusterer {
    /**
     * XXX add doc
     */
    private int kMin, kMax;

    /**
     * XXX add doc
     */
    private int repeats, clusters, iterations;;

    /**
     * XXX add doc
     */
    private ClusterEvaluation ce;

    /**
     * XXX add doc
     */
    private DistanceMeasure dm;

    /**
     * default constructor
     * @param ClusterEvaluation ce
     */
    public IterativeMultiKMeans(ClusterEvaluation ce){
        this(2,6,100,10,new EuclideanDistance(),ce);
    }
    

    /**
     * XXX add doc
     * 
     * @param kMin
     * @param kMax
     * @param ClusterEvaluation ce
     * 
     */
    public IterativeMultiKMeans(int kMin, int kMax, ClusterEvaluation ce) {
    	this(kMin,kMax,100,10,new EuclideanDistance(),ce);
    }
    
    /**
     * XXX add doc
     * 
     * @param kMin
     * @param kMax
     * @param iterations
     * @param repeats
     * @param DistanceMeasure dm
     * @param ClusterEvaluation ce
     */
    public IterativeMultiKMeans(int kMin, int kMax, int iterations, int repeats, DistanceMeasure dm,
            ClusterEvaluation ce) {
        this.kMax = kMax;
        this.kMin = kMin;
        this.iterations = iterations;
        this.repeats = repeats;
        this.dm = dm;
        this.ce = ce;
    }

    /**
     * XXX add doc
     */
    public Dataset[] cluster(Dataset data) {
        KMeans km = new KMeans(kMin, this.iterations, this.dm);
        Dataset[] bestClusters = km.cluster(data);
        for (clusters = kMin + 1; clusters <= kMax; clusters++) {
            double bestScore = this.ce.score(bestClusters);
            for (int i = 0; i < repeats; i++) {
                KMeans km2 = new KMeans(clusters, this.iterations, this.dm);
                Dataset[] tmpClusters = km2.cluster(data);
                double tmpScore = this.ce.score(tmpClusters);
                if (this.ce.compareScore(bestScore, tmpScore)) {
                    bestScore = tmpScore;
                    bestClusters = tmpClusters;
                }
            }
        }
        return bestClusters;
    }
}
