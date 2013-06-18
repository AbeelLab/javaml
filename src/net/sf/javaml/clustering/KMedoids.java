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
import net.sf.javaml.tools.DatasetTools;

/**
 * Implementation of the K-medoids algorithm. K-medoids is a clustering
 * algorithm that is very much like k-means. The main difference between the two
 * algorithms is the cluster center they use. K-means uses the average of all
 * instances in a cluster, while k-medoids uses the instance that is the closest
 * to the mean, i.e. the most 'central' point of the cluster.
 * 
 * Using an actual point of the data set to cluster makes the k-medoids
 * algorithm more robust to outliers than the k-means algorithm.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class KMedoids implements Clusterer {
	/* Distance measure to measure the distance between instances */
	private DistanceMeasure dm;

	/* Number of clusters to generate */
	private int numberOfClusters;

	/* Random generator for selection of candidate medoids */
	private Random rg;

	/* The maximum number of iterations the algorithm is allowed to run. */
	private int maxIterations;

	/**
	 * default constructor
	 */
	public KMedoids() {
		this(4, 100, new EuclideanDistance());
	}

	/**
	 * Creates a new instance of the k-medoids algorithm with the specified
	 * parameters.
	 * 
	 * @param numberOfClusters
	 *            the number of clusters to generate
	 * @param maxIterations
	 *            the maximum number of iteration the algorithm is allowed to
	 *            run
	 * @param DistanceMeasure
	 *            dm the distance metric to use for measuring the distance
	 *            between instances
	 * 
	 */
	public KMedoids(int numberOfClusters, int maxIterations, DistanceMeasure dm) {
		super();
		this.numberOfClusters = numberOfClusters;
		this.maxIterations = maxIterations;
		this.dm = dm;
		rg = new Random(System.currentTimeMillis());
	}

	@Override
	public Dataset[] cluster(Dataset data) {
		Instance[] medoids = new Instance[numberOfClusters];
		Dataset[] output = new DefaultDataset[numberOfClusters];
		for (int i = 0; i < numberOfClusters; i++) {
			int random = rg.nextInt(data.size());
			medoids[i] = data.instance(random);
		}

		boolean changed = true;
		int count = 0;
		while (changed && count < maxIterations) {
			changed = false;
			count++;
			int[] assignment = assign(medoids, data);
			changed = recalculateMedoids(assignment, medoids, output, data);

		}

		return output;

	}

	/**
	 * Assign all instances from the data set to the medoids.
	 * 
	 * @param medoids candidate medoids
	 * @param data the data to assign to the medoids
	 * @return best cluster indices for each instance in the data set
	 */
	private int[] assign(Instance[] medoids, Dataset data) {
		int[] out = new int[data.size()];
		for (int i = 0; i < data.size(); i++) {
			double bestDistance = dm.measure(data.instance(i), medoids[0]);
			int bestIndex = 0;
			for (int j = 1; j < medoids.length; j++) {
				double tmpDistance = dm.measure(data.instance(i), medoids[j]);
				if (dm.compare(tmpDistance, bestDistance)) {
					bestDistance = tmpDistance;
					bestIndex = j;
				}
			}
			out[i] = bestIndex;

		}
		return out;

	}

	/**
	 * Return a array with on each position the clusterIndex to which the
	 * Instance on that position in the dataset belongs.
	 * 
	 * @param medoids
	 *            the current set of cluster medoids, will be modified to fit
	 *            the new assignment
	 * @param assigment
	 *            the new assignment of all instances to the different medoids
	 * @param output
	 *            the cluster output, this will be modified at the end of the
	 *            method
	 * @return the
	 */
	private boolean recalculateMedoids(int[] assignment, Instance[] medoids,
			Dataset[] output, Dataset data) {
		boolean changed = false;
		for (int i = 0; i < numberOfClusters; i++) {
			output[i] = new DefaultDataset();
			for (int j = 0; j < assignment.length; j++) {
				if (assignment[j] == i) {
					output[i].add(data.instance(j));
				}
			}
			if (output[i].size() == 0) { // new random, empty medoid
				medoids[i] = data.instance(rg.nextInt(data.size()));
				changed = true;
			} else {
				Instance centroid = DatasetTools.average(output[i]);
				Instance oldMedoid = medoids[i];
				medoids[i] = data.kNearest(1, centroid, dm).iterator().next();
				if (!medoids[i].equals(oldMedoid))
					changed = true;
			}
		}
		return changed;
	}

}
