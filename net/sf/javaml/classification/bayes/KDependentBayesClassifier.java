package net.sf.javaml.classification.bayes;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Implementation of the K Dependent Bayes classification algorithm.
 * 
 * @author Lieven Baeyens
 * 
 */
public class KDependentBayesClassifier extends AbstractBayesianClassifier {

	private double treshold;
	private int[] kparents;
	private int currentWorkingK = 0;
	private int maxkparents;

	/**
	 * Instantiate the KD Bayesian classifier
	 * 
	 * 
	 * @param sparse
	 *            sparseness of dataset
	 * @param treshold
	 *            KDB CMI treshold
	 * @param kparents
	 *            a list of ASC SORTED k values
	 */
	public KDependentBayesClassifier(boolean sparse, double treshold,
			int[] kparents) {
		// laplace and logs are neccesary for KDB
		super(true, true, sparse);
		this.kparents = kparents;
		this.treshold = treshold;
		maxkparents = kparents[0];
		// the kparents list MUST be sorted f.e. 0 1 2 8 15
		for (int i = 0; i < kparents.length; i++) {
			if (kparents[i] > maxkparents) {
				maxkparents = kparents[i];
			}
		}
	}

	/**
	 * Instantiate the KD Bayesian classifier building process.
	 * 
	 * 
	 * @param data
	 *            dataset to build model on
	 */
	@Override
	public void buildClassifier(Dataset data) {
		// same for NB, calculate and store frequencies
		super.buildClassifier(data);
		// calculate (Conditional) Mutual Information and build Bayesian network
		// for every k
		buildBayesianNetworks();

	}

	/**
	 * Build Bayesian networks for all k values
	 * 
	 * 
	 */
	private void buildBayesianNetworks() {

		// calculate/store only needed CMI values (=for highest k value)
		System.out.println("Start calculating MI/CMI");
		calculateNeededCMIbyMIorder();

		// fetch MI sorted features and CMI
		Vector<Integer> usedFeatures = trainResult.getUsedFeatures_SortedMI();
		HashMap<Integer, Object[]> ImaxLL = trainResult
				.getBNBB_XiXjinS_SortedCMI();
		System.out.println("Start building BN");
		// BN for k0 will be base for latter networks
		BayesNet BN = trainResult.getBayesNet(kparents[0]).getBN().cloon();
		// will be cloned for all BN
		BN.setIC(numFeatures, initialCap);

		// we have CMI for all k values,now we build iteratively
		// kparents is sorted by el1<el2
		for (int r = 0; r < kparents.length; r++) {

			// KDB 0 has no edges
			if (kparents[r] != 0) {
				// First element doesnt have parent (theory: S list is empty:
				// always o-1 elements in S)
				for (int o = 1; o < usedFeatures.size(); o++) {
					// select feature with highest (next) MI
					Integer Imax = usedFeatures.get(o);
					// retrieve parents of this feature (already sorted by
					// highest needed CMI)
					Object[] nextXjArray = ImaxLL.get(Imax);
					// in theory: m variable
					int endAmountOfEdges = Math.min(kparents[r],
							nextXjArray.length);
					int beginAmountOfEdges;
					// if there is no k=0 so index 0 wont be skipped and index
					// r-1 would be -1 ,must be 0
					if (r == 0) {
						beginAmountOfEdges = 0;
					} else {
						beginAmountOfEdges = kparents[r - 1];
					}
					// if stops at 7 links so index 0-6 so -1
					for (int index = beginAmountOfEdges; index <= endAmountOfEdges - 1; index++) {
						BN.addParent(Imax, (Integer) nextXjArray[index]);
					}
				}
				// clone network to be independent of future edge adding
				trainResult.getBayesNet(kparents[r]).setBN(BN.cloon());
			}
		}// end k
	}

	/**
	 * Calculate/store only needed CMI values (=for highest k value)
	 * 
	 * 
	 */

