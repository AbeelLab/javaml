/**
 * %SVN.HEADER%
 */
package tutorials.core;

import java.util.SortedSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;

import org.junit.Assert;
import org.junit.Test;

/**
 * This tutorial show how to create a {@link net.sf.javaml.core.Dataset} from a
 * collection of instances. This tutorial assumes you know how to create an
 * {@link net.sf.javaml.core.Instance}. To create instances for this tutorial
 * we will use a method from {@link net.sf.javaml.core.InstanceTools} to create
 * random instances.
 * 
 * In this tutorial we will create a number of instances and group them in a
 * data set.
 * 
 * Basically a data set is a collection of instances.
 * 
 * {@jmlSource}
 * 
 * @see CreatingAnInstance
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.Dataset
 * @see net.sf.javaml.core.DefaultDataset
 * @see net.sf.javaml.core.InstanceTools
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialDataset {

    /**
     * Create a data set and put some instances in it.
     */
    @Test
    public void testConstruction() {
        Dataset data = new DefaultDataset();
        for (int i = 0; i < 10; i++) {
            Instance tmpInstance = InstanceTools.randomInstance(25);
            data.add(tmpInstance);
        }
        /* Retrieve all class values that are ever used in the data set */
        SortedSet<Object> classValues = data.classes();
        

        Assert.assertEquals(data.noAttributes(), 25);
        Assert.assertEquals(data.size(), 10);
        Assert.assertEquals(classValues.size(), 0);

    }
}
