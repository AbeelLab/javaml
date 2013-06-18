/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.NormalizedEuclideanDistance;

/**
 * Provides the density-based-spatial-scanning clustering algorithm.
 * 
 * @author Thomas Abeel
 * 
 */
public class DensityBasedSpatialClustering extends AbstractDensityBasedClustering implements Clusterer {

    /**
     * Specifies the radius for a range-query
     */
    private double epsilon;

    /**
     * Specifies the density (the range-query must contain at least minPoints
     * instances)
     */
    private int minPoints;

    /**
     * Holds the current clusterID
     */
    private int clusterID;

    /**
     * Creates a density based clusterer with default parameters. Epsilon = 0.1,
     * minpoints = 6 and a normalized version of the euclidean distance.
     */
    public DensityBasedSpatialClustering() {
        this(0.1, 6);
    }

    /**
     * Create a new Density based clusterer with the provided parameters.
     * 
     * @param epsilon
     *            epsilon range query parameter
     * @param minPoints
     *            the minimum number of points that should fall within the
     *            epsilon range query
     * 
     */
    public DensityBasedSpatialClustering(double epsilon, int minPoints) {
        this(epsilon, minPoints, null);
    }

    /**
     * Create a new Density based clusterer with the provided parameters.
     * 
     * @param epsilon
     *            the epsilon value for the epsilon range query to determining
     *            whether the density is high engou
     * @param minPoints
     *            the minimum number of points that should fall within the
     *            epsilon range query.
     * @param dm
     *            the distance measure to use for the epsilon range query.
     */
    public DensityBasedSpatialClustering(double epsilon, int minPoints, DistanceMeasure dm) {
        this.dm = dm;
        this.epsilon = epsilon;
        this.minPoints = minPoints;
    }

    /**
     * Assigns this dataObject to a cluster or remains it as NOISE
     * 
     * @param instance
     *            The DataObject that needs to be assigned
     * @return true, if the DataObject could be assigned, else false
     */
    private boolean expandCluster(DataObject dataObject) {
        HashSet<DataObject> usedSeeds = new HashSet<DataObject>();
        List<DataObject> seedList = epsilonRangeQuery(epsilon, dataObject);
        usedSeeds.addAll(seedList);

        /** dataObject is NO coreObject */
        if (seedList.size() < minPoints) {
            // System.out.println("This is noise...");
            dataObject.clusterIndex = DataObject.NOISE;
            return false;
        }

        // System.out.println("Object is core object");
        /** dataObject is coreObject, it has sufficient neighboring points */
        for (int i = 0; i < seedList.size(); i++) {
            DataObject seedListDataObject = seedList.get(i);
            /* Label seedListDataObject with the current clusterID */
            seedListDataObject.clusterIndex = clusterID;
            if (seedListDataObject.equals(dataObject)) {
                seedList.remove(i);
                i--;
            }
        }

        // System.out.println("Seedlist is labeled and pruned");
        /** Iterate the seedList of the startDataObject */
        // for (int j = 0; j < seedList.size(); j++) {
        while (seedList.size() > 0) {
            DataObject seedListDataObject = seedList.get(0);
            List<DataObject> seedListDataObject_Neighbourhood = epsilonRangeQuery(epsilon, seedListDataObject);

            /** seedListDataObject is coreObject */
            if (seedListDataObject_Neighbourhood.size() >= minPoints) {
                for (int i = 0; i < seedListDataObject_Neighbourhood.size(); i++) {
                    DataObject p = seedListDataObject_Neighbourhood.get(i);
                    if (p.clusterIndex == DataObject.UNCLASSIFIED || p.clusterIndex == DataObject.NOISE) {
                        if (p.clusterIndex == DataObject.UNCLASSIFIED) {
                            if (!usedSeeds.contains(p)) {
                                seedList.add(p);
                                usedSeeds.add(p);
                            }

                        }

                    }
                    p.clusterIndex = clusterID;
                }
            }
            seedList.remove(0);

        }

        return true;
    }

    /* Reference to the original data set */
    private Dataset originalData = null;

    @Override
    public Dataset[] cluster(Dataset data) {
        this.originalData = data;
        if (dm == null) {
            dm = new NormalizedEuclideanDistance(this.originalData);
        }
        this.clusterID = 0;
        dataset = new Vector<DataObject>();
        for (int i = 0; i < data.size(); i++) {
            dataset.add(new DataObject(data.instance(i)));

        }

        Collections.shuffle(dataset);// make clustering algorithm random
        ArrayList<Dataset> output = new ArrayList<Dataset>();
        for (DataObject dataObject : dataset) {
            if (dataObject.clusterIndex == DataObject.UNCLASSIFIED) {
                if (expandCluster(dataObject)) {
                    /* Extract cluster here */
                    /* Cluster ids may be overwritten in further iterations */
                    output.add(extract(clusterID));
                    clusterID++;
                }
            }
        }

        return output.toArray(new Dataset[0]);

    }

    /* Extract a cluster from the DataObject vector */
    private Dataset extract(int clusterID) {
        Dataset cluster = new DefaultDataset();
        for (DataObject dataObject : dataset) {
            if (dataObject.clusterIndex == clusterID)
                cluster.add(dataObject.instance);

        }
        return cluster;
    }

}
