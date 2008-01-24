/**
 * Classifier.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.classification;

import java.io.Serializable;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * Interface for all classifiers.
 * 
 * {@jmlSource}
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
     * @return the index of the class to which this instance belongs
     */
    public int classifyInstance(Instance instance);

    /**
     * Generate the membership distribution for this instance using this
     * classifier.
     * 
     * @param instance
     *            the instance to be classified
     * @return an array with membership degrees for all the various classes in
     *         the data set
     */
    public double[] distributionForInstance(Instance instance);
    
  
}
