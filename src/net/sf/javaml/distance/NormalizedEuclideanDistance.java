/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetTools;

/**
 * A normalized version of the Euclidean distance. This distance measure is
 * normalized in the interval [0,1].
 * 
 * High values denote low similar items (high distance) and low values denote
 * highly similar items (low distance).
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizedEuclideanDistance extends EuclideanDistance {

    /**
     * 
     */
    private static final long serialVersionUID = -6489071802740149683L;

    private Dataset data;

    public NormalizedEuclideanDistance(Dataset data) {
        super();
        this.data = data;
    }

    public double measure(Instance i, Instance j) {
        Instance min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);

        Instance normI = normalizeMidrange(0.5, 1, min, max, i);
        Instance normJ = normalizeMidrange(0.5, 1, min, max, j);
        return super.calculateDistance(normI, normJ) / Math.sqrt(i.noAttributes());

    }

    private Instance normalizeMidrange(double normalMiddle, double normalRange, Instance min, Instance max,
            Instance instance) {
        double[] out = new double[instance.noAttributes()];
        for (int i = 0; i < out.length; i++) {
            double range = Math.abs(max.value(i) - min.value(i));
            double middle = Math.abs(max.value(i) + min.value(i)) / 2;
            out[i] = ((instance.value(i) - middle) / range) * normalRange + normalMiddle;
        }
        return new DenseInstance(out, instance);
    }
}
