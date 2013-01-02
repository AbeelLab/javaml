/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tutorials.classification;

import net.sf.javaml.classification.bayes.KDependentBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import tutorials.TutorialData;

/**
 * 
 * Tutorial for K Dependent Bayes classifier
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class TutorialKDependentBayes {

	public static void main(String[] args) throws Exception {

		/* Load a data set */
		Dataset data = TutorialData.IRIS.load();

		EqualWidthBinning eb = new EqualWidthBinning(3);
		System.out.println("Start discretisation");
		eb.build(data);
		Dataset ddata = data.copy();
		eb.filter(ddata);

		double treshold = 0.0;
		KDependentBayesClassifier nbc = new KDependentBayesClassifier(false,
				treshold, new int[] { 0, 1, 2, 4, 5, 8 });
		nbc.buildClassifier(ddata);

		// for (int n=0;n<5;n++){

		// Algorithm needs to know which Bayesian network (which k value)
		// you need to classify the sample with
		nbc.setcurrentWorkingK(5);
		System.out.println("Start classification:");

		/*
		 * Load a data set, this can be a different one, but we will use the
		 * same one.
		 */

		Dataset dataForClassification = TutorialData.IRIS.load();

		/* Counters for correct and wrong predictions. */
		int correct = 0, wrong = 0;
		/* Classify all instances and check with the correct class values */

		double cnt = 0;

		double overallF = dataForClassification.size();

		for (Instance inst : dataForClassification) {
			System.out.println(((++cnt) / overallF * 100) + "%");

			eb.filter(inst);

			Object predictedClassValue = nbc.classify(inst);
			Object realClassValue = inst.classValue();
			// System.out.println("realClassValue "+ realClassValue);
			if (predictedClassValue.equals(realClassValue))
				correct++;
			else {
				wrong++;

			}

		}
	}
}
