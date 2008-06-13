/**
 * DefaultDataset.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import net.sf.javaml.distance.DistanceMeasure;
/**
 * Provides a standard data set implementation.
 * 
 * {@jmlSource}
 * 
 * @see Dataset
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class DefaultDataset extends Vector<Instance> implements Dataset {

    private int maxAttributes = 0;

    private void check(Collection<? extends Instance> c) {
        for (Instance i : c)
            check(i);
    }

    private void check(Instance i) {

        if (i.classValue() != null)
            classes.add(i.classValue());
        if (i.noAttributes() > maxAttributes)
            maxAttributes = i.noAttributes();
    }

    @Override
    public synchronized boolean addAll(Collection<? extends Instance> c) {
        check(c);
        return super.addAll(c);
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends Instance> c) {
        check(c);
        return super.addAll(index, c);
    }

    private static final long serialVersionUID = 8586030444860912681L;

    private TreeSet<Object> classes = new TreeSet<Object>();

    @Override
    public void clear() {
        classes.clear();
        super.clear();
    }

    @Override
    public synchronized boolean add(Instance e) {
        check(e);
        return super.add(e);
    }

    @Override
    public void add(int index, Instance e) {
        check(e);
        super.add(index, e);
    }

    @Override
    public synchronized void addElement(Instance e) {
        check(e);
        super.addElement(e);
    }

    @Override
    public synchronized void insertElementAt(Instance e, int index) {
        check(e);
        super.insertElementAt(e, index);
    }

    @Override
    public synchronized void setElementAt(Instance e, int index) {
        check(e);
        super.setElementAt(e, index);
    }

    @Override
    public Instance instance(int index) {
        return super.get(index);
    }

    @Override
    public SortedSet<Object> classes() {
        return classes;
    }

    /**
     * Returns the k instances of the given data set that are the closest to the
     * instance that is given as a parameter.
     * 
     * @param dm
     *            the distance measure used to calculate the distance between
     *            instances
     * @param inst
     *            the instance for which we need to find the closest
     * @return the instances from the supplied data set that are closest to the
     *         supplied instance
     * 
     * TODO bad implementation, this could be written much more efficient.
     * 
     */
    public Set<Instance> kNearest(int k, DistanceMeasure dm, Instance inst) {

        Set<Instance> closest = new HashSet<Instance>();
        // double bestDistance = dm.calculateDistance(inst, closest);
        for (Instance tmp : this) {
            if (!inst.equals(tmp)) {
                closest.add(tmp);
                if (closest.size() > k)
                    removeFarthest(closest, inst, dm);
            }
            // double tmpDistance = dm.calculateDistance(inst,
            // data.instance(i));
            // if (dm.compare(tmpDistance, bestDistance)) {
            // bestDistance = tmpDistance;
            // closest = data.instance(i);
            // }
        }
        return closest;
    }

    /*
     * Removes the element from the vector that is farthest from the supplied
     * element.
     */
    private void removeFarthest(Set<Instance> vector, Instance supplied, DistanceMeasure dist) {
        Instance tmp = null;// ; = vector.get(0);
        double max = 0;// dist.calculateDistance(vector.get(0), supplied);
        for (Instance inst : vector) {
            double tmpDist = dist.measure(inst, supplied);
            if (dist.compare(max, tmpDist)) {
                max = tmpDist;
                tmp = inst;
            }
        }

        if (!vector.remove(tmp)) {
            System.out.println(tmp);
            throw new RuntimeException("This should not happen...");
        }

    }

    @Override
    public Dataset[] folds(int numFolds, Random rg) {
        Dataset[] out = new Dataset[numFolds];
        List<Integer> indices = new Vector<Integer>();
        for (int i = 0; i < this.size(); i++)
            indices.add(i);
        int size = (this.size() / numFolds) + 1;
        int[][] array = new int[numFolds][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < numFolds; j++) {
                if (indices.size() > 0)
                    array[j][i] = indices.remove(rg.nextInt(indices.size()));
                else
                    array[j][i] = -1;
            }
        }
        for (int i = 0; i < numFolds; i++) {
            int[] indi;
            if (array[i][size - 1] == -1) {
                indi = new int[size - 1];
                System.arraycopy(array[i], 0, indi, 0, size - 1);
            } else {
                indi = new int[size];
                System.arraycopy(array[i], 0, indi, 0, size);
            }
            out[i] = new Fold(this, indi);

        }
        // System.out.println(Arrays.deepToString(array));
        return out;
    }

    @Override
    public int noAttributes() {
        return this.get(0).noAttributes();
    }

    @Override
    public int classIndex(Object clazz) {

        if (clazz != null)
            return this.classes().headSet(clazz).size();
        else
            return -1;

    }

    @Override
    public Object classValue(int index) {
        int i = 0;
        for (Object o : this.classes) {
            if (i == index)
                return o;
            i++;
        }
        return null;
    }
}
