/**
 * %SVN.HEADER%
 */
package junit.libsvm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;
import libsvm.LibSVM;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestWeights {

	@Test
	public void testWeights() {
		try {
			Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/4gate.txt"), 0, " ", ":");
			LibSVM svm = new LibSVM();
			svm.buildClassifier(data);
			double[] w1 = svm.getWeights();

			data = FileHandler.loadSparseDataset(new File("devtools/data/4gate_original.txt"), 0, " ", ":");
			svm = new LibSVM();
			svm.buildClassifier(data);
			double[] w2 = svm.getWeights();
			
			data = FileHandler.loadSparseDataset(new File("devtools/data/4gatesparse.txt"), 0, " ", ":");
			svm = new LibSVM();
			svm.buildClassifier(data);
			double[] w3 = svm.getWeights();
			
			data = FileHandler.loadDataset(new File("devtools/data/4gatedense.txt"), 0, " ");
			svm = new LibSVM();
			svm.buildClassifier(data);
			double[] w4 = svm.getWeights();

			
			
			double tmp=w2[0];
			w2[0]=w2[1];
			w2[1]=tmp;
			
			System.out.println(Arrays.toString(w1));
			System.out.println(Arrays.toString(w2));
			System.out.println(Arrays.toString(w3));
			System.out.println(Arrays.toString(w4));
			
			Assert.assertTrue(Arrays.equals(w1,w2));
			Assert.assertTrue(Arrays.equals(w1,w3));
			Assert.assertTrue(Arrays.equals(w1,w4));
			
			
			
			System.out.println(EvaluateDataset.testDataset(svm, data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
}
