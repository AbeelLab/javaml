package net.sf.javaml.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class SparseInstance extends AbstractInstance implements Instance {

    private HashMap<Integer, Double> data = new HashMap<Integer, Double>();

    private double defaultValue;

    /* The number of attributes */
    private int noAttributes = -1;

    private static final long serialVersionUID = -7642462956857985858L;

    public SparseInstance() {
        this(-1);
    }

    public SparseInstance(int noAttributes) {
        this(noAttributes, 0.0);
    }

    public SparseInstance(int noAttributes, double defaultValue) {
        this.defaultValue = defaultValue;
        this.noAttributes = noAttributes;

    }

    @Override
    public double value(int pos) {
        return get(pos).doubleValue();
    }

    @Override
    public void clear() {
        data.clear();

    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<Integer, Double>> entrySet() {
        return data.entrySet();
    }

    @Override
    public Double get(Object key) {
        if (data.containsKey(key))
            return data.get(key);
        else
            return defaultValue;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Set<Integer> keySet() {
        return data.keySet();
    }

    @Override
    public Double put(Integer key, Double value) {
        return data.put(key, value);

    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Double> m) {
        data.putAll(m);

    }

    @Override
    public Double remove(Object key) {
        return data.remove(key);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Collection<Double> values() {
        return data.values();
    }

    // private int maxIndex = 0;

    @Override
    public int noAttributes() {
        if (noAttributes < 0)
            return Collections.max(data.keySet())+1;
        else
            return noAttributes;

    }

    @Override
    public void removeAttribute(int remove) {
        data.remove(remove);
        List<Integer> indices = new Vector<Integer>();
        indices.addAll(data.keySet());
        Collections.sort(indices);
        for (int i = 0; i < indices.size(); i++) {
            int index = indices.get(i);

            if (index > remove) {
                data.put(index - 1, data.get(index));
                data.remove(index);
            }
        }
        noAttributes--;

    }
    @Override
    public String toString(){
        return "{"+data.toString()+";"+classValue()+"}";
    }
}
