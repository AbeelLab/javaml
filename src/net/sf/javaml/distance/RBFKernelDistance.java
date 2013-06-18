/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Instance;

public class RBFKernelDistance extends AbstractDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -7024287104968480288L;

    public double measure(Instance x, Instance y) {

        return 1 - new RBFKernel().measure(x, y);
    }

}
