/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

public class TestCrossValidation {
	@Test
	public void testCVRandomForest() {
		/* Load data */
		Dataset data=null;
		try {
			data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		Assert.assertNotNull(data);
		/* Construct KNN classifier */
		Classifier knn = new KNearestNeighbors(5);
		/* Construct new cross validation instance with the KNN classifier */
		CrossValidation cv = new CrossValidation(knn);
		/* Perform 5-fold cross-validation on the data set */
		Map<Object, PerformanceMeasure> p = cv.crossValidation(data);

		System.out.println("Accuracy=" + p.get("Iris-setosa").getAccuracy());
		System.out.println(p);

	}
}
