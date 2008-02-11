/**
 * SimpleDataset.java
 *
 * %SVN.HEADER%
 */

package net.sf.javaml.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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

    /*
     * After remove operations, the caches of the data set may contain false
     * information and should be recalculated.
     */
    private boolean dirty = true;

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
        this.addAll(data);
    }

    public SimpleDataset(Dataset data) {
        for (Instance i : data)
            add(i.copy());
    }

    /**
     * XXX doc
     */
    private Vector<Instance> instances = new Vector<Instance>();

    /**
     * XXX doc
     */
    private double[] lowArray, highArray, sum;

    /**
     * XXX doc
     */
    public int getIndex(Instance i) {
        return instances.indexOf(i);
    }

    /**
     * XXX doc
     */
    public Instance instance(int index) {
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
        if (dirty)
            recalculate(numAttributes());
        if (highArray != null)
            return new SimpleInstance(highArray);
        else
            return null;
    }

    /**
     * XXX doc
     */
    public Instance getMinimumInstance() {
        if (dirty)
            recalculate(numAttributes());
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
        StringBuffer out = new StringBuffer("");
        if (this.size() == 0)
            return "";
        out.append(this.instance(0).toString());
        for (int i = 1; i < this.size(); i++) {
            out.append(";");
            out.append(this.instance(i).toString());

        }

        return out.toString();
    }

    private HashSet<Integer> classValues = new HashSet<Integer>();

    public int numClasses() {
        if (dirty)
            recalculate(numAttributes());
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

        double pivot = instance((l + r) / 2).value(index);

        while (l < r) {
            while ((instance(l).value(index) < pivot) && (l < r)) {
                l++;
            }
            while ((instance(r).value(index) > pivot) && (l < r)) {
                r--;
            }
            if (l < r) {
                swap(l, r);
                l++;
                r--;
            }
        }
        if ((l == r) && (instance(r).value(index) > pivot)) {
            r--;
        }

        return r;
    }

    public void swap(int first, int second) {
        Instance help = instances.elementAt(first);
        instances.set(first, instances.get(second));// = m_Objects[second];
        instances.set(second, help);// m_Objects[second] = help;
    }

    public int numAttributes() {
        if (instances.size() == 0)
            return 0;
        else
            return instances.get(0).size();

    }

    public Instance getAverageInstance() {
        if (dirty)
            recalculate(numAttributes());
        double[] tmpSum = new double[sum.length];
        System.arraycopy(sum, 0, tmpSum, 0, sum.length);
        for (int i = 0; i < sum.length; i++)
            tmpSum[i] /= instances.size();
        return new SimpleInstance(tmpSum);

    }

    /*
     * Flag to indicate whether the cache should be updated when adding new
     * instances.
     * 
     */
    private boolean lazy = true;

    /**
     * XXX doc
     */
    public boolean add(Instance instance) {
        if (dirty && !lazy)
            recalculate(instance.size());
        if (instances.size() > 0 && !instance.isCompatible(instances.get(0)))
            return false;
        instances.add(instance);
        if (!lazy)
            update(instance);
        return true;

    }

    private void update(Instance instance) {
        classValues.add(instance.classValue());
        for (int i = 0; i < instance.size(); i++) {
            if (instance.value(i) < lowArray[i]) {
                lowArray[i] = instance.value(i);
            }
            if (instance.value(i) > highArray[i]) {
                highArray[i] = instance.value(i);
            }
            sum[i] += instance.value(i);
        }

    }

    private void recalculate(int size) {
        classValues = new HashSet<Integer>();
        numValuesCache = new int[size];
        numValuesSet = new boolean[size];
        lowArray = new double[size];
        highArray = new double[size];
        sum = new double[size];
        dirty = false;
        for (Instance inst : instances) {
            update(inst);
        }

    }

    public double getAverageAttribute(int index) {
        if (dirty)
            recalculate(numAttributes());
        return sum[index] / instances.size();
    }

    public double getMaximumAttribute(int index) {
        if (dirty)
            recalculate(numAttributes());
        return highArray[index];
    }

    public double getMinimumAttribute(int index) {
        if (dirty)
            recalculate(numAttributes());
        return lowArray[index];
    }

    public Dataset copy() {
        return new SimpleDataset(this);
    }

    /*
     * Contains which of the values in numValuesCache actually have meaning.
     */
    private boolean[] numValuesSet = null;

    private int[] numValuesCache = null;

    public int numValues(int attIndex) {
        if (dirty)
            recalculate(numAttributes());
        if (numValuesSet == null)
            numValuesSet = new boolean[numAttributes()];
        if (numValuesCache == null)
            numValuesCache = new int[numAttributes()];
        if (!numValuesSet[attIndex]) {
            Set<Double> tmp = new HashSet<Double>();
            for (Instance i : this)
                tmp.add(i.value(attIndex));
            numValuesCache[attIndex] = tmp.size();
        }
        return numValuesCache[attIndex];
    }

    public boolean addAll(Collection<? extends Instance> c) {
        for (Instance i : c)
            this.add(i);
        return true;
    }

    public void clear() {
        dirty = true;
        instances.clear();

    }

    public boolean contains(Object o) {
        return instances.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return instances.containsAll(c);
    }

    public boolean isEmpty() {
        return instances.isEmpty();
    }

    public boolean remove(Object o) {
        dirty = instances.remove(o);
        return dirty;
    }

    public boolean removeAll(Collection<?> c) {
        dirty = instances.removeAll(c);
        return dirty;

    }

    public boolean retainAll(Collection<?> c) {
        dirty = instances.retainAll(c);
        return dirty;
    }

    public Object[] toArray() {
        return instances.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return instances.toArray(a);
    }

    public boolean remove(int index) {
        dirty=true;
        instances.remove(index);
        return dirty;
    }

}
