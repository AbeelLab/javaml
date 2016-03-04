/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

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
public class JaccardIndexDistance extends AbstractDistance {

    private static final long serialVersionUID = 6715828721744669500L;

    private JaccardIndexSimilarity jci;

    public JaccardIndexDistance() {
        this.jci = new JaccardIndexSimilarity();
    }

    public double measure(Instance a, Instance b) {

        return 1 - jci.measure(a, b);

    }

}
