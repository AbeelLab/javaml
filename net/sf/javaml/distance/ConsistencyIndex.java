/**
 * ConsistencyIndex.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import java.util.HashSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

public class ConsistencyIndex extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -9108138773263724130L;
    private int n;

    public ConsistencyIndex(int n){
        this.n=n;
    }
    
    public double calculateDistance(Instance a, Instance b) {
        HashSet<Integer> set1 = new HashSet<Integer>();
        HashSet<Integer> set2 = new HashSet<Integer>();

        
        for (int i = 0; i < a.size(); i++)
            set1.add((int)a.value(i));

        for (int i = 0; i < b.size(); i++)
            set2.add((int)b.value(i));

        double k = set1.size();
        set1.retainAll(set2);
        double r = set1.size();
//        System.out.println("n="+n+";"+"r="+r+";"+"k="+k);
        return (r*n-k*k)/(k*(n-k)); 

    }

    @Override
    public double getMaximumDistance(Dataset data) {
        return 1;
    }

    @Override
    public double getMinimumDistance(Dataset data) {
        return 0;
    }
}
