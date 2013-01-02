/**
 * %SVN.HEADER%
 */
package tutorials.filter;

import java.io.IOException;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.KDependentBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.TwoBinMinimalEntropyPartitioning;
import tutorials.TutorialData;

/**
 * 
 * Tutorial Two Bin Minimal Entropy Partitioning
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class Tutorial2BinMinimalEntropyPartitioning {

	public static void main(String[] args) throws Exception {

		Dataset ddata = TutorialData.IRIS.load();

		TwoBinMinimalEntropyPartitioning rmep = new TwoBinMinimalEntropyPartitioning(
				false);
		rmep.build(ddata);
		// Dataset ddata=data.copy();
		// int index =

		rmep.filter(ddata);

		int[] kparents = new int[6];
		// k ideal for cancer data
		kparents[0] = 0;
		kparents[1] = 1;
		kparents[2] = 2;
		kparents[3] = 3;
		kparents[4] = 4;
		kparents[5] = 10;

		double treshold = 0.0;
		KDependentBayesClassifier nbc = new KDependentBayesClassifier(false,
				treshold, kparents);
		nbc.buildClassifier(ddata);
		for (int n = 0; n < kparents.length; n++) {
			// st.start();
			nbc.setcurrentWorkingK(kparents[n]);
			int[] res = classifyNewInstance(nbc, kparents[n], rmep);
			// st.stop();

			int result = kparents[n];
			System.out.println("---------------------\nKDB-" + result);
			System.out.println("correct: " + res[0]);
			System.out.println("incorrect: " + res[1]);
		}

	}

	private static int[] classifyNewInstance(Classifier nbc, int workingK,
			TwoBinMinimalEntropyPartitioning rmep) throws IOException {
		/*
		 * Load a data set, this can be a different one, but we will use the
		 * same one.
		 */

		// Result r = new Result();
		// absentFeatureValueTest
		// fileindex=7;
		Dataset dataForClassification = TutorialData.IRIS.load();
		// instance needs to be discretised too ! Later boolean for both
		// (learning set and predicting set)
		// Discretize()
		// try categorizing 1 first
		//

		/* Counters for correct and wrong predictions. */
		int correct = 0, wrong = 0;
		/* Classify all instances and check with the correct class values */

		for (Instance inst : dataForClassification) {

			rmep.filter(inst);
			Object predictedClassValue = nbc.classify(inst);
			// System.out.println("predictedClassValue "+
			// predictedClassValue);
			Object realClassValue = inst.classValue();
			// System.out.println("realClassValue "+ realClassValue);
			if (predictedClassValue.equals(realClassValue))
				correct++;
			else
				wrong++;

		}

		return new int[] { correct, wrong };

	}

}
