/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.Instance;

/**
 * 
 * Jaccard index. The distance between two sets is computed in the following
 * way:
 * 
 * <pre>
 *               n1 + n2 - 2*n12 
 * D(S1, S2) = ------------------ 
 *               n1 + n2 - n12
 *               
 *               
 * </pre>
 * 
 * <pre>
 * D(S1,S2) = |S1 ^ S2|
 *            ---------
 *            |S1 u S2|
 * </pre>
 * 
 * 
 * Where n1 and n2 are the numbers of elements in sets S1 and S2, respectively,
 * and n12 is the number that is in both sets (section).
 * 
 * 
 * 
 * @version %SVN.VERSION%
 * 
 * @linkplain http://en.wikipedia.org/wiki/Jaccard_index
 * 
 * @author Thomas Abeel
 * 
 */
public class JaccardIndexSimilarity extends AbstractSimilarity {

    private static final long serialVersionUID = 6715828721744669500L;

    public double measure(Instance a, Instance b) {
        HashSet<Integer> set1 = new HashSet<Integer>();
        HashSet<Integer> set2 = new HashSet<Integer>();

        for (int i = 0; i < a.noAttributes(); i++)
            set1.add((int) a.value(i));

        for (int i = 0; i < b.noAttributes(); i++)
            set2.add((int) b.value(i));

        Set<Integer> union = new HashSet<Integer>();
        union.addAll(set1);
        union.addAll(set2);

        Set<Integer> intersection = new HashSet<Integer>();
        intersection.addAll(set1);
        intersection.retainAll(set2);

        return ((double)intersection.size()) / ((double)union.size());

    }

}
