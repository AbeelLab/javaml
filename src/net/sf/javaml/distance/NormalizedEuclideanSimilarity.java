/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * XXX DOC
 */
public class NormalizedEuclideanSimilarity extends AbstractSimilarity {
    /**
     * 
     */
    private static final long serialVersionUID = 3840902505037993972L;
    /**
     * XXX DOC
     */
    private NormalizedEuclideanDistance dm;

    /**
     * XXX DOC
     */
    public NormalizedEuclideanSimilarity(Dataset data) {
        super();
        this.dm = new NormalizedEuclideanDistance(data);
    }

    /**
     * XXX DOC
     */
    public double measure(Instance i, Instance j) {
        return 1 - dm.calculateDistance(i, j);
    }

 

}
