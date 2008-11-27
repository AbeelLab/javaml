/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

import org.junit.Test;

public class TestDenseInstance {
    /**
     * Shows the use of the <code>remove(int index)</code> method on an Instance
     */
    @Test
    public void testRemoveAttribute() {
        double[] values = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Instance instance = new DenseInstance(values);
        System.out.println(instance);
        instance.removeAttribute(4);
        System.out.println(instance);

        instance = new SparseInstance(10);
        instance.put(1, 1.0);
        instance.put(2, 2.0);
        instance.put(4, 4.0);
        instance.put(5, 5.0);
        instance.put(6, 6.0);
        instance.put(8, 8.0);
        System.out.println(instance);
        instance.removeAttribute(5);
        System.out.println(instance);

    }
}