	private void calculateNeededCMIbyMIorder() {

		// calculate Mutual Information values and store it
		LinkedList<Integer> MI = calculateMutualInformation_Elvira();
		// if we calculate cmi values for highest k we can build all BN with
		// other k values out of it
		// this is the KDB building Bayesian network algorithm: cfr theory

		// filling in vertexes + KDB0
		BayesNet BN = new BayesNet();
		Vector<Integer> usedFeatures = new Vector<Integer>(numFeatures);
		Iterator MIIt = MI.iterator();
		HashMap<Integer, Object[]> ImaxLL = new HashMap<Integer, Object[]>(
				initialCap);
		while ((usedFeatures.size() < MI.size()) && (MIIt.hasNext())) {
			int Imax = (Integer) MIIt.next();
			if (!BN.getNodes().contains(Imax)) {
				BN.addNode(Imax);
			}
			int m = Math.min(usedFeatures.size(), maxkparents);
			// only calculate m highest CMI values out of list N
			// output: sorted features Xj belonging to those highest cmi values
			LinkedList<Integer> usedFeatures_SortedCMIvalueListLL = calculateCMI_Memory(
					Imax, (Vector<Integer>) usedFeatures.clone(), m);
			Object[] usedFeatures_SortedCMIvalueA = usedFeatures_SortedCMIvalueListLL
					.toArray();
			ImaxLL.put(Imax, usedFeatures_SortedCMIvalueA);
			// store list as features not MI values
			usedFeatures.add(Imax);

		}
		trainResult.setBNBB_XiXjinS_SortedCMI(ImaxLL);
		trainResult.setUsedFeatures_SortedMI(usedFeatures);
		// set basic network,topology later when network completed
		for (int r = 0; r < kparents.length; r++) {
			trainResult.setBayesNet(new BayesKSolution(BN, null), kparents[r]);
		}

	}

	// **********************************************************************************************//
	// rx ry
	// Ip(X;Y) = SUM SUM P(X=xi, Y=yj) * log P(X=xi, Y=yj)
	// i=1 j=1
	//
	// rx ry
	// - SUM P(X=xi) * log P(X=xi) - SUM P(Y=yj) * log P(Y=yj)
	// i=1 j=1
	//
	//
	// **********************************************************************************************//
	/**
	 * Calculates the Mutual Information distance of a variable, based on a
	 * multiclass problem
	 * 
	 */
	private LinkedList<Integer> calculateMutualInformation_Elvira() {
		Hashtable<Integer, Hashtable<Double, ClassCounter>> featureName_HT = trainResult
				.getFeatureTable();
		// set initial probabilities to the prior probs

		double[] freq = trainResult.getClassFreqs().clone();
		// Red black tree structure to have constant sorted structure
		// so we can remove lowest cmi values, good for memory
		SortedMap<Double, LinkedList> sMap = new TreeMap<Double, LinkedList>()
				.descendingMap();

		int fncount = 0;
		double probCj;
		double clsum = 0.0;
		// only once
		for (int j = 0; j < numClasses; j++) {
			// avoid probs k because its rounded
			probCj = freq[j] / numInstances;
			if (probCj != 0)
				clsum += (probCj * (Math.log(probCj) / Math.log(10)));
		}
		for (Object key : featureName_HT.keySet()) {
			fncount++;
			int FN = (Integer) key;

			double info = 0;
			int nClasses = numClasses;

			double probXiCj, probXi, numXi;

			for (Object key2 : featureName_HT.get(FN).keySet()) {
				Double FV = (Double) key2;
				probXi = 0.0;

				// probXiCj
				for (int j = 0; j < nClasses; j++) {

					double probXi_temp = featureName_HT.get(FN).get(FV)
							.getCountClass(j);
					probXi += probXi_temp;
					probXiCj = (probXi_temp) / numInstances;
					if (probXiCj != 0)
						info += probXiCj * (Math.log(probXiCj) / Math.log(10));
				}
				// probXi
				probXi /= numInstances;
				if (probXi != 0)
					info = info - (probXi * (Math.log(probXi) / Math.log(10)));

			}
			info -= clsum;

			if (!sMap.containsKey(info)) {
				LinkedList<Integer> list = new LinkedList<Integer>();
				list.add(FN);

				sMap.put(info, list);
			} else {
				sMap.get(info).add(FN);
			}

		}// end loop all FEATURES

		LinkedList<Integer> list2 = new LinkedList<Integer>();

		for (double sc : sMap.keySet()) {
			list2.addAll(sMap.get(sc));
		}
		return list2;
	}// end mutualInformation(Node)>

