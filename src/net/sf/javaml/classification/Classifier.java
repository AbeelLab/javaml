/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification;

import java.io.Serializable;
import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Interface for all classifiers.
 * 
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public interface Classifier extends Serializable {
    /**
     * Create a classifier from the given data set.
     * 
     * @param data
     *            the data set to be used to create the classifier
     */
    public void buildClassifier(Dataset data);

    /**
     * Classify the instance according to this classifier.
     * 
     * @param instance
     *            the instance to be classified
     * @return the class to which this instance belongs or null if it doesn't
     *         belong to any of the known classes.
     */
    public Object classify(Instance instance);

    /**
     * Generate the membership distribution for this instance using this
     * classifier. All values should be in the interval [0,1]
     * 
     * Note: The returned map may not contain a value for all classes that were
     * present in the data set used for training. If the map does not contain a
     * value, the value for that class equals zero.
     * 
     * @param instance
     *            the instance to be classified
     * @return an array with membership degrees for all the various classes in
     *         the data set
     */
    public Map<Object, Double> classDistribution(Instance instance);

}
