/*
 * ReplaceValueFilter.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package net.sf.javaml.filter.instance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.InstanceFilter;

/**
 * Filter to replace all values with their ceiled equivalent.
 * 
 * @author Thomas Abeel
 * 
 */
public class CeilValueFilter implements InstanceFilter {

    @Override
    public void filterInstance(Instance inst) {
        for (Integer i : inst.keySet()) {
            inst.put(i, (double) (int) (inst.get(i) + 1.0));
        }

    }

}
