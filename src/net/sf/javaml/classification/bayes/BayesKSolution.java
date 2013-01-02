/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.bayes;

import java.util.Vector;

/**
 * Data structure used for Bayesian networks Stores Bayesian networks and
 * topology ordened features
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
class BayesKSolution {

	private BayesNet BN;
	private Vector<Integer> top;

	BayesKSolution(BayesNet BN, Vector<Integer> top) {
		this.top = top;
		this.BN = BN;
	}

	BayesNet getBN() {
		return BN;
	}

	void setBN(BayesNet BN) {
		this.BN = BN;
	}

	void setTop(Vector<Integer> top2) {
		this.top = (Vector<Integer>) top2.clone();
	}

	Vector<Integer> getTop() {
		return top;
	}

}
