/*
 * DefaultDataset.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package net.sf.javaml.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import net.sf.javaml.distance.DistanceMeasure;

public class DefaultDataset extends Vector<Instance> implements Dataset {

    @Override
    public synchronized boolean addAll(Collection<? extends Instance> c) {
        for (Instance i : c)
            if(i.classValue()!=null)
                classes.add(i.classValue());
        return super.addAll(c);
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends Instance> c) {
        for (Instance i : c)
            if(i.classValue()!=null)
                classes.add(i.classValue());
        return super.addAll(index, c);
    }

    private static final long serialVersionUID = 8586030444860912681L;

    private Set<Object> classes = new HashSet<Object>();

    @Override
    public void clear() {
        classes.clear();
        super.clear();
    }

    @Override
    public synchronized boolean add(Instance e) {
        if (e.classValue() != null)
            classes.add(e.classValue());
        // System.out.println("ADD: "+classes);
        return super.add(e);
    }

    @Override
    public void add(int index, Instance e) {
        if (e.classValue() != null)
            classes.add(e.classValue());
        super.add(index, e);
    }

    @Override
    public synchronized void addElement(Instance obj) {
        if (obj.classValue() != null)
            classes.add(obj.classValue());
        super.addElement(obj);
    }

    @Override
    public synchronized void insertElementAt(Instance e, int index) {
        if (e.classValue() != null)
            classes.add(e.classValue());
        super.insertElementAt(e, index);
    }

    @Override
    public synchronized void setElementAt(Instance e, int index) {
        if (e.classValue() != null)
            classes.add(e.classValue());
        super.setElementAt(e, index);
    }

    @Override
    public Instance instance(int index) {
        return super.get(index);
    }

    @Override
    public Set<Object> classes() {
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
}
