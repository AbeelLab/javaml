/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * XXX DOC
 * 
 * @author Thomas Abeel
 * 
 */
public class IterativeFarthestFirst implements Clusterer {
    /**
     * XXX DOC
     */
    private DistanceMeasure dm;

    /**
     * XXX DOC
     */
    private ClusterEvaluation ce;

    /**
     * XXX DOC
     */
    private int kMin, kMax;

    /**
     * default constructor
     * @param ClusterEvaluation ce
     * 
     */
    public IterativeFarthestFirst(ClusterEvaluation ce){
        this(2,6, new EuclideanDistance(),ce);
    }
    /**
     * XXX DOC
     * 
     * @param kMin
     * @param kMax
     * @param DistanceMeasure dm
     * @param ClusterEvaluation ce
     */
    public IterativeFarthestFirst(int kMin, int kMax, DistanceMeasure dm, ClusterEvaluation ce) {
        this.kMin = kMin;
        this.kMax = kMax;
        this.dm = dm;
        this.ce = ce;
    }

    /**
     * XXX DOC
     */
    public Dataset[] cluster(Dataset data) {

        FarthestFirst ff = new FarthestFirst(kMin, dm);
        Dataset[] bestClusters = ff.cluster(data);
        double bestScore = ce.score(bestClusters);

        for (int i = kMin + 1; i <= kMax; i++) {
            ff = new FarthestFirst(i, dm);
            Dataset[] tmp = ff.cluster(data);
            double tmpScore = ce.score(tmp);
            if (ce.compareScore(bestScore, tmpScore)) {
                bestScore = tmpScore;
                bestClusters = tmp;
            }
        }
        return bestClusters;
    }

}
