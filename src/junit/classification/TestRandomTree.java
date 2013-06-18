/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestRandomTree {
	@Test
	public void testRFDestructiveConstruction() {
		// Show that a data set can be modified during training
		Dataset data = new DefaultDataset();
		data.add(new DenseInstance(new double[] { 1, 2 }, "hallo"));
		data.add(new DenseInstance(new double[] { 4, 2 }, "hallo"));
		data.add(new DenseInstance(new double[] { 1, 1 }, "hallo"));
		System.out.println("Loader: " + data.classes());
		RandomTree rt = new RandomTree(1, new Random());
		rt.buildClassifier(data);
		Assert.assertEquals(0, data.size());

	}

	@Test
	public void testRT() {

		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/colon.csv.gz"), 0, ",");
			System.out.println("Loader: " + data.classes());
			RandomTree knn = new RandomTree(5, new Random());
			CrossValidation cv = new CrossValidation(knn);
			System.out.println("Java-ML-0:"
					+ cv.crossValidation(data, 5, new Random(10)));

		} catch (IOException e) {
			Assert.assertTrue(false);
		}

	}

}
