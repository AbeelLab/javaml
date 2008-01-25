/**
 * RELIEF.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.attribute.eval;

import java.util.Random;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.ManhattanDistance;
import net.sf.javaml.filter.normalize.DatasetNormalizeMidrange;

/**
 * Implementation of the RELIEF attribute evaluation algorithm.
 * 
 * This implementation is extended to k include more neighbors in calculating the
 * weights of the features.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class RELIEF implements IAttributeEvaluation {

    private double[] weights = null;

    private int numNeighbors;

    public RELIEF(){
        this(1);
    }
    public RELIEF(int numNeighbors){
        this.numNeighbors=numNeighbors;
    }
    
    public void setNumNeigbors(int num) {
        numNeighbors = num;
    }

    public void build(Dataset data) {

        Random rg = new Random();

        // Normalize the data to [0,1]
        DatasetNormalizeMidrange dnm = new DatasetNormalizeMidrange(0.5, 1);
        data = dnm.filterDataset(data);

        weights = new double[data.numAttributes()];
        // number of iterations
        int m = data.size();

        for (int i = 0; i < m; i++) {
            Instance random = data.instance(rg.nextInt(data.size()));
            findNearest(data, random);
            for (int j = 0; j < weights.length; j++)
                weights[j] = weights[j] - diff(j, random, nearestHit) / m + diff(j, random, nearestMiss) / m;
        }
    }

    private Vector<Instance> nearestHit;

    private Vector<Instance> nearestMiss;

    private double diff(int index, Instance a, Vector<Instance> vector) {
        double sum = 0;
        for (Instance b : vector) {
            sum += Math.abs(a.value(index) - b.value(index));
        }
        return sum / vector.size();
    }

    /*
     * Distance measure to find nearest neighbors
     */
    private ManhattanDistance dist = new ManhattanDistance();

    /*
     * Find nearest neighbors that have the same class and that have another
     * class value. The results are stored in the vectors nearestHit and
     * nearestMiss.
     */
    private void findNearest(Dataset data, Instance random) {

        nearestMiss = new Vector<Instance>();
        nearestHit = new Vector<Instance>();
        for (Instance i : data) {
            if (!i.equals(random)) {
                if (i.classValue() == random.classValue()) {
                    nearestHit.add(i);
                    if (nearestHit.size() > numNeighbors)
                        removeFarthest(nearestHit, random);
                } else {
                    nearestMiss.add(i);
                    if (nearestMiss.size() > numNeighbors)
                        removeFarthest(nearestMiss, random);
                }

            }
        }

    }

    /*
     * Removes the element from the vector that is farthest from the supplied
     * element.
     */
    private void removeFarthest(Vector<Instance> vector, Instance supplied) {
        Instance tmp = null;
        double max = 0;// dist.calculateDistance(vector.get(0), supplied);
        for (Instance inst : vector) {
            double tmpDist = dist.calculateDistance(inst, supplied);
            if (tmpDist > max) {
                max = tmpDist;
                tmp = inst;
            }
        }
        vector.remove(tmp);
    }

    public double evaluateAttribute(int attribute) {
        return weights[attribute];
    }

}
