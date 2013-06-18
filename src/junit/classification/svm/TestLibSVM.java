/**
 * TestLibSVM.java
 *
 * %SVN.HEADER%
 */
package junit.classification.svm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import libsvm.LibSVM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;
/**
 * Test case for LibSVM.java
 * 
 * @author Thomas Abeel
 *
 */
public class TestLibSVM {

	@Test
	public void testLibSVM() {
		try {
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4, "\t");
			LibSVM ls = new LibSVM();
			// ls.buildClassifier(data);

			ls.buildClassifier(data);
			data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4, "\t");
			int t = 0, f = 0;
			for (Instance i : data)
				if (i.classValue().equals(ls.classify(i)))
					t++;
				else
					f++;
			System.out.println("Correct: " + t);
			System.out.println("Wrong: " + f);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testLibSVMDistance() {
		try {
			Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4, "\t");
			LibSVM ls = new LibSVM();
			// ls.buildClassifier(data);

			ls.buildClassifier(data);
			data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4, "\t");
			for (Instance i : data) {
				double[] val = ls.rawDecisionValues(i);
				System.out.println(val[0]);
				assertEquals(1, val.length);
			}

		} catch (IOException e) {
			fail();
		}

	}

	@Test
	public void testLibSVMBupa() {
		try {
			PrintWriter out = new PrintWriter("bupa010.txt");
			Dataset data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
			LibSVM ls = new LibSVM();
			// ls.buildClassifier(data);

			ls.buildClassifier(data);
			data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
			int t = 0, f = 0;

			for (Instance i : data) {
				out.println(ls.classify(i));
				if (i.classValue().equals(ls.classify(i)))
					t++;
				else
					f++;
			}
			System.out.println("Correct: " + t);
			System.out.println("Wrong: " + f);
			out.close();
			new File("bupa010.txt").delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testLibSVMSparse() {
		try {
			Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.tsv"), 0, " ", ":");
			LibSVM ls = new LibSVM();
			ls.buildClassifier(data);
			data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.tsv"), 0, " ", ":");
			int t = 0, f = 0;
			for (Instance i : data) {
				if (i.classValue().equals(ls.classify(i)))
					t++;
				else
					f++;
				break;
			}

			System.out.println("Correct: " + t);
			System.out.println("Wrong: " + f);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testLibSVMSparse2() {
		try {
			Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/testdata01"), 0, " ", ":");
			System.out.println(data.instance(1));
			System.out.println(data.instance(1).noAttributes());
			System.out.println("noAttributes: " + data.noAttributes());
			LibSVM svm = new LibSVM();
			svm.getParameters().C = 10;
			svm.buildClassifier(data);
			double[] weights = svm.getWeights();
			System.out.println("weights " + weights.length);
			assertEquals(9, weights.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

}