	/**
	 * Calculates the Conditional Mutual Information of 2 features given a class
	 * 
	 */
	private LinkedList<Integer> calculateCMI_Memory(int FN1,
			Vector<Integer> usedFeatures, int m) {

		Hashtable<Integer, Hashtable<Double, ClassCounter>> featureName_HT = trainResult
				.getFeatureTable();

		int fcnt = 0;
		int allfcnt = 0;
		double[] freq = trainResult.getClassFreqs().clone();
		LinkedList<Integer> usedFeatures_SortedCMIvalueListLL = new LinkedList<Integer>();

		SortedMap<Double, LinkedList> sMap = new TreeMap<Double, LinkedList>()
				.descendingMap();

		double sumXC = 0;
		for (Double FV1 : featureName_HT.get(FN1).keySet()) {

			for (int k = 0; k < numClasses; k++) {
				double pXC = featureName_HT.get(FN1).get(FV1).getCountClass(k)
						/ freq[k];
				if (pXC != 0) {
					sumXC += pXC * (Math.log(pXC) / Math.log(10));
				}
			}
		}

		Iterator it = usedFeatures.iterator();
		while (it.hasNext()) {
			int FN2 = (Integer) it.next();
			double CMIvalue = 0.0;

			double sumXYC = 0;
			double sumYC = 0;
			for (double FV2 : featureName_HT.get(FN2).keySet()) {

				for (int k = 0; k < numClasses; k++) {
					double pYC = featureName_HT.get(FN2).get(FV2)
							.getCountClass(k)
							/ freq[k];
					if (pYC != 0) {
						sumYC += pYC * (Math.log(pYC) / Math.log(10));
					}
					// pXYC
					for (double FV1 : featureName_HT.get(FN1).keySet()) {

						double pXYC = compareConditionalInstanceIDLists(FN1,
								FV1, FN2, FV2, k)
								/ freq[k];
						if (pXYC != 0) {
							sumXYC += pXYC * (Math.log(pXYC) / Math.log(10));
						}
					}

				}
			}

			CMIvalue = (sumXYC - sumXC - sumYC);
			// KDB treshold to eliminate low CMI value features
			if (treshold < CMIvalue) {
				if (!sMap.containsKey(CMIvalue)) {
					LinkedList<Integer> list = new LinkedList<Integer>();
					list.add(FN2);

					sMap.put(CMIvalue, list);
				} else {
					sMap.get(CMIvalue).add(FN2);
				}
				fcnt++;
				allfcnt++;

			}
			// removing redundant and lowest CMI values
			if ((fcnt > (m + 10))) {
				int delAmount = fcnt - m;
				while (delAmount > 0) {
					LinkedList<Integer> list2 = sMap.get(sMap.lastKey());
					if (list2.size() <= delAmount) {
						delAmount -= list2.size();
						fcnt -= list2.size();
						sMap.remove(sMap.lastKey());

					} else {
						list2.removeFirst();
						sMap.put(sMap.lastKey(), list2);
						delAmount--;
						fcnt--;
					}

				}

			}
		}// end FN2 lus

		for (double sc : sMap.keySet()) {
			usedFeatures_SortedCMIvalueListLL.addAll(sMap.get(sc));
		}

		return usedFeatures_SortedCMIvalueListLL;

	}

