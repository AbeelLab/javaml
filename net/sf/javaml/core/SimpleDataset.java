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

	private Instance low = null;

	private Instance high = null;

	public boolean addInstance(Instance instance) {
		if (instances.size() == 0) {
			low = new SimpleInstance(instance);
			high = new SimpleInstance(instance);
		}
		if (instances.size() > 0 && !instance.isCompatible(instances.get(0))) {
			return false;
		} else {
			instances.add(instance);
			for (int i = 0; i < instance.size(); i++) {
				if (instance.getValue(i).doubleValue() < low.getValue(i)
						.doubleValue()) {
					low.setValue(i, instance.getValue(i));
				}
				if (instance.getValue(i).doubleValue() > high.getValue(i)
						.doubleValue()) {
					high.setValue(i, instance.getValue(i));
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
		low = null;
		high = null;
	}

	private void recalculate() {
		if(instances.size()==0){
			low=null;
			high=null;
		}else{
			// TODO recalculate to high and low instances.
			// TODO currently when ones removes an item from the dataset, the min
			// and max instance may be broken.

		}
			
	}

	public int size() {
		return instances.size();
	}

	public Instance getMaximumInstance() {
		return high;
	}

	public Instance getMinimumInstance() {
		return low;
	}
}
