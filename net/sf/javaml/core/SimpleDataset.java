/**
 * SimpleDataset.java
 *
 * %SVN.HEADER%
 */

package net.sf.javaml.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * @todo Write javadoc
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class SimpleDataset implements Dataset, Serializable {

    private static final long serialVersionUID = -601979095191981395L;

    /**
     * XXX doc
     */
    public SimpleDataset() {

    }

    /**
     * XXX doc
     */
    public SimpleDataset(Vector<Instance> data) {
        for (Instance in : data) {
            this.addInstance(in);
        }
    }

    /**
     * XXX doc
     */
    private Vector<Instance> instances = new Vector<Instance>();

    /**
     * XXX doc
     */
    private double[] lowArray, highArray;

    /**
     * XXX doc
     */
    public boolean addInstance(Instance instance) {
        if (instance.isClassSet()) {
            classValues.add(instance.getClassValue());
        }

        // XXX this will not work for complex instances...
        try {
            if (instances.size() == 0) {
                lowArray = instance.toArray();
                highArray = instance.toArray();
            }
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (instances.size() > 0 && !instance.isCompatible(instances.get(0))) {
            return false;
        } else {
            instances.add(instance);
            if (lowArray != null && highArray != null) {
                for (int i = 0; i < instance.size(); i++) {
                    if (instance.getValue(i) < lowArray[i]) {
                        lowArray[i] = instance.getValue(i);
                    }
                    if (instance.getValue(i) > highArray[i]) {
                        highArray[i] = instance.getValue(i);
                    }
                }
            }
            return true;
        }

    }

    /**
     * XXX doc
     */
    public int getIndex(Instance i) {
        return instances.indexOf(i);
    }

    /**
     * XXX doc
     */
    public Instance getInstance(int index) {
        return instances.get(index);
    }

    /**
     * XXX doc
     */
    public int size() {
        return instances.size();
    }

    /**
     * XXX doc
     */
    public Instance getMaximumInstance() {
        if (highArray != null)
            return new SimpleInstance(highArray);
        else
            return null;
    }

    /**
     * XXX doc
     */
    public Instance getMinimumInstance() {
        if (lowArray != null)
            return new SimpleInstance(lowArray);
        else
            return null;
    }

    /**
     * XXX doc
     */
    @Override
    public String toString() {
        // TODO optimize using stringbuffer;
        if (this.size() == 0)
            return "";
        String out = this.getInstance(0).toString();
        for (int i = 1; i < this.size(); i++) {
            out += ";" + this.getInstance(i).toString();
        }

        return out;
    }

    private HashSet<Integer> classValues = new HashSet<Integer>();

    public int getNumClasses() {
        return classValues.size();
    }

    /**
     * Provides an iterator over all Instances in this Dataset.
     */
    public Iterator<Instance> iterator() {
        return instances.iterator();
    }

    /**
     * Sort the dataset on the attribute of the given index.
     * 
     * @param index
     *            index of the sorted attribute
     */
    public void sort(int index) {
        // XXX sorting is only done on the real part of the values
        quickSort(index, 0, this.size() - 1);
    }

    private void quickSort(int index, int left, int right) {
        if (left < right) {
            int middle = partition(index, left, right);
            quickSort(index, left, middle);
            quickSort(index, middle + 1, right);
        }
    }

    private int partition(int index, int l, int r) {

        double pivot = getInstance((l + r) / 2).getValue(index);

        while (l < r) {
            while ((getInstance(l).getValue(index) < pivot) && (l < r)) {
                l++;
            }
            while ((getInstance(r).getValue(index) > pivot) && (l < r)) {
                r--;
            }
            if (l < r) {
                swap(l, r);
                l++;
                r--;
            }
        }
        if ((l == r) && (getInstance(r).getValue(index) > pivot)) {
            r--;
        }

        return r;
    }

    private void swap(int first, int second) {
        Instance help = instances.elementAt(first);
        instances.set(first, instances.get(second));// = m_Objects[second];
        instances.set(second, help);// m_Objects[second] = help;
    }

}
