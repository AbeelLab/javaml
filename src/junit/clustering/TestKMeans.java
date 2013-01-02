/**
 * %SVN.HEADER%
 */
package junit.clustering;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestKMeans {

	/**
	 * Test endless loop
	 */
	@Test
	public void testEndless() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				SparseInstance i1 = new SparseInstance(2);
				SparseInstance i2 = new SparseInstance(2);
				i1.put(0, 1d);
				i2.put(1, 1d);
				Dataset dataset = new DefaultDataset();
				dataset.add(i1);
				dataset.add(i2);
				KMeans cluster = new KMeans(2, 1);
				cluster.cluster(dataset);

			}
		});
		t.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* If it is still alive, it is endlessly looping */
		Assert.assertFalse(t.isAlive());
		

	}

	/**
	 * Tests the k-means algorithm with default parameter settings.
	 */
	@Test
	public void testKMean() {
		try {
			/* Load a dataset */
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
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
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
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
