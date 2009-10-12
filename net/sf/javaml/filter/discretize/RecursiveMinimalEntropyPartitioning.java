/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.discretize;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import net.sf.javaml.classification.bayes.AbstractBayesianClassifier;
import net.sf.javaml.classification.bayes.AbstractBayesianClassifier_compact;
import net.sf.javaml.classification.bayes.ClassCounter;
import net.sf.javaml.classification.bayes.ClassCounter_compact;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.filter.AbstractFilter;

/**
 * A filter that discretizes a range of numeric attributes in the data set into
 * nominal attributes. This discretization process is recursive and entropy
 * based.
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
public class RecursiveMinimalEntropyPartitioning extends AbstractFilter {

	private Hashtable<Integer, Hashtable<Double, ClassCounter>> featureName_HT;
	private Hashtable<Integer, Hashtable<Double, ClassCounter_compact>> featureName_HT_compact;
	private AbstractBayesianClassifier_compact abc_compact;
	private AbstractBayesianClassifier abc;
	private double[] workingfValues;
	private int IndexFirstElementInCurrentBin;
	private int IndexLastElementInCurrentBin;
	private int numFeatures;
	private boolean memoryMode;
	private int initialCap;
	private Hashtable<Integer, Vector<Double>> featureName_HT_Discretized;
	private Vector<Double> borders;
	private boolean sparse;

	public RecursiveMinimalEntropyPartitioning(boolean sparse) {
		this.sparse = sparse;
		// standard time mode (memory vs time cost mode)
		this.memoryMode = false;
	}

	public void setMemoryMode(boolean m) {
		memoryMode = m;
	}

	// building process
	public void build(Dataset data) {
		determineBorders(data, sparse);
	}

	

	/**
	 * determine borders for every feature
	 * 
	 * @param data
	 *            dataset
	 * @param sparse
	 *            sparseness of dataset
	 */
	private void determineBorders(Dataset data, boolean sparse) {

		// for discretisation we need a list of all features and all
		// featurevalues
		abc_compact = new AbstractBayesianClassifier_compact(true, false,
				sparse);
		abc_compact.buildClassifier(data);
		featureName_HT_compact = abc_compact.getFeatureTable_compact();
		numFeatures = featureName_HT_compact.size();
		initialCap = ((int) Math.ceil(numFeatures / 0.75) + 10);
		featureName_HT_Discretized = new Hashtable<Integer, Vector<Double>>(
				initialCap);

		for (Object key : featureName_HT_compact.keySet()) {
			workingfValues = new double[featureName_HT_compact.get(key)
					.keySet().size()];
			int featureName = (Integer) key;
			int index = 0;
			for (Object value : featureName_HT_compact.get(key).keySet()) {
				Double featureValue = (Double) value;
				workingfValues[index] = featureValue;
				index++;
			}

			Arrays.sort(workingfValues);
			borders = new Vector<Double>();
			calculateBins(featureName);
			Collections.sort(borders);
			featureName_HT_Discretized.put(featureName, borders);
		}

	}

	/**
	 * determine borders for one feature recusively
	 * 
	 * @param FN
	 *            feature name
	 */
	private void calculateBins(int FN) {
		// start with whole bin
		IndexFirstElementInCurrentBin = 0;
		IndexLastElementInCurrentBin = workingfValues.length - 1;
		gainCheck(FN, IndexFirstElementInCurrentBin,
				IndexLastElementInCurrentBin);
	}

	private void gainCheck(int FN, int IndexFirstElementInCurrentBin,
			int IndexLastElementInCurrentBin) {
		// border check (at least 2 elements)
		if (IndexLastElementInCurrentBin - IndexFirstElementInCurrentBin >= 1) {

			Border resultBorder = calcLowestEntropyFunction(FN,
					IndexFirstElementInCurrentBin, IndexLastElementInCurrentBin);
			double LEF = resultBorder.getlowestLEF();
			HashMap<Integer, Double> classfreqBin = calcClassLabelFreqInS(FN,
					IndexFirstElementInCurrentBin, IndexLastElementInCurrentBin);
			int N = IndexLastElementInCurrentBin
					- IndexFirstElementInCurrentBin + 1;
			double EntS = calcEntropy(classfreqBin);
			double gain = EntS - LEF;
			int k = classfreqBin.size();
			int k1 = resultBorder.getK1();
			int k2 = resultBorder.getK2();
			double deltaLEF = ((Math.log(Math.pow(3, k) - 2)) / Math.log(2))
					- (k * EntS - k1 * resultBorder.getEntropyS1() - k2
							* resultBorder.getEntropyS2());
			double RightMember = ((Math.log(N - 1) / Math.log(2)) / N);
			RightMember += (deltaLEF / N);

			if (gain >= RightMember) {
				// left and right from border
				borders.add(resultBorder.getTmin());
				gainCheck(FN, IndexFirstElementInCurrentBin, resultBorder
						.getTminIndex() - 1);
				gainCheck(FN, resultBorder.getTminIndex(),
						IndexLastElementInCurrentBin);
			}

		}

	}

	/**
	 * Calculate Lowest Entropy Function
	 * 
	 * @param FN
	 *            feature name
	 * @param leftborder
	 *            begin of range
	 * @param rightborder
	 *            end of range
	 */

	private Border calcLowestEntropyFunction(int FN, int leftborder,
			int rightborder) {
		// f.e. 3 elements so index 0,1,2 so interval 0-1 , 1-2, 2-3
		// if border is behind first element so index 1 the left bin consists of
		// 1 and the right bin consist of 3-1=2
		Border border = new Border();
		double lowestLEF = Double.MAX_VALUE;
		// starts at 1(after element at index 0) so borderindex=left border of
		// element
		for (int borderindex = leftborder + 1; borderindex <= rightborder; borderindex++) {
			int sizeBinLeft = borderindex - leftborder;
			int sizeBinRight = rightborder - borderindex + 1;
			HashMap<Integer, Double> classfreqBinS1 = calcClassLabelFreqInS(FN,
					leftborder, borderindex - 1);
			HashMap<Integer, Double> classfreqBinS2 = calcClassLabelFreqInS(FN,
					borderindex, rightborder);
			double EntS1 = calcEntropy(classfreqBinS1);
			double EntS2 = calcEntropy(classfreqBinS2);
			double LEF = ((double) (sizeBinLeft) / (double) (sizeBinLeft + sizeBinRight))
					* (EntS1);
			LEF += ((double) sizeBinRight)
					/ ((double) (sizeBinLeft + sizeBinRight)) * (EntS2);
			if (LEF < lowestLEF) {
				lowestLEF = LEF;
				border.setlowestLEF(LEF);
				border.setTMin(workingfValues[borderindex]);
				border.setTMinIndex(borderindex);
				border.setEntS1(EntS1);
				border.setEntS2(EntS2);
				border.setK1(classfreqBinS1.size());
				border.setK2(classfreqBinS2.size());

			}
		}
		return border;
	}

	/**
	 * Collect frequencies as input for entropy calculation
	 * 
	 * @param FN
	 *            feature name
	 * @param leftborder
	 *            begin of range
	 * @param rightborder
	 *            end of range
	 */
	private HashMap<Integer, Double> calcClassLabelFreqInS(int FN,
			int leftborder, int rightborder) {
		// iterate all elements in interval and check for all classes if there
		// is a count
		HashMap<Integer, Double> classfreqBin = new HashMap<Integer, Double>();

		for (int i = leftborder; i <= rightborder; i++) {
			double FV = workingfValues[i];
			double[] counterTable = abc_compact.getFeatureTable_compact().get(
					FN).get(FV).getCounterTable();
			for (int j = 0; j < counterTable.length; j++) {
				if (counterTable[j] > 0) {
					if (!classfreqBin.containsKey(j)) {
						classfreqBin.put(j, counterTable[j]);
					} else {
						classfreqBin.put(j, classfreqBin.get(j)
								+ counterTable[j]);
					}
				}
			}
		}
		return classfreqBin;

	}

	/**
	 * Entropy calculation
	 * 
	 */
	private double calcEntropy(HashMap<Integer, Double> classfreqBin) {

		int numOfClassInstances = 0;
		for (Object key : classfreqBin.keySet()) {
			numOfClassInstances += classfreqBin.get(key);
		}

		double entropy = 0.0;
		for (Object cl : classfreqBin.keySet()) {
			double pClass = ((double) classfreqBin.get(cl) / (double) numOfClassInstances);
			if (pClass != 0.0) {
				entropy += pClass * (Math.log(pClass) / Math.log(2));
			}
		}
		if (entropy == 0.0) {
			return entropy;
		}
		return -1.0 * entropy;

	}

	// filter
	@Override
	public void filter(Instance instance) {
		if (featureName_HT_Discretized == null)
			throw new TrainingRequiredException();

		for (Object key : instance.keySet()) {
			double repVal = calcReplacementValue((Integer) key, instance
					.value((Integer) key));
			instance.put((Integer) key, repVal);
		}

	}

	@Override
	public void filter(Dataset ddata) {

		// we build another bc because of possible folds (validationdata !=
		// trainingdata) as filter argument
		// so not every sampleid is avaliable so index out of bound errors...
		// so we need updated list of available samples and values

		if (memoryMode) {
			// doesnt need the memory usage of AbstractBayesianClassifier
			// but will be slower
			for (Instance inst : ddata) {
				filter(inst);
			}
		} else {
			// faster solution: every unqiue value -> only one calculation of
			// replacement value
			// but needs a list of all sample-ids so needs memory

			abc = new AbstractBayesianClassifier(true, false, sparse);
			abc.buildClassifier(ddata);
			// for discretisation we need a list of all features and all
			// featurevalues
			featureName_HT = abc.getFeatureTable();
			for (Integer key : featureName_HT.keySet()) {
				int FN = key;
				for (Object value : featureName_HT.get(key).keySet()) {
					Double FV = (Double) value;
					// not adding zero values again)
					if ((!sparse) || (FV != 0.0)) {
						double repVal = calcReplacementValue(FN, FV);
						Vector<Integer> v = featureName_HT.get(FN).get(FV)
								.getClassInstanceIDLists();
						Iterator it = v.iterator();
						while (it.hasNext()) {
							int instanceID = (Integer) it.next();
							ddata.get(instanceID - 1).put(key, repVal);
						}
					}

				}

			}
		}

	}

	/**
	 * Determine discrete bin to map on continuous value
	 * 
	 * @param FN
	 *            feature name
	 * @param FV
	 *            feature value
	 */

	private double calcReplacementValue(int FN, double FV) {
		if (!featureName_HT_Discretized.containsKey(FN)) {
			// with folds it could be not all features are yet discovered (with
			// sparse dataset)

			return FV;
		} else {

			Vector fvalues = featureName_HT_Discretized.get(FN);
			int bin = 0;
			boolean ok = true;
			if (!fvalues.isEmpty()) {

				while ((ok) && (FV >= (Double) fvalues.get(bin))) {

					// +1 for next bin index +1 for relative to size
					if ((bin + 2) > fvalues.size()) {
						ok = false;
					}// for new bin at the end
					bin++;
				}
			}
			return (double) bin;
		}

	}

}
