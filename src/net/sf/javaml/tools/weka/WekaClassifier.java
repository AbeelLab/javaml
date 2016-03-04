/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.weka;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import weka.core.Instances;

public class WekaClassifier implements Classifier {

    private static final long serialVersionUID = -4607698346509036963L;

    private weka.classifiers.Classifier wekaClass;

    private ToWekaUtils utils;

    public WekaClassifier(weka.classifiers.Classifier wekaClass) {
        this.wekaClass = wekaClass;
    }

    public void buildClassifier(Dataset data) {
        utils = new ToWekaUtils(data);
        Instances inst = utils.getDataset();
        try {
            wekaClass.buildClassifier(inst);
        } catch (Exception e) {
            throw new WekaException(e);
        }

    }

    @Override
    public Object classify(Instance instance) {

        try {
            return utils.convertClass(wekaClass.classifyInstance(utils.instanceToWeka(instance)));
        } catch (Exception e) {
            throw new WekaException(e);
        }
    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        try {
            Map<Object, Double> out = new HashMap<Object, Double>();
            double[] distr = wekaClass.distributionForInstance(utils.instanceToWeka(instance));
            for (int i = 0; i < distr.length; i++)
                out.put(utils.convertClass(i), distr[i]);
            return out;
        } catch (Exception e) {
            throw new WekaException(e);
        }
    }

}
