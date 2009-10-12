/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.bayes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
class BayesNet {

	// public String NodeName="";
	protected HashMap<Integer, Vector<Integer>> parentNodes;
	protected HashMap<Integer, Vector<Integer>> childrenNodes;
	// protected SortedMap<Integer, LinkedList> sMap;
	protected HashMap<Integer, Integer> parentcount;
	// amount of features for vector,for hashmap taken loadfactor into account
	// (special formulae to avoid resizing)
	private int initialcapacity_vector;
	private int initialcapacity_hashmaps;

	// protected Vector<String> NodeChildren;

	BayesNet() {
		parentNodes = new HashMap<Integer, Vector<Integer>>(
				initialcapacity_hashmaps);
		childrenNodes = new HashMap<Integer, Vector<Integer>>(
				initialcapacity_hashmaps);
		parentcount = new HashMap<Integer, Integer>(initialcapacity_hashmaps);
		// sMap= new TreeMap<Integer, LinkedList>();
	}

	void setIC(int icv, int ichm) {
		this.initialcapacity_hashmaps = ichm;
		this.initialcapacity_vector = icv + 1;
	}

	// returns duplicate or not
	boolean addNode(int feature) {

		if (!parentNodes.containsKey(feature)) {
			parentNodes.put(feature, new Vector<Integer>());
			parentcount.put(feature, 0);
			childrenNodes.put(feature, new Vector<Integer>());
			return true;
		} else {
			return false;
		}

	}

	HashMap<Integer, Integer> getparentCountMap() {
		return this.parentcount;
	}

	private void setparentCountMap(HashMap<Integer, Integer> parentCount) {
		this.parentcount = parentCount;
	}

	HashMap<Integer, Vector<Integer>> getParentNodeMap() {
		return this.parentNodes;
	}

	private void setParentNodeMap(HashMap<Integer, Vector<Integer>> nodes) {
		this.parentNodes = nodes;
	}

	private HashMap<Integer, Vector<Integer>> getChildrenNodeMap() {
		return this.childrenNodes;
	}

	private void setChildrenNodeMap(HashMap<Integer, Vector<Integer>> nodes) {
		this.childrenNodes = nodes;
	}

	Set<Integer> getNodes() {
		return (parentNodes.keySet());
	}

	Vector<Integer> getNodeParents(int feature) {
		return (parentNodes.get(feature));
	}

	void setNodeParents(int feature, Vector<Integer> v) {
		parentNodes.put(feature, v);
	}

	int getparentCount(int feature) {
		return this.parentcount.get(feature);
	}

	void setparentCount(int feature, int cnt) {
		this.parentcount.put(feature, cnt);
	}

	Vector<Integer> getNodeChildren(int feature) {
		return (childrenNodes.get(feature));
	}

	void setNodeChildren(int feature, Vector<Integer> v) {
		childrenNodes.put(feature, v);
	}

	void addParent(int feature, int parent) {
		parentNodes.get(feature).add(parent);
		parentcount.put(feature, parentcount.get(feature) + 1);
		childrenNodes.get(parent).add(feature);
	}

	BayesNet cloon() {
		// herwerken geen nood meer aan topologie
		BayesNet BN2 = new BayesNet();
		HashMap<Integer, Vector<Integer>> parentNodesMap = new HashMap<Integer, Vector<Integer>>(
				initialcapacity_hashmaps);
		HashMap<Integer, Vector<Integer>> childrenNodesMap = new HashMap<Integer, Vector<Integer>>(
				initialcapacity_hashmaps);
		HashMap<Integer, Integer> parentcountMap = new HashMap<Integer, Integer>(
				initialcapacity_hashmaps);
		Iterator it = this.getNodes().iterator();
		while (it.hasNext()) {
			int node = (Integer) it.next();
			parentNodesMap.put(node, (Vector<Integer>) this.getParentNodeMap()
					.get(node).clone());
			// hopefully clone...
			parentcountMap.put(node, this.getparentCount(node));
			childrenNodesMap.put(node, (Vector<Integer>) this
					.getChildrenNodeMap().get(node).clone());
		}
		BN2.setParentNodeMap(parentNodesMap);
		BN2.setChildrenNodeMap(childrenNodesMap);
		BN2.setparentCountMap(parentcountMap);
		return BN2;

	}

}
