/**
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

import net.sf.javaml.distance.DistanceMeasure;

class Fold implements Dataset {
    private int[] indices;

    private Dataset parent;

    public Fold(Dataset parent, int[] indices) {
        // System.out.println("construction: "+parent.classes());
        this.indices = indices;
        this.parent = parent;
    }

    @Override
    public boolean add(Instance i) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public SortedSet<Object> classes() {
        // System.out.println("Call");
        return parent.classes();
    }

    @Override
    public Dataset[] folds(int numFolds, Random rg) {
        // TODO this method can be implemented on a fold.
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public Instance instance(int index) {
        // System.out.println(parent);
        // System.out.println(parent.size());
        // System.out.println(index);
        return parent.instance(indices[index]);
    }

    @Override
    public Set<Instance> kNearest(int k, Instance inst,DistanceMeasure dm) {
        // TODO this method can be implemented on a fold.
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public void add(int index, Instance element) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public boolean addAll(Collection<? extends Instance> c) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Instance> c) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");

    }

    @Override
    public boolean contains(Object o) {
        // TODO this method can be implemented on a fold.
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO this method can be implemented on a fold.
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public Instance get(int index) {
        return instance(index);
    }

    @Override
    public int indexOf(Object o) {
        // TODO this method can be implemented on a fold.
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    class FoldIterator implements ListIterator<Instance> {

        private int currentIndex = 0;

        public FoldIterator(int index) {
            this.currentIndex = index;
        }

        public FoldIterator() {
            this(0);
        }

        @Override
        public boolean hasNext() {
            return currentIndex < indices.length;
        }

        @Override
        public Instance next() {
            currentIndex++;
            return instance(currentIndex - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("You cannot do this on a fold.");

        }

        @Override
        public void add(Instance arg0) {
            throw new UnsupportedOperationException("You cannot do this on a fold.");

        }

        @Override
        public boolean hasPrevious() {
            return currentIndex > 0;
        }

        @Override
        public int nextIndex() {
            return currentIndex;
        }

        @Override
        public Instance previous() {
            currentIndex--;
            return instance(currentIndex);
        }

        @Override
        public int previousIndex() {
            return currentIndex;
        }

        @Override
        public void set(Instance arg0) {
            throw new UnsupportedOperationException("You cannot do this on a fold.");

        }

    }

    @Override
    public Iterator<Instance> iterator() {
        return new FoldIterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        // TODO this method can be implemented on a fold.
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public ListIterator<Instance> listIterator() {
        return new FoldIterator();
    }

    @Override
    public ListIterator<Instance> listIterator(int index) {
        return new FoldIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public Instance remove(int index) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public Instance set(int index, Instance element) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public int size() {
        return indices.length;
    }

    @Override
    public List<Instance> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public Object[] toArray() {
        Object[] out = new Object[indices.length];
        for (int i = 0; i < size(); i++) {
            out[i] = instance(i);
        }
        return out;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        Vector<T> tmp = new Vector<T>();
        for (Instance i : this) {
            tmp.add((T) i);
        }
        return tmp.toArray(a);
    }

    @Override
    public int noAttributes() {
        return parent.noAttributes();
    }

    @Override
    public int classIndex(Object clazz) {
        return parent.classIndex(clazz);
    }

    @Override
    public Object classValue(int index) {
        return parent.classValue(index);
    }

    @Override
    public Dataset copy() {
        Dataset out=new DefaultDataset();
        for(Instance i:this)
            out.add(i.copy());
        return out;
    }
}