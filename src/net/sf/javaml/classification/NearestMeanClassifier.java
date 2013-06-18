/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Nearest mean classifier. This classifier calculates the mean for each class
 * and use this to classify further instances.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class NearestMeanClassifier extends AbstractMeanClassifier {

    private static final long serialVersionUID = 3044426429892220857L;

    private EuclideanDistance dist = new EuclideanDistance();

    @Override
    public Object classify(Instance instance) {
        double min = Double.POSITIVE_INFINITY;
        Object pred = null;
        for (Object o : mean.keySet()) {
            double d = dist.calculateDistance(mean.get(o), instance);
            if (d < min) {
                min = d;
                pred = o;
            }
        }
        return pred;
    }

    

}
