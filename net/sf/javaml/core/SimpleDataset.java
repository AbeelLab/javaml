/**
 * SimpleDataset.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.core;

import java.util.Vector;

/**
 * XXX write doc
 * 
 * @author Thomas Abeel
 * 
 */
public class SimpleDataset implements Dataset {
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
    private float[] lowArray, highArray;

    /**
     * XXX doc
     */
    public boolean addInstance(Instance instance) {

        if (instances.size() == 0) {
            lowArray = instance.toArray();
            highArray = instance.toArray();
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

}
