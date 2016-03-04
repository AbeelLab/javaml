/**
 * %SVN.HEADER%
 */
package tutorials.core;

import java.util.SortedSet;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.InstanceTools;

/**
 * This tutorial show how to create a {@link net.sf.javaml.core.Dataset} from a
 * collection of instances. This tutorial assumes you know how to create an
 * {@link net.sf.javaml.core.Instance}. To create instances for this tutorial
 * we will use a method from {@link net.sf.javaml.tools.InstanceTools} to create
 * random instances.
 * 
 * In this tutorial we will create a number of instances and group them in a
 * data set.
 * 
 * Basically a data set is a collection of instances.
 * 
 * 
 * @see CreatingAnInstance
 * @see net.sf.javaml.core.Instance
 * @see net.sf.javaml.core.Dataset
 * @see net.sf.javaml.core.DefaultDataset
 * @see net.sf.javaml.tools.InstanceTools
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
   public static void main(String[]args){
        Dataset data = new DefaultDataset();
        for (int i = 0; i < 10; i++) {
            Instance tmpInstance = InstanceTools.randomInstance(25);
            data.add(tmpInstance);
        }
        /* Retrieve all class values that are ever used in the data set */
        SortedSet<Object> classValues = data.classes();
        System.out.println(classValues);
   }	
}
