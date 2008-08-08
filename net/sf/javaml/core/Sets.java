/**
 * %SVN.HEADER%
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
