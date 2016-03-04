/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification.tree;

import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.meta.Bagging;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class RandomForest implements Classifier {

	/**
     * 
     */
	private static final long serialVersionUID = 5832370995432897745L;

	private Bagging bagger;

	private int treeCount;

	private int numAttributes;

	public void setNumAttributes(int k) {
		this.numAttributes = k;

	}

	private boolean calculateOutOfBagErrorEstimate = false;

	private Random rg;

	public RandomForest(int treeCount) {
		this(treeCount, false, 1, new Random(System.currentTimeMillis()));
	}

	public RandomForest(int treeCount, boolean calculateOutOfBagErrorEstimate, int numAttributes, Random rg) {
		this.treeCount = treeCount;
		if (rg == null)
			rg = new Random(System.currentTimeMillis());
		this.rg = rg;
		this.calculateOutOfBagErrorEstimate = calculateOutOfBagErrorEstimate;
		this.numAttributes = numAttributes;
	}

	public double getOutOfBagErrorEstimate() {
		return bagger.getOutOfBagErrorEstimate();
	}

	public void buildClassifier(Dataset data) {
		if (treeCount < 0)
			treeCount = (int) Math.sqrt(data.noAttributes()) + 1;
		RandomTree[] trees = new RandomTree[treeCount];
		assert (rg != null);
		for (int i = 0; i < trees.length; i++) {
			trees[i] = new RandomTree(numAttributes, rg);
		}
		bagger = new Bagging(trees, rg);
		bagger.setCalculateOutOfBagErrorEstimate(calculateOutOfBagErrorEstimate);
		bagger.buildClassifier(data);

	}

	@Override
	public Object classify(Instance instance) {
		return bagger.classify(instance);
	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		return bagger.classDistribution(instance);
	}

}
