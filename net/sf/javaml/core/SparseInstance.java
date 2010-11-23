/**
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Implementation of a sparse instance. This implementation maps attribute
 * indices to values. Attributes for which the value is not set return a default
 * value.
 * 
 * @see Dataset
 * @see Instance
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class SparseInstance extends AbstractInstance implements Instance {

    /**
     * Sets the number of attributes that this sparse instance has.
     * 
     * @param noAttributes
     */
    public void setNoAttributes(int noAttributes) {
        this.noAttributes = noAttributes;
    }

    private HashMap<Integer, Double> data = new HashMap<Integer, Double>();

    private double defaultValue;

    /* The number of attributes */
    private int noAttributes = -1;

    private static final long serialVersionUID = -7642462956857985858L;

    public SparseInstance() {
        this(-1);
    }

    /* defaultValue will be set to 0.0, classValue to null */
    public SparseInstance(int noAttributes) {
        this(noAttributes, 0.0, null);
    }

    /* classValue will be set to null */
    public SparseInstance(int noAttributes, double defaultValue) {
        this(noAttributes, defaultValue, null);
    }

    /* defaultValue will be set to 0.0 */
    public SparseInstance(int noAttributes, Object classValue) {
        this(noAttributes, 0.0, classValue);
    }

    /* Create empty sparse instance */
    public SparseInstance(int noAttributes, double defaultValue, Object classValue) {
        super(classValue);
        this.defaultValue = defaultValue;
        this.noAttributes = noAttributes;
    }

    /* defaultValue will be set to 0.0, classValue to null */
    public SparseInstance(double[] datavector) {
        this(datavector, 0.0, null);
    }

    /* classValue will be set to null */
    public SparseInstance(double[] datavector, double defaultValue) {
        this(datavector, defaultValue, null);
    }

    /* defaultValue will be set to 0.0 */
    public SparseInstance(double[] datavector, Object classValue) {
        this(datavector, 0.0, classValue);
    }

    /* Create sparse instance with values from the datavector */
    public SparseInstance(double[] datavector, double defaultValue, Object classValue) {
        super(classValue);
        this.defaultValue = defaultValue;
        initiate(datavector);
    }

    private void initiate(double[] datavector) {
        data.clear();
        noAttributes = datavector.length;
        for (int i = 0; i < datavector.length; i++)
            if (datavector[i] != defaultValue)
                put(i, datavector[i]);
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
    public TreeSet<Integer> keySet() {
        TreeSet<Integer> set = new TreeSet<Integer>();
        set.addAll(data.keySet());
        return set;
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
    @Deprecated
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
        if (noAttributes < 0) {
            if (data.keySet().size() == 0) {
                return 0;
            } else {
                return Collections.max(data.keySet()) + 1;
            }

        } else
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
    public String toString() {
        return "{" + data.toString() + ";" + classValue() + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        long temp;
        temp = Double.doubleToLongBits(defaultValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SparseInstance other = (SparseInstance) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (Double.doubleToLongBits(defaultValue) != Double.doubleToLongBits(other.defaultValue))
            return false;
        return true;
    }

    @Override
    public Instance copy() {
        SparseInstance out = new SparseInstance();
        out.data = new HashMap<Integer, Double>();
        out.data.putAll(this.data);
        out.defaultValue = this.defaultValue;
        out.noAttributes = this.noAttributes;
        out.setClassValue(this.classValue());
        return out;

    }

    @Override
    public void removeAttributes(Set<Integer> indices) {
        List<Integer> indix = new Vector<Integer>();
        indix.addAll(indices);
        Collections.sort(indix);
        for (int i = indix.size() - 1; i >= 0; i--) {
            removeAttribute(indix.get(i));
        }

    }
}
