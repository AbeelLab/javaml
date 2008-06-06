package net.sf.javaml.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SparseInstance extends AbstractInstance implements Instance {

    private HashMap<Integer, Double> data = new HashMap<Integer, Double>();

    private static final long serialVersionUID = -7642462956857985858L;

    @Override
    public double value(int pos) {
        return data.get(pos).doubleValue();
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
        return data.get(key);
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

//    private int maxIndex = 0;

    @Override
    public int noAttributes() {
      return Collections.max(data.keySet());
    }

}
