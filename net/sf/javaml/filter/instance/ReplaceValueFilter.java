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

public class ReplaceValueFilter implements InstanceFilter{

    Double to,from;
    public ReplaceValueFilter(Double from, Double to) {
        this.from=from;
        this.to=to;
    }

    @Override
    public void filter(Instance inst) {
       for(Integer i:inst.keySet()){
           if(inst.get(i).equals(from))
               inst.put(i, to);
       }
        
    }

}
