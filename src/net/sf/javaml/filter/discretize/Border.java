/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.discretize;

import java.util.Vector;

/**
 * Data structure used by discretisation methods
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
class Border {

	double lowestLEF = -1.0;
	double TMin = -1.0;
	int TMinIndex = -1;
	int k1 = 1;
	int k2 = 1;
	double EntropyS1 = -1.0;
	double EntropyS2 = -1.0;
	Vector<Double> TminC;

	double getlowestLEF() {

		return lowestLEF;
	}

	Vector<Double> getTminC() {

		return TminC;
	}

	double getTmin() {

		return TMin;
	}

	int getTminIndex() {

		return TMinIndex;
	}

	int getK1() {

		return k1;
	}

	int getK2() {

		return k1;
	}

	double getEntropyS1() {

		return EntropyS1;
	}

	double getEntropyS2() {

		return EntropyS2;
	}

	void setlowestLEF(double LEF) {
		this.lowestLEF = LEF;
	}

	void setTMin(double tmin) {
		this.TMin = tmin;

	}

	void setTMinC(Vector<Double> tminC) {
		this.TminC = tminC;

	}

	void setTMinIndex(int borderindex) {
		this.TMinIndex = borderindex;

	}

	void setK1(int k) {
		this.k1 = k;

	}

	void setK2(int k) {
		this.k2 = k;

	}

	void setEntS1(double EntS1) {
		this.EntropyS1 = EntS1;
	}

	void setEntS2(double EntS2) {
		this.EntropyS2 = EntS2;
	}
}
