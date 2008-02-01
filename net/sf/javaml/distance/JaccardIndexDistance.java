/**
 * TanimotoDistance.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import java.util.HashSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * 
 * Tanimoto distance. The distance between two sets is computed in the following
 * way:
 * 
 * <pre>
 *               n1 + n2 - 2*n12 
 * D(S1, S2) = ------------------ 
 *               n1 + n2 - n12
 * </pre>
 * 
 * Where n1 and n2 are the numbers of elements in sets S1 and S2, respectively,
 * and n12 is the number that is in both sets (section).
 * 
 * {@jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class JaccardIndexDistance extends AbstractDistance {

    private static final long serialVersionUID = 6715828721744669500L;

    public double calculateDistance(Instance a, Instance b) {
        HashSet<Integer> set1 = new HashSet<Integer>();
        HashSet<Integer> set2 = new HashSet<Integer>();

        for (int i = 0; i < a.size(); i++)
            set1.add((int)a.value(i));

        for (int i = 0; i < b.size(); i++)
            set2.add((int)b.value(i));

        double n1 = set1.size();
        double n2 = set2.size();

        set1.retainAll(set2);

        double n12 = set1.size();

        return 1 - (n1 + n2 - 2 * n12) / (n1 + n2 - n12);

    }

    public double getMaximumDistance(Dataset data) {
        return 1;
    }

    public double getMinimumDistance(Dataset data) {
        return 0;
    }

}
