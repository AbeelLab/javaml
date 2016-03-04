/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.tools.DatasetTools;

/**
 * E_1 from the Zhao 2001 paper 
 * 
 * XXX DOC 
 * 
 * Distance measure has to be
 * CosineSimilarity 
 * 
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */

public class TraceScatterMatrix implements ClusterEvaluation {

    /**
     * XXX DOC
     */
    private DistanceMeasure dm = new CosineSimilarity();

    /**
     * XXX DOC
     */
    public double score(Dataset[] clusters) {
        Instance [] clusterCentroid= new Instance[clusters.length];
        Instance overAllCentroid;
        int [] clusterSizes = new int[clusters.length];
       
        // calculate centroids of each cluster
        for (int i = 0; i < clusters.length; i++) {
            clusterCentroid[i] = DatasetTools.average(clusters[i]);
            clusterSizes[i]=clusters[i].size();
        }

        // calculate centroid all instances
        // firs put all cluster back together
        Dataset data = new DefaultDataset();
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].size(); j++) {
                data.add(clusters[i].instance(j));
            }
        }
        overAllCentroid = DatasetTools.average(data);
        // calculate trace of the between-cluster scatter matrix.
        double sum = 0;
        for (int i = 0; i < clusters.length; i++) {
            double cos = dm.measure(clusterCentroid[i], overAllCentroid);
            sum += cos * clusterSizes[i];
        }
        return sum;
    }

    /**
     * XXX DOC
     */
    public boolean compareScore(double score1, double score2) {
        // should be minimized
        return score2 < score1;
    }
}
