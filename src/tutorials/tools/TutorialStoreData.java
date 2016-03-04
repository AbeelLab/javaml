/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

/**
 * Demonstrates how you can store data to a file.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialStoreData {

    public static void main(String[] args) throws Exception {
        /* Load the iris data set from file */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        System.out.println(data);
        /* Store the data back to another file */
        FileHandler.exportDataset(data, new File("output.txt"));

    }
}
