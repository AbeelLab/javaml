package net.sf.javaml.tools.weka;

import weka.core.Instances;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class ClassifierBridge implements Classifier {

	private static final long serialVersionUID = -4607698346509036963L;
	private weka.classifiers.Classifier wekaClass;
	private WekaUtils utils;

	public ClassifierBridge(weka.classifiers.Classifier wekaClass) {
		this.wekaClass = wekaClass;
	}

	public void buildClassifier(Dataset data) {
		utils = new WekaUtils(data);
		Instances inst = utils.getDataset();
		try {
			wekaClass.buildClassifier(inst);
		} catch (Exception e) {
			throw new WekaException(e);
		}

	}

	public int classifyInstance(Instance instance) {

		try {
			return (int) wekaClass.classifyInstance(utils
					.instanceToWeka(instance));
		} catch (Exception e) {
			throw new WekaException(e);
		}
	}

	public double[] distributionForInstance(Instance instance) {
		try {
			return wekaClass.distributionForInstance(utils
					.instanceToWeka(instance));
		} catch (Exception e) {
			throw new WekaException(e);
		}
	}

}
