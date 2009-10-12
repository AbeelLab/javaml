/**
 * %SVN.HEADER%
 */
package tutorials.classification;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import tutorials.TutorialData;

/**
 * Tutorial for Naive Bayes classifier
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class TutorialNaiveBayes {

	public static void main(String[] args) throws Exception {

		/* Load a data set */
		Dataset data = TutorialData.IRIS.load();

		/* Discretize through EqualWidtBinning */
		EqualWidthBinning eb = new EqualWidthBinning(20);
		System.out.println("Start discretisation");
		eb.build(data);
		Dataset ddata = data.copy();
		eb.filter(ddata);

		boolean useLaplace = true;
		boolean useLogs = true;
		Classifier nbc = new NaiveBayesClassifier(useLaplace, useLogs, false);
		nbc.buildClassifier(data);

		Dataset dataForClassification = TutorialData.IRIS.load();

		/* Counters for correct and wrong predictions. */
		int correct = 0, wrong = 0;

		/* Classify all instances and check with the correct class values */
		for (Instance inst : dataForClassification) {
			eb.filter(inst);
			Object predictedClassValue = nbc.classify(inst);
			Object realClassValue = inst.classValue();
			if (predictedClassValue.equals(realClassValue))
				correct++;
			else {
				wrong++;

			}

		}
		System.out.println("correct " + correct);
		System.out.println("incorrect " + wrong);

	}

}
