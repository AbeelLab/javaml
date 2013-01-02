/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.core.Instance;

/**
 * This classifier calculates the mean for each class. Subsequently each feature
 * gets one vote. Every features votes for the class that has the most similar
 * average. The final result of the classification of an instance is the
 * distribution of the votes of all the features.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class MeanFeatureVotingClassifier extends AbstractMeanClassifier {

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        HashMap<Object, Double> voting = new HashMap<Object, Double>();
        int count = 0;
        for (Object o : parentClasses) {
            voting.put(o, 0.0);

        }
        for (int i = 0; i < instance.noAttributes(); i++) {
            double min = Double.POSITIVE_INFINITY;
            Object vote = null;
            for (Object o : mean.keySet()) {
                double d = Math.abs(instance.value(i) - mean.get(o).value(i));
                if (d < min) {
                    min = d;
                    vote = o;
                }

            }
            voting.put(vote, voting.get(vote) + 1);
            count++;
        }
        for (Object o : voting.keySet()) {
            voting.put(o, voting.get(o) / count);
        }
        return voting;
    }

    private static final long serialVersionUID = 2393351569825555546L;

  

}