	/**
	 * Calculates the prob of the testExample being generated by each category
	 * 
	 * @param testExample
	 *            The retrieveInstanceIDList example to be categorized
	 */
	protected HashMap<Object, Double> calculateProbs(Instance inst) {
		System.out.println("Start classification process");
		// classification process KDB, cfr. theory
		HashMap<Object, Double> out = new HashMap<Object, Double>();
		coverAbsentFeatures_And_fill_helpMap(inst);
		// fetch frequencies
		Hashtable<Integer, Hashtable<Double, ClassCounter>> featureName_HT = trainResult
				.getFeatureTable();
		double[] freq = trainResult.getClassFreqs().clone();

		// fetch dependencies for current working k value
		BayesNet BN = trainResult.getBayesNet(currentWorkingK).getBN();
		// get feature list
		Set<Integer> List = BN.getNodes();

		for (int k = 0; k < numClasses; k++) {
			Iterator itrtlc = List.iterator();

			double classScore = fnc.log2(freq[k] / numInstances);
			while (itrtlc.hasNext()) {

				int feature_current = (Integer) itrtlc.next();
				// for laplace
				int numValues = featureName_HT.get(feature_current).size();

				Vector<Integer> parents = BN.getNodeParents(feature_current);
				SortedMap<Integer, LinkedList> sMap = new TreeMap<Integer, LinkedList>();
				Vector<Integer> list_instanceIDs = retrieveInstanceIDList(
						feature_current, getInstValue(feature_current, inst), k);

				double numerator = list_instanceIDs.size();
				// will be overwritten if feature has parents in BN
				double denominator = freq[k];

				if (parents.size() > 0) {

					Vector<Integer> parentList_instanceIDs;
					Iterator it = parents.iterator();

					// if more parent lists,sort by list size
					// so duplicate check algo can go faster
					if ((parents.size() > 3) && (numFeatures > 50)) {

						while (it.hasNext()) {
							int parent = (Integer) it.next();
							int newsize;
							newsize = retrieveInstanceIDList(parent,
									getInstValue(parent, inst), k).size();
							if (!sMap.containsKey(newsize)) {
								LinkedList<Integer> list = new LinkedList<Integer>();
								list.add(parent);
								sMap.put(newsize, list);
							} else {
								sMap.get(newsize).add(parent);
							}
						}
						LinkedList<Integer> list2 = new LinkedList<Integer>();

						for (int sc : sMap.keySet()) {
							list2.addAll(sMap.get(sc));
						}
						// filled parent map

						it = list2.iterator();

					}
					int parent = (Integer) it.next();
					parentList_instanceIDs = retrieveInstanceIDList(parent,
							getInstValue(parent, inst), k);

					// calculate mutual elements in sample-id lists of all
					// parents of current feature
					while (it.hasNext() && parentList_instanceIDs != null) {
						parent = (Integer) it.next();
						parentList_instanceIDs = fnc.cutVectorsSort(
								parentList_instanceIDs, retrieveInstanceIDList(
										parent, getInstValue(parent, inst), k));
					}

					Vector<Integer> newlist = null;
					denominator = parentList_instanceIDs.size();
					if (parentList_instanceIDs != null) {
						newlist = fnc.cutVectorsSort(list_instanceIDs,
								parentList_instanceIDs);
					}
					numerator = newlist.size();

				}
				classScore += fnc
						.log2(((numerator + 1) / (denominator + numValues)));
			}

			out.put(classes[k], classScore);
		}
		out = calcFictionalChances(out);
		return out;
	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		return calculateProbs(instance);
	}

	/**
	 * fetch sample-id lists of all classes for one feature value p(X=x)=(SUM
	 * Lists).size()
	 * 
	 * @param FN
	 *            feature name
	 * @param FV
	 *            feature value
	 * @param CV
	 *            class value
	 */

	protected Vector<Integer> retrieveInstanceIDList(int FN, Double FV, int CV) {
		Hashtable<Integer, Hashtable<Double, ClassCounter>> featureName_HT = trainResult
				.getFeatureTable();
		Vector<Integer> v = null;
		if (featureName_HT.containsKey(FN)) {
			if (featureName_HT.get(FN).containsKey(FV)) {
				v = featureName_HT.get(FN).get(FV).getClassInstanceIDList(CV);
			}
		}
		return v;
	}

	// returns [(A AND B) GIVEN C] amount (still needs division by amount of
	// C-instances)
	protected int compareConditionalInstanceIDLists(int FN1, Double FV1,
			Integer FN2, Double FV2, int CV) {
		return fnc.cutVectorsSort(retrieveInstanceIDList(FN1, FV1, CV),
				retrieveInstanceIDList(FN2, FV2, CV)).size();
	}

	// returns [(A AND B) GIVEN C] amount (still needs division by amount of
	// C-instances)
	protected Vector<Integer> compareExistingConditionalInstanceIDLists(
			int FN1, Double FV1, Vector<Integer> v2, int CV) {
		return fnc.cutVectorsSort(retrieveInstanceIDList(FN1, FV1, CV), v2);
	}

	protected Vector<Integer> compareExistingConditionalInstanceIDLists(
			Vector<Integer> v1, Vector<Integer> v2) {
		return fnc.cutVectorsSort(v1, v2);
	}

	/**
	 * public getter/setter methods
	 */

	public void setBN(BayesNet BN, int workingK) {
		trainResult.getBayesNet(workingK).setBN(BN);

	}

	public void setcurrentWorkingK(int k) {
		this.currentWorkingK = k;
	}

	public double getTreshold() {
		return treshold;
	}

	public int[] getkparents() {
		return kparents;
	}

	public HashMap<Integer, Vector<Integer>>[] getBNs() {
		HashMap<Integer, Vector<Integer>>[] res = new HashMap[kparents.length];
		for (int r = 0; r < kparents.length; r++) {
			res[r] = trainResult.getBayesNet(kparents[r]).getBN()
					.getParentNodeMap();
		}
		return res;

	}
}
