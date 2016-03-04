/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;

import org.junit.Assert;
import org.junit.Test;

public class TestSparseInstance {
	
	
    /**
     * Shows how to construct a SparseInstance.
     */
    @Test
    public void testSparseInstance() {
        /*
         * Here we will create an instance with 10 attributes, but will only set
         * the attributes with index 1,3 and 7 with a value.
         */
        /* Create instance with 10 attributes */
        Instance instance = new SparseInstance(10);
        /* Set the values for particular attributes */
        instance.put(1, 1.0);
        instance.put(3, 2.0);
        instance.put(7, 4.0);

        /* Check whether values are properly set */
        for (int i = 0; i < 10; i++) {
            if (i == 1)
                Assert.assertEquals(1.0, instance.value(i), 1e-6);
            else if (i == 3)
                Assert.assertEquals(2.0, instance.value(i), 1e-6);
            else if (i == 7)
                Assert.assertEquals(4.0, instance.value(i), 1e-6);
            else
                Assert.assertEquals(0.0, instance.value(i), 1e-6);

        }

    }
   
    @Test
    public void testCopy() {
        /*
         * Here we will create an instance with 10 attributes, but will only set
         * the attributes with index 1,3 and 7 with a value.
         */
        /* Create instance with 10 attributes */
        Instance instance = new SparseInstance(10);
        /* Set the values for particular attributes */
        instance.put(1, 1.0);
        instance.put(3, 2.0);
        instance.put(7, 4.0);
        String cv="classValue";
        instance.setClassValue(cv);

        /* Check whether values are properly set */
        for (int i = 0; i < 10; i++) {
            if (i == 1)
                Assert.assertEquals(1.0, instance.value(i), 1e-6);
            else if (i == 3)
                Assert.assertEquals(2.0, instance.value(i), 1e-6);
            else if (i == 7)
                Assert.assertEquals(4.0, instance.value(i), 1e-6);
            else
                Assert.assertEquals(0.0, instance.value(i), 1e-6);

        }
        Instance copy=instance.copy();
        /* Check whether values are properly set */
        for (int i = 0; i < 10; i++) {
            if (i == 1)
                Assert.assertEquals(1.0, copy.value(i), 1e-6);
            else if (i == 3)
                Assert.assertEquals(2.0, copy.value(i), 1e-6);
            else if (i == 7)
                Assert.assertEquals(4.0, copy.value(i), 1e-6);
            else
                Assert.assertEquals(0.0, copy.value(i), 1e-6);

        }
        Assert.assertEquals(cv,copy.classValue());
        

    }
}
