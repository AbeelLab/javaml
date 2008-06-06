package net.sf.javaml.core;

import java.util.HashMap;

public class SparseInstance extends HashMap<Integer, Double> implements Instance {

    private static final long serialVersionUID = -7642462956857985858L;

    private int noAttributes;

    public SparseInstance(int noAttributes) {
        this.noAttributes = noAttributes;
    }

    private Object classValue = null;

    @Override
    public Object classValue() {
        return classValue;
    }

    @Override
    public int noAttributes() {
        return noAttributes;
    }

    @Override
    public double value(int pos) {
        return super.get(pos).doubleValue();
    }

}
