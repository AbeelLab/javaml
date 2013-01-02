/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.bayes;

/** "Memory" help class
 *
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

class BayesResult {
	/** Stores the prior probabilities of each class */
	protected double[] classFreq;
	protected double[] classProb;
	protected Vector<Integer>[] classInstanceIDList;
	private String[][] topList;
	private HashMap<String, Integer> help;
	private HashMap<String, Integer> s2;
	protected HashMap<Integer, BayesKSolution> BN = new HashMap<Integer, BayesKSolution>();

	double treshold;
	/**
	 * Stores the counts for each feature: an entry in the hashTable stores the
	 * array of class counts for a feature
	 */
	protected Hashtable<Integer, Hashtable<Double, ClassCounter>> featureTable;
	protected Hashtable<Integer, Hashtable<Double, ClassCounter_compact>> featureTable_compact;
	protected HashMap<Integer, HashMap<Integer, Double>> CMI_map;
	protected LinkedList<Integer> MI_map;
	protected Vector<Integer> usedFeatures;
	protected HashMap<Integer, Object[]> ImaxLL;

	void setBNBB_XiXjinS_SortedCMI(HashMap<Integer, Object[]> ImaxLL) {
		this.ImaxLL = ImaxLL;
	}

	void setUsedFeatures_SortedMI(Vector<Integer> usedFeatures) {
		this.usedFeatures = usedFeatures;
	}

	Vector<Integer> getUsedFeatures_SortedMI() {

		return this.usedFeatures;
	}

	HashMap<Integer, Object[]> getBNBB_XiXjinS_SortedCMI() {
		return this.ImaxLL;
	}

	void setHelpMap(HashMap<String, Integer> help) {
		this.help = help;
	}

	HashMap<String, Integer> getHelpMap() {
		return help;
	}

	void sets2imap(HashMap<String, Integer> s2) {
		this.s2 = s2;
	}

	HashMap<String, Integer> gets2imap() {
		return s2;
	}

	/** Sets the class priors */
	void setClassFreqs(double[] cfreq) {
		classFreq = cfreq;
	}

	void initInstanceIDtoClassListArray(int amountOfClasses) {
		classInstanceIDList = new Vector[amountOfClasses];
		for (int i = 0; i < amountOfClasses; i++) {
			classInstanceIDList[i] = new Vector<Integer>();
		}
	}

	void addInstanceIDtoClassList(int index, int instanceid_count) {
		classInstanceIDList[index].add(instanceid_count);
	}

	Vector<Integer> getClassInstanceList(int index) {
		return classInstanceIDList[index];
	}

	/***/
	String[] getTopologyList(int k) {
		return (topList[k]);
	}

	/** Sets the class priors */
	void setTopologyList(String[] list, int k) {
		topList[k] = list;
	}

	/** Returns the class priors */
	double[] getClassFreqs() {
		return (classFreq);
	}

	/** Sets the class priors */
	 void setClassProbs(double[] cprob) {
		classProb = cprob;
	}

	/** Returns the class priors */
	 double[] getClassProbs() {
		return (classProb);
	}

	 void setBayesNet(BayesKSolution BKS, int k) {
		this.BN.put(k, BKS);
	}

	 BayesKSolution getBayesNet(int k) {
		return this.BN.get(k);
	}

	/*
	 *  void settopology(topology top){ this.top=top; }  topology
	 * gettopology(){ return top; }
	 */

	 void setConditionalMI_HMap(
			HashMap<Integer, HashMap<Integer, Double>> CMItable_sorted1) {
		CMI_map = CMItable_sorted1;
	}

	 HashMap<Integer, HashMap<Integer, Double>> getConditionalMI_HMap() {
		return CMI_map;
	}

	 void setMI_LHMap(LinkedList<Integer> mimap) {
		MI_map = mimap;
	}

	 LinkedList<Integer> getMI_LHMap() {
		return MI_map;
	}

	/** Returns the class priors */
	/** Sets the feature hash */
	 void setFeatureTable(
			Hashtable<Integer, Hashtable<Double, ClassCounter>> table) {
		featureTable = table;
	}

	/** Returns the feature hash */
	 Hashtable<Integer, Hashtable<Double, ClassCounter>> getFeatureTable() {
		return (featureTable);
	}

	 void setFeatureTable_compact(
			Hashtable<Integer, Hashtable<Double, ClassCounter_compact>> table) {
		featureTable_compact = table;
	}

	/** Returns the feature hash */
	 Hashtable<Integer, Hashtable<Double, ClassCounter_compact>> getFeatureTable_compact() {
		return (featureTable_compact);
	}
}
