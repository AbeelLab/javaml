/**
 * %SVN.HEADER%
 */
package junit.clustering;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

/**
 * This tutorial shows how to use a clustering algorithm to cluster a data set.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialKMeans {

	/**
	 * Tests the k-means algorithm with default parameter settings.
	 */
	@Test
	public void testKMean() {
		try {
			/* Load a dataset */
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
			/*
			 * Create a new instance of the KMeans algorithm, with no options
			 * specified. By default this will generate 4 clusters.
			 */
			Clusterer km = new KMeans();
			/*
			 * Cluster the data, it will be returned as an array of data sets,
			 * with each dataset representing a cluster
			 */
			Dataset[] clusters = km.cluster(data);
			System.out.println("Cluster count: " + clusters.length);

		} catch (IOException e) {
			Assert.assertTrue(false);
		}
	}

	/**
	 * Tests the k-means algorithm with user-specified parameter settings.
	 */
	@Test
	public void testKMeanWithParameters() {
		try {
			/* Load a dataset */
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
			/*
			 * Create a new instance of the KMeans algorithm, with all options
			 * specified. This instance of the k-means algorithm will generate 3
			 * clusters, will run for 100 iteration and will use the euclidean
			 * distance.
			 */
			Clusterer km = new KMeans(3, 100, new EuclideanDistance());
			/*
			 * Cluster the data, it will be returned as an array of data sets,
			 * with each data set representing a cluster
			 */
			Dataset[] clusters = km.cluster(data);
			System.out.println("Cluster count: " + clusters.length);

		} catch (IOException e) {
			Assert.assertTrue(false);
		}
	}
}
