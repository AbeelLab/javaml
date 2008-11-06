/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.core.Dataset;
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
public class NearestMeanClassifier extends AbstractClassifier {

    private static final long serialVersionUID = 3044426429892220857L;

    private EuclideanDistance dist = new EuclideanDistance();

    @Override
    public Object classify(Instance instance) {
        double min = Double.POSITIVE_INFINITY;
        Object pred = null;
        for (Object o : map.keySet()) {
            double d = dist.calculateDistance(map.get(o), instance);
            if (d < min) {
                min = d;
                pred = o;
            }
        }
        return pred;
    }

    private Map<Object, Instance> map;

    @Override
    public void buildClassifier(Dataset data) {
        super.build(data);
        map = new HashMap<Object, Instance>();
        HashMap<Object, Integer> count = new HashMap<Object, Integer>();
        for (Instance i : data) {
            if (!map.containsKey(i.classValue())) {
                map.put(i.classValue(), i);
                count.put(i.classValue(), 1);
            } else {
                map.put(i.classValue(), map.get(i.classValue()).plus(i));
                count.put(i.classValue(), count.get(i.classValue()) + 1);
            }
        }
        for (Object o : map.keySet()) {
            map.put(o, map.get(o).divide(count.get(o)));
        }
    }

}
