/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.bayes;

/**
 * Data structure used for Entropy based algorithms
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */

 public class ClassCounter_compact {

	protected double[] counter_class;

	 ClassCounter_compact(int amountOfClasses) {
		counter_class = new double[amountOfClasses];
		for (int i = 0; i < counter_class.length; i++) {
			counter_class[i] = 0.0;
		}
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

	 public double[] getCounterTable() {
		return counter_class;
	}

}
