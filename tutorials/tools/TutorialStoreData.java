/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

/**
 * Demonstrates how you can store data to a file.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialStoreData {
    @Test
    public void testLoader() {
        try {
            /* Load the iris data set from file */
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            System.out.println(data);
            /* Store the data back to another file */
            FileHandler.exportDataset(data,new File("output.txt"));

        } catch (IOException e) {
            Assert.assertFalse(true);
        }
    }
}
