/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering;

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Cluster data using the FarthestFirst algorithm.<br/> <br/> For more
 * information see:<br/> <br/> Hochbaum, Shmoys (1985). A best possible
 * heuristic for the k-center problem. Mathematics of Operations Research.
 * 10(2):180-184.<br/> <br/> Sanjoy Dasgupta: Performance Guarantees for
 * Hierarchical Clustering. In: 15th Annual Conference on Computational Learning
 * Theory, 351-363, 2002.<br/> <br/> Notes:<br/> - works as a fast simple
 * approximate clusterer<br/> - modelled after KMeans, might be a useful
 * initializer for it <p/>
 * 
 * BibTeX:
 * 
 * <pre>
 * &#64;article{Hochbaum1985,
 *    author = {Hochbaum and Shmoys},
 *    journal = {Mathematics of Operations Research},
 *    number = {2},
 *    pages = {180-184},
 *    title = {A best possible heuristic for the k-center problem},
 *    volume = {10},
 *    year = {1985}
 * }
 * 
 * &#64;inproceedings{Dasgupta2002,
 *    author = {Sanjoy Dasgupta},
 *    booktitle = {15th Annual Conference on Computational Learning Theory},
 *    pages = {351-363},
 *    publisher = {Springer},
 *    title = {Performance Guarantees for Hierarchical Clustering},
 *    year = {2002}
 * }
 * </pre>
 * 
 * <p/>
 * 
 * @author Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)
 * @author Thomas Abeel
 * 
 */
public class FarthestFirst implements Clusterer {

    /**
     * training instances, not necessary to keep, could be replaced by centroids
     * where needed for header info
     */
    private Dataset data;

    /**
     * number of clusters to generate
     */
    private int m_NumClusters = 2;

    /**
     * holds the cluster centroids
     */
    private Instance[] centroids;

    private DistanceMeasure dm;

    /**
     * XXX DOC
     * 
     * @param minDistance
     * @param selected
     * @param center
     */
    private void updateMinDistance(double[] minDistance, boolean[] selected, Instance center) {
        for (int i = 0; i < selected.length; i++)
            if (!selected[i]) {
                double d = dm.measure(center, data.instance(i));
                if (d < minDistance[i])
                    minDistance[i] = d;
            }
    }

    /**
     * XXX DOC
     * 
     * @param minDistance
     * @param selected
     * @return
     */
    private int farthestAway(double[] minDistance, boolean[] selected) {
        double maxDistance = -1.0;
        int maxI = -1;
        for (int i = 0; i < selected.length; i++)
            if (!selected[i])
                if (maxDistance < minDistance[i]) {
                    maxDistance = minDistance[i];
                    maxI = i;
                }
        return maxI;
    }

    /**
     * XXX doc
     */
    private Random rg;

    
    /**
     * default constructor
     */
    public FarthestFirst(){
        this(4,new EuclideanDistance());
    }
    /**
     * XXX DOC
     * 
     * @param numClusters
     * @param DistanceMeasure dm
     */
    public FarthestFirst(int numClusters, DistanceMeasure dm) {
        m_NumClusters = numClusters;
        this.dm = dm;
        this.rg = new Random(System.currentTimeMillis());
    }

    /**
     * XXX DOC
     */
    public Dataset[] cluster(Dataset data) {
        this.data = data;
        centroids = new Instance[m_NumClusters];

        int n = data.size();
        boolean[] selected = new boolean[n];
        double[] minDistance = new double[n];

        for (int i = 0; i < n; i++)
            minDistance[i] = Double.MAX_VALUE;

        int firstI = rg.nextInt(n);
        centroids[0] = data.instance(firstI);
        selected[firstI] = true;

        updateMinDistance(minDistance, selected, data.instance(firstI));

        if (m_NumClusters > n)
            m_NumClusters = n;

        for (int i = 1; i < m_NumClusters; i++) {
            int nextI = farthestAway(minDistance, selected);
            centroids[i] = data.instance(nextI);
            selected[nextI] = true;
            updateMinDistance(minDistance, selected, data.instance(nextI));
        }

        // put data in clusters using the calculated centroids
        Dataset[] clusters = new Dataset[m_NumClusters];
        for (int i = 0; i < m_NumClusters; i++) {
            clusters[i] = new DefaultDataset();
        }
        for (int i = 0; i < data.size(); i++) {
            Instance inst = data.instance(i);
            double min = dm.measure(inst, centroids[0]);
            int index = 0;
            for (int j = 1; j < m_NumClusters; j++) {
                double tmp = dm.measure(inst, centroids[j]);
                if (tmp < min) {
                    min = tmp;
                    index = j;
                }
            }
            clusters[index].add(inst);
        }
        return clusters;
    }
}
