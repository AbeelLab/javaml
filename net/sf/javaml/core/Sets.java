/*
 * Sets.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package net.sf.javaml.core;

import java.util.HashSet;
import java.util.Set;

public class Sets {

    public static  Set<Integer> union(Set<? extends Integer> a, Set<? extends Integer> b) {
        Set<Integer>out=new HashSet<Integer>();
        out.addAll(a);
        out.addAll(b);
        return out;

    }
    
    public static  Set<Integer> intersection(Set<? extends Integer> a, Set<? extends Integer> b) {
        Set<Integer>out=new HashSet<Integer>();
        out.addAll(a);
        out.retainAll(b);
        return out;
    }
}
