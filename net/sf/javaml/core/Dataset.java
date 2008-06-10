/**
 * Dataset.java
 *
 * %SVN.HEADER%
 */

package net.sf.javaml.core;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import net.sf.javaml.distance.DistanceMeasure;

/**
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public interface Dataset extends List<Instance> {

    /**
     * Returns the k closest instances.
     * 
     * @param k
     * @param dm
     * @param inst
     * @return
     */
    public Set<Instance> kNearest(int k, DistanceMeasure dm, Instance inst);

    /**
     * Returns a set containing all different classes in this data set. If no
     * classes are available, this will return the empty set.
     * 
     * @return
     */
    public SortedSet<Object> classes();

    /**
     * Add an instance to this data set. The compatibility of the new item with
     * the items in the data set should be checked by the implementation.
     * Incompatible items should not be added to the data set.
     * 
     * @param i
     *            the instance to be added
     * @return true if the instance was added, otherwise false
     */
    public boolean add(Instance i);

    /**
     * Get the instance with a certain index.
     * 
     * @param index
     *            the index of the instance you want to retrieve.
     * @return
     */
    public Instance instance(int index);

    /**
     * Create a number of folds from the data set and return them. The supplied
     * random generator is used to determine which instances are assigned to
     * each of the folds.
     * 
     * @param numFolds
     *            the number of folds to create
     * @param rg
     *            the random generator
     * @return an array of data sets that contains <code>numFolds</code> data
     *         sets.
     */
    public Dataset[] folds(int numFolds, Random rg);

    public int noAttributes();

    // /**
    // * Get the index of a certain instance.
    // *
    // * @param inst
    // * the instance you want the index of
    // * @return
    // */
    // public int getIndex(Instance inst);

    // /**
    // * Returns the size of the data set
    // *
    // * @return the number of instances in the data set.
    // */
    // public int size();

    // /**
    // * Get the 'minimum instance' of this data set. This is a virtual instance
    // * with for each index the lowest value found in the data set.
    // *
    // * If this method is not supported by the implementation, the method
    // should
    // * return null.
    // *
    // * @return an instance with for every index it's lowest value, null if the
    // * data set is empty
    // */
    // public Instance getMinimumInstance();
    //
    // public double getMinimumAttribute(int index);
    //
    // /**
    // * Get the 'maximum instance' of this data set. This is a virtual instance
    // * with for each index the highest value found in the data set.
    // *
    // * If this method is not supported by the implementation, the method
    // should
    // * return null.
    // *
    // * @return an instance with for every index it's highest value, null if
    // the
    // * data set is empty
    // */
    // public Instance getMaximumInstance();
    //
    // public double getMaximumAttribute(int index);
    //
    // /**
    // * Get the 'average instance' of this data set. This is a virtual instance
    // * with for each index the average value found in the data set.
    // *
    // * If this method is not supported by the implementation, the method
    // should
    // * return null.
    // *
    // * @return an instance with for every index it's average value, null if
    // the
    // * data set is empty
    // */
    // public Instance getAverageInstance();
    //
    // public double getAverageAttribute(int index);

    // /**
    // * Returns the number of different classes in the data set.
    // *
    // * @return
    // */
    // public int numClasses();

    // /**
    // * Sorts the data set according to the attribute with the given index.
    // *
    // */
    // public void sort(int index);

    // /**
    // *
    // */

    // /**
    // * Returns the number of attributes each instance has in this data set.
    // *
    // * @return the number of attributes
    // */
    // public int numAttributes();
    //
    // /**
    // * Returns a deep copy of this data set.
    // *
    // */
    // public Dataset copy();
    //
    // /**
    // * Returns the number of values that exist for the attribute
    // */
    // public int numValues(int attIndex);

    // public boolean remove(int index);
    /**
     * Returns the index of the class value in the supplied data set. This
     * method will return -1 if the class value of this instance is not set.
     * 
     * @param data
     *            the data set to give the index for
     * @return the index of the class value
     */
    public int classIndex(Object clazz);
    
    public Object classValue(int index);
}
