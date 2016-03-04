/**
 * %SVN.HEADER%
 */
package tutorials.filter;

import java.io.IOException;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.KDependentBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.ThreeBinMinimalEntropyPartitioning;
import tutorials.TutorialData;

/**
 * Tutorial Three Bin Minimal Entropy Partitioning
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class Tutorial3BinMinimalEntropyPartitioning {

	public static void main(String[] args) throws Exception {
		;

		Dataset data = TutorialData.IRIS.load();

		/* Load a data set */
		// data = inputFiles[fileindex];
		// start discretisation
		ThreeBinMinimalEntropyPartitioning rmep = new ThreeBinMinimalEntropyPartitioning(
				false);
		rmep.build(data);
		// Dataset ddata=data.copy();
//		int index = 0;
		// if (sparse) {
		// // start fix
		// // fix for sparse datasets but should be fixed in JML now
		// for (Instance inst : ddata) {
		//
		// ddata.get(index).setClassValue(inst.classValue());
		// if (!ddata.classes().contains(inst.classValue())) {
		// ddata.classes().add(inst.classValue());
		// }
		// index++;
		// }
		// // end fix
		// }
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
//			st.start();
			nbc.setcurrentWorkingK(kparents[n]);
			int[] res = classifyNewInstance(nbc, test_1sample, kparents[n],rmep);

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
//				false,);
//		abc.buildClassifier(data);
//
//		AbstractBayesianClassifier abc_disc = new AbstractBayesianClassifier(
//				true, false, sparsedata.get(fileindex));
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

	private static int[] classifyNewInstance(Classifier nbc,
			boolean test_1sample, int workingK,
			ThreeBinMinimalEntropyPartitioning rmep) throws IOException {

		Dataset dataForClassification = TutorialData.IRIS.load();

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
		return new int[]{correct,wrong};
	}

}
