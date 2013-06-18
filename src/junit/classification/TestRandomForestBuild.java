/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;

import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import be.abeel.util.TimeInterval;

public class TestRandomForestBuild {
	@Test
	public void testRFNPEConstructor() {

		RandomForest rf=new RandomForest(4);
		Dataset data;
		try {
			data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
			rf.buildClassifier(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
	}

	@Test
	public void testRF2Performance() {
		long seed = System.currentTimeMillis();
		try {
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4, "\t");
			System.out.println("Loader: " + data.classes());
			RandomForest rf2 = new RandomForest(5, false, 2, new Random(seed));
			System.out.println("Building 1");
			rf2.buildClassifier(data.copy());
			System.out.println("Building 2");
			rf2.buildClassifier(data.copy());
			System.out.println("Building 3");
			rf2.buildClassifier(data.copy());
			System.out.println(EvaluateDataset.testDataset(rf2, data));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new TimeInterval(System.currentTimeMillis() - seed));

	}

}
