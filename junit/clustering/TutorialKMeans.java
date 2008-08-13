/**
 * %SVN.HEADER%
 */
package junit.clustering;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
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

	@Test
	public void testKMean() {
		try {
			/* Load a dataset */
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
			/* Create a new instance of the KMeans algorithm, with no options specified */
			Clusterer km = new KMeans();
			/* Cluster the data, it will be returned as an array of data sets, with each dataset representing a cluster */
			Dataset[] clusters = km.cluster(data);
			System.out.println("Cluster count: " + clusters.length);

		} catch (IOException e) {
			Assert.assertTrue(false);
		}
	}
}
