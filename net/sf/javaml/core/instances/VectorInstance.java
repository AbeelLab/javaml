/**
 * VectorInstance.java, 11-okt-06
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

package net.sf.javaml.common.instances;

import java.util.Vector;

import net.sf.javaml.common.Instance;
import net.sf.javaml.common.Value;

public class VectorInstance extends AbstractInstance {

	private Vector<Value> values = new Vector<Value>();

    
	public boolean isCompatible(Instance instance) {
		boolean out = true;
		if (values.size() != instance.getValues().size())
			return false;
		for (int i = 0; i < values.size() && out; i++)
			out = out
					&& values.get(i).getClass().equals(
							instance.getValue(i).getClass());
		return out;
	}

	

	public Value getValue(int index) {
		return values.get(index);
	}

    public void setValue(int index, Value a) {
      values.set(index,a);

    }
    
	public Vector<Value> getValues() {
		return values;
	}
    
    public void setValues(Vector<Value> vec){
        values=vec;
    }

	

	

	

}
