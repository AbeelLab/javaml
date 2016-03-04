/**
 * %SVN.HEADER%
 */
package junit.featureselection;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.FeatureRanking;
import net.sf.javaml.featureselection.ranking.RecursiveFeatureEliminationSVM;
import net.sf.javaml.sampling.Sampling;

import org.junit.Test;

import be.abeel.util.Pair;
/**
 * Test-case for SVM Recursive Feature Elimination
 * 
 * @author Thomas Abeel
 *
 */
public class TestSVM_RFE {
	@Test
	public void testGreedyBackwardEliminationSynthetic() {

		Dataset data = new DefaultDataset();
		for (int i = 0; i < 100; i++) {
			double[] vals = { i / 50, vary(1, 0), Math.random(), vary(5 * (i / 50),1), 1 };
			System.out.println(vals.length);
			Instance inst = new DenseInstance(vals, "" + (i / 50));
			data.add(inst);

		}

		
		FeatureRanking fr = new RecursiveFeatureEliminationSVM(1.0);

		Pair<Dataset, Dataset> split = Sampling.SubSampling.sample(data, (int) (data.size() * 0.5));
		System.out.println("Training feature selection...");
		
		fr.build(split.x().copy());

		for (int i = 0; i < data.noAttributes(); i++)
			System.out.println(i + "\t" + fr.rank(i));
		// System.out.println(ga.selectedAttributes());
		// Assert.assertTrue(ga.selectedAttributes().contains(0));
		// Assert.assertTrue(ga.selectedAttributes().contains(3));

	}

	private double vary(double i, double j) {
		return i + j * Math.random();
	}
}
