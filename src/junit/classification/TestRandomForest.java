/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;

import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

import be.abeel.util.TimeInterval;

public class TestRandomForest {
	@Test
	public void testRF3() {
		try {
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
			Classifier rf = new RandomForest(10);
			rf.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testRF2() {
		try {
			System.out.println("Running tests on the Iris data set...");

			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");

			RandomForest forest = new RandomForest(100, false, 3, null);
			crossValidate(forest, data);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void crossValidate(Classifier classifier, Dataset data) {

		Dataset datasets[];
		/*
		 * Outter map: actual class, predictions Inner map: predicted class,
		 * count
		 */
		Map<Object, Map<Object, Integer>> confusionMatrix;

		datasets = data.folds(2, new Random());
		classifier.buildClassifier(datasets[0]);

		// TODO build confusion matrix
	}

	@Test
	public void testRF() {
		try {
			Classifier classifier = new RandomForest(10);
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
			Dataset[] folds = data.folds(10, new Random(System.currentTimeMillis()));
			Map<Object, PerformanceMeasure> out = new HashMap<Object, PerformanceMeasure>();
			for (Object o : data.classes()) {
				out.put(o, new PerformanceMeasure());
			}
			for (int i = 0; i < 10; i++) {
				Dataset validation = folds[i];
				Dataset training = new DefaultDataset();
				for (int j = 0; j < 10; j++) {
					if (j != i)
						training.addAll(folds[j]);

				}

				classifier.buildClassifier(training);

				for (Instance instance : validation) {

					Object prediction = classifier.classify(instance);
					Assert.assertNotNull(prediction);
					if (instance.classValue().equals(prediction)) {// prediction
						// ==class
						for (Object o : out.keySet()) {
							if (o.equals(instance.classValue())) {
								out.get(o).tp++;
							} else {
								out.get(o).tn++;
							}

						}
					} else {// prediction != class
						for (Object o : out.keySet()) {
							/* prediction is positive class */
							if (prediction.equals(o)) {
								out.get(o).fp++;
							}
							/* instance is positive class */
							else if (o.equals(instance.classValue())) {
								out.get(o).fn++;
							}
							/* none is positive class */
							else {
								out.get(o).tn++;
							}

						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}

	}

	@Test
	public void testRF2Performance() {
		long seed = System.currentTimeMillis();
		try {
			Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
			System.out.println("Loader: " + data.classes());
			RandomForest rf2 = new RandomForest(10, false, 20, new Random(seed));
			CrossValidation cv = new CrossValidation(rf2);
			Map<Object, PerformanceMeasure> p = cv.crossValidation(data, 5, new Random(10));
			System.out.println("RF2:" + p);
			System.out.println("RF2:--" + p.get("0").getAccuracy());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new TimeInterval(System.currentTimeMillis() - seed));

	}

	/**
	 * Shows the default usage of the random forest algorithm.
	 */
	@Test
	public void testDefaultRF() {

		try {
			/* Load a data set */
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
			/*
			 * Contruct a RF classifier that uses 5 neighbors to make a
			 * decision.
			 */
			Classifier rf = new RandomForest(50, false, 3, new Random());
			rf.buildClassifier(data);

			/*
			 * Load a data set, this can be a different one, but we will use the
			 * same one.
			 */
			Dataset dataForClassification = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
			/* Counters for correct and wrong predictions. */
			int correct = 0, wrong = 0;
			/* Classify all instances and check with the correct class values */
			for (Instance inst : dataForClassification) {
				Object predictedClassValue = rf.classify(inst);
				Object realClassValue = inst.classValue();
				if (predictedClassValue.equals(realClassValue))
					correct++;
				else
					wrong++;
			}
			System.out.println("Correct predictions  " + correct);
			System.out.println("Wrong predictions " + wrong);

		} catch (IOException e) {
			Assert.assertTrue(false);
		}

	}

	/**
	 * Shows the default usage of the random forest algorithm.
	 */
	@Test
	public void testRFClassDistribution() {

		try {
			/* Load a data set */
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
			/*
			 * Contruct a RF classifier.
			 */
			Classifier rf = new RandomForest(50, false, 3, new Random());
			rf.buildClassifier(data);

			/*
			 * Load a data set, this can be a different one, but we will use the
			 * same one.
			 */
			Dataset dataForClassification = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
			/* Counters for correct and wrong predictions. */
			int correct = 0, wrong = 0;
			/* Classify all instances and check with the correct class values */
			for (Instance inst : dataForClassification) {
				Map<Object, Double> distribution = rf.classDistribution(inst);
				Assert.assertTrue(distribution.size() == 3);
				Object predictedClassValue = rf.classify(inst);
				Object realClassValue = inst.classValue();
				if (predictedClassValue.equals(realClassValue))
					correct++;
				else
					wrong++;

				for (Object o : distribution.keySet()) {
					if (o.equals(predictedClassValue))
						Assert.assertTrue(distribution.get(o) > 0.5);
					else
						Assert.assertTrue(distribution.get(o) < 0.5);
				}
			}
			System.out.println("Correct predictions  " + correct);
			System.out.println("Wrong predictions " + wrong);

		} catch (IOException e) {
			Assert.assertTrue(false);
		}

	}

	@Test
	public void testRFPerformance() {
		long seed = System.currentTimeMillis();
		try {
			Dataset data = FileHandler.loadDataset(new File("devtools/data/colon.csv.gz"), 0, ",");
			System.out.println("Loader: " + data.classes());
			RandomForest rf2 = new RandomForest(10, false, 20, new Random(seed));
			CrossValidation cv = new CrossValidation(rf2);
			Map<Object, PerformanceMeasure> p = cv.crossValidation(data, 5, new Random(10));
			System.out.println("Performance: " + p);
			System.out.println("Accuracy: " + p.get("0").getAccuracy());

		} catch (IOException e) {
			Assert.assertTrue(false);
		}
		System.out.println(new TimeInterval(System.currentTimeMillis() - seed));

	}

}
