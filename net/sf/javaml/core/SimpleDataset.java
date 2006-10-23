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

	public boolean addInstance(Instance i) {
		if (instances.size() >0 && !i.isCompatible(instances.get(0)))
			return false;
		else{
			instances.add(i);
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

	}

	public void removeInstance(int index) {
		instances.remove(index);

	}

	public void clear() {
		instances.removeAllElements();

	}

	public int size() {
		return instances.size();
	}
}
