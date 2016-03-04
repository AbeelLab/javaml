/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.bayes;

import java.util.Iterator;
import java.util.Vector;

/**
 * Data structure used for Bayesian networks
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */

 public class ClassCounter {

	protected double[] counter_class;
	protected Vector<Integer>[] classInstanceIDList;

	 ClassCounter(int amountOfClasses) {
		counter_class = new double[amountOfClasses];
		classInstanceIDList = new Vector[amountOfClasses];
		for (int i = 0; i < counter_class.length; i++) {
			counter_class[i] = 0.0;
			classInstanceIDList[i] = new Vector<Integer>();
		}
	}

	/** Sets the name of the example */
	 void setClassInstanceIDList(Vector<Integer> vidlist, int index) {
		classInstanceIDList[index] = vidlist;
	}

	 void addInstanceIDtoList(int index, int iId) {
		classInstanceIDList[index].add(new Integer(iId));
	}

	/** Returns the name of the example */
	 Vector<Integer> getClassInstanceIDList(int index) {
		return classInstanceIDList[index];
	}

	 public Vector<Integer> getClassInstanceIDLists() {
		Vector<Integer> classInstanceIDList_merge = new Vector<Integer>();
		for (int i = 0; i < counter_class.length; i++) {
			Vector<Integer> temp = getClassInstanceIDList(i);
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				int tempp = (Integer) it.next();
				if (!classInstanceIDList_merge.contains(tempp)) {
					classInstanceIDList_merge.add(tempp);
				}
			}
		}
		return classInstanceIDList_merge;
	}

	/** Sets the name of the example */
	 void setCountClass(double amount, int index) {
		counter_class[index] = amount;
	}

	/** Returns the name of the example */
	 double getCountClass(int index) {
		return counter_class[index];
	}

	/** Returns the name of the example */
	// to get prior probs of a featurevalue
	 double getSumAllCountClasses() {
		double sum = 0.0;
		for (int i = 0; i < counter_class.length; i++) {
			sum += counter_class[i];
		}
		return sum;
	}

	 double[] getCounterTable() {
		return counter_class;
	}

}
