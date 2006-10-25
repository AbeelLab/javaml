/**
 * SimpleDataset.java, 11-okt-06
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.core;

import java.util.Vector;

public class SimpleDataset implements Dataset {
    private Vector<Instance> instances = new Vector<Instance>();

    double[] lowArray,highArray;

    public boolean addInstance(Instance instance) {
        
        if (instances.size() == 0) {
            lowArray = instance.getArrayForm();
            highArray = instance.getArrayForm();
        }
        if (instances.size() > 0 && !instance.isCompatible(instances.get(0))) {
            return false;
        } else {
            instances.add(instance);
            for (int i = 0; i < instance.size(); i++) {
                if (instance.getValue(i) < lowArray[i]) {
                    lowArray[i]=instance.getValue(i);
                }
                if (instance.getValue(i) > highArray[i]) {
                    highArray[i]=instance.getValue(i);
                }
            }
            return true;
        }

    }

    public int getIndex(Instance i) {
        return instances.indexOf(i);
    }

    public Instance getInstance(int index) {
        return instances.get(index);
    }

    public void removeInstance(Instance i) {
        instances.remove(i);
        recalculate();
    }

    public void removeInstance(int index) {
        instances.remove(index);
        recalculate();
    }

    public void clear() {
        instances.removeAllElements();
        lowArray = null;
        highArray = null;
    }

    private void recalculate() {
        if (instances.size() == 0) {
            lowArray = null;
            highArray = null;
        } else {
            lowArray = instances.get(0).getArrayForm();
            highArray = instances.get(0).getArrayForm();
            for (int j = 1; j < instances.size(); j++) {
                Instance instance = instances.get(j);
                for (int i = 0; i < instance.size(); i++) {
                    if (instance.getValue(i) < lowArray[i]) {
                        lowArray[i]=instance.getValue(i);
                    }
                    if (instance.getValue(i) > highArray[i]) {
                        highArray[i]=instance.getValue(i);
                    }
                }
            }
        }

    }

    public int size() {
        return instances.size();
    }

    public Instance getMaximumInstance() {
        return new SimpleInstance(highArray);
    }

    public Instance getMinimumInstance() {
        return new SimpleInstance(lowArray);
    }
}
