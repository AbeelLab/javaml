/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.AbstractClassifier;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;
import be.abeel.util.Pair;

/**
 * Bagging meta learner. This implementation can also calculate the out-of-bag
 * error estimate while training at very little extra cost.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class Bagging extends AbstractClassifier {

	private static final long serialVersionUID = 5571842927861670307L;

	private Classifier[] classifiers;

	private Dataset dataReference = null;

	private Sampling samplingMethod=Sampling.NormalBootstrapping;

	private long seed;

	/**
	 * Please use the 3 argument constructor.
	 * 
	 * @param classifiers
	 * @param rg
	 */
	@Deprecated
	public Bagging(Classifier[] classifiers, Random rg) {
		this.classifiers = classifiers;
		if(rg==null)
			rg=new Random();
		this.seed = rg.nextLong();
	}

	public Bagging(Classifier[] classifiers,Sampling s){
		this(classifiers,s,System.currentTimeMillis());
	}
	public Bagging(Classifier[] classifiers, Sampling s, long seed) {
		this.classifiers = classifiers;
		this.seed = seed;
		this.samplingMethod = s;
	}

	private boolean calculateOutOfBagErrorEstimate = false;

	public void setCalculateOutOfBagErrorEstimate(boolean b) {
		this.calculateOutOfBagErrorEstimate = b;
	}

	private double outOfBagErrorEstimate;

	public double getOutOfBagErrorEstimate() {
		return outOfBagErrorEstimate;
	}

	public void buildClassifier(Dataset data) {
		this.dataReference = data;
		int t = 0, f = 0;
		for (int i = 0; i < classifiers.length; i++) {
			Pair<Dataset, Dataset>sample = samplingMethod.sample(data,data
					.size(), seed++);
			classifiers[i].buildClassifier(sample.x());
			if (calculateOutOfBagErrorEstimate) {
				for (Instance inst : sample.y()) {
					Object predClass = classifiers[i].classify(inst);
					if (predClass.equals(inst.classValue())) {
						t++;
					} else {
						f++;
					}
				}
				outOfBagErrorEstimate = t / (t + f);
				
			}
			
		}
		

	}

	@Override
	public Map<Object, Double> classDistribution(Instance instance) {
		Map<Object, Double> membership = new HashMap<Object, Double>();
		for (Object o : dataReference.classes())
			membership.put(o, 0.0);
		for (int i = 0; i < classifiers.length; i++) {
			Object prediction = classifiers[i].classify(instance);
			membership.put(prediction, membership.get(prediction)
					+ (1.0 / classifiers.length));// [classifiers[i].classifyInstance(instance)]++;
		}
		// for (int i = 0; i < this.numClasses; i++)
		// membership[i] /= classifiers.length;
		return membership;

	}
}
