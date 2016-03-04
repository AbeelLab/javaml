/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.ARFFHandler;

/**
 * Demonstrates how you can load data from an ARFF formatted file.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialARFFLoader {

    public static void main(String[] args) throws Exception {
        Dataset data = ARFFHandler.loadARFF(new File("devtools/data/iris.arff"), 4);
        System.out.println(data);

    }
}
