/**
 * %SVN.HEADER%
 */
package tutorials.filter;

import java.io.IOException;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.KDependentBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.FourBinMinimalEntropyPartitioning;
import tutorials.TutorialData;

/**
 * Tutorial Four Bin Minimal Entropy Partitioning
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class Tutorial4BinMinimalEntropyPartitioning {

	public static void main(String[] args) throws Exception {

		Dataset data =TutorialData.IRIS.load();
	
		/* Load a data set */

		FourBinMinimalEntropyPartitioning rmep = new FourBinMinimalEntropyPartitioning(
				false);
		rmep.build(data);

		rmep.filter(data);

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
		nbc.buildClassifier(data);

		boolean test_1sample = false;

		for (int n = 0; n < kparents.length; n++) {
			// st.start();
			nbc.setcurrentWorkingK(kparents[n]);
			int[] res = classifyNewInstance(nbc, kparents[n],rmep);
			// st.stop();
			// kres.get(kparents[n]).setTime(st.toString());
			int result = kparents[n];
			System.out.println("---------------------\nKDB-" + result);
			System.out.println("correct: " + res[0]);
			System.out.println("incorrect: " + res[1]);
		}

	}

//	private static void compareDS(Dataset data, Dataset discdata) {
//
//		int index = 0;
//		for (Instance inst : data) {
//
//			// ddata.get(index3).setClassValue(inst.classValue());
//			System.out.println("BEFORE: " + inst);
//			System.out.println("AFTER!: " + discdata.get(index));
//			System.out.println();
//			index++;
//		}
//
//		AbstractBayesianClassifier abc = new AbstractBayesianClassifier(true,
//				false, sparsedata.get(fileindex));
//		abc.buildClassifier(data);
//
//		AbstractBayesianClassifier abc_disc = new AbstractBayesianClassifier(
//				true, false, TutorialData.IRIS.load());
//		abc_disc.buildClassifier(discdata);
//
//		for (Object key : abc.getFeatureTable().keySet()) {
//			System.out.println("#VALUES FOR FEATURE(=" + key + ") BEFORE: "
//					+ abc.getFeatureTable().get(key).keySet().size());
//			System.out.println("#VALUES FOR FEATURE(=" + key + ") AFTER!: "
//					+ abc_disc.getFeatureTable().get(key).keySet().size());
//			System.out.println();
//
//		}
//		System.out.println();
//
//	}

	private static int[] classifyNewInstance(Classifier nbc, int workingK,FourBinMinimalEntropyPartitioning rmep) throws IOException {
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

		// if (test_1sample) {
		// Instance inst = dataForClassification.instance(1);
		// rmep.filter(inst);
		// Object predictedClassValue = nbc.classify(inst);
		// // System.out.println("predictedClassValue "+ predictedClassValue);
		// Object realClassValue = inst.classValue();
		// // System.out.println("realClassValue "+ realClassValue);
		// if (predictedClassValue.equals(realClassValue))
		// correct++;
		// else
		// wrong++;
		//
		// } else {
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
