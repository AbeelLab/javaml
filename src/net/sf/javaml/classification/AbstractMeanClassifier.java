/**
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.util.HashMap;
import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Abstract classifier class that is the parent of all classifiers that require
 * the mean of each class as training.
 * 
 * @author Thomas Abeel
 * 
 */
public class AbstractMeanClassifier extends AbstractClassifier {

    private static final long serialVersionUID = 8596181454461400908L;

    protected Map<Object, Instance> mean;

    public Instance getMean(Object clazz) {
        return mean.get(clazz);

    }

    @Override
    public void buildClassifier(Dataset data) {
        super.buildClassifier(data);
        mean = new HashMap<Object, Instance>();
        HashMap<Object, Integer> count = new HashMap<Object, Integer>();
        for (Instance i : data) {
            if (!mean.containsKey(i.classValue())) {
                mean.put(i.classValue(), i);
                count.put(i.classValue(), 1);
            } else {
                mean.put(i.classValue(), mean.get(i.classValue()).add(i));
                count.put(i.classValue(), count.get(i.classValue()) + 1);
            }
        }
        for (Object o : mean.keySet()) {
            mean.put(o, mean.get(o).divide(count.get(o)));
        }

    }
}
