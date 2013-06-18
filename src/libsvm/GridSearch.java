/**
 * %SVN.HEADER%
 */
package libsvm;

import java.util.Map;

import libsvm.LibSVM;
import libsvm.svm_parameter;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;

/**
 * 
 * Helps finding optimal parameters C and gamma for the <code>LibSVM</code>
 * Support Vector Machine.
 * 
 * @author André Kreienbring
 * @author Thomas Abeel
 * 
 * 
 */
public class GridSearch {
	private final LibSVM classifier;
	private final Dataset dataset;
	private final int folds;
	private final CrossValidation cv;

	private double bestAccuracy;
	private double bestC;
	private double bestGamma;

	private double[] C;
	private double gamma[];
	private svm_parameter svmParameters;

	/**
	 * The GridSearch Class is constructed with the
	 * <code>LibSVM<code> that is later used for 
	 * cross validation.
	 * The cross validation is performed on the given <code>DataSet</code> using
	 * the given number of folds.
	 * 
	 * @param classifier
	 *            The <code>Classifier</code>,
	 * @param dataset
	 *            the <code>DataSet</code>,
	 * @param folds
	 *            and the number of folds used for cross validation.
	 */
	public GridSearch(LibSVM classifier, Dataset dataset, int folds) {
		this.classifier = classifier;
		this.dataset = dataset;
		this.folds = folds;
		this.cv = new CrossValidation(this.classifier);

		this.bestAccuracy = Double.MIN_VALUE;
		this.bestC = Double.MIN_VALUE;
		this.bestGamma = Double.MIN_VALUE;
	}

	/**
	 * This method performs cross validation for each of the given C and gamma
	 * combination. It uses the given <code>svm_parameter</code> Object to
	 * configure the <code>LibSVM<code> Classifier.
	 * If the used Kernel is a linear kernel then the gamma parameters are ignored.
	 * 
	 * The C and gammy parameters that produce the highest accuracy during cross validation are considered to
	 * be the optimal parameters. These values are set in the returned parameter object.
	 * 
	 * @param param
	 *            This settings are used to configure the classifier.
	 * @param C
	 *            An array of values for the C parameter
	 * @param gamma
	 *            An array of values for the gammy parameter
	 * @return The passed in configuration, but with C and gamma set to the
	 *         optimal values.
	 */
	public svm_parameter search(svm_parameter param, double[] C, double[] gamma) {
		this.C = C;
		this.gamma = gamma;
		this.svmParameters = param;

		if (param.kernel_type == svm_parameter.LINEAR && gamma != null) {
			this.gamma = null;
		}

		if (this.gamma != null) {
			// search for C and gamma
			for (int i = 0; i < C.length; i++) {
				for (int j = 0; j < gamma.length; j++) {
					crossValidation(i, j);
				}// gamma
			}// C
		} else {
			// search for C
			for (int i = 0; i < C.length; i++) {
				crossValidation(i, null);
			}// C
		}

		param.C = bestC;

		if (this.gamma != null) {
			param.gamma = bestGamma;
		}

		return param;
	}

	/**
	 * Performes cross validation the C and gamma values that are indicated by
	 * the given index values.
	 * 
	 * @param CIndex
	 *            The index that points to a position of the C-Array
	 * @param gammaIndex
	 *            The index that points to a position of the gamma-Array
	 */
	private void crossValidation(Integer CIndex, Integer gammaIndex) {

		this.svmParameters.C = this.C[CIndex];

		if (gammaIndex != null) {
			this.svmParameters.gamma = this.gamma[gammaIndex];
		}

		this.classifier.setParameters(this.svmParameters);
		double averageAccuracy = 0;

		Map<Object, PerformanceMeasure> perfMap = null;

		// do cross validation

		perfMap = cv.crossValidation(this.dataset, this.folds);

		for (Object o : perfMap.keySet()) {

			PerformanceMeasure pm = perfMap.get(o);
			averageAccuracy += pm.getAccuracy();
		}

		averageAccuracy /= perfMap.keySet().size();
		perfMap.clear();

		if (averageAccuracy > this.bestAccuracy) {
			this.bestAccuracy = averageAccuracy;
			this.bestC = this.C[CIndex];

			if (gammaIndex != null) {
				this.bestGamma = this.gamma[gammaIndex];
			}
		}

	}
}
