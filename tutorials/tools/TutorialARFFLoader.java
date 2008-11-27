/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.ARFFHandler;

import org.junit.Test;

/**
 * Demonstrates how you can load data from an ARFF formatted file.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialARFFLoader {
    @Test
    public void testLoader() {
        try {
            Dataset data = ARFFHandler.loadARFF(new File("devtools/data/iris.arff"), 4);
            System.out.println(data);
            
        } catch (IOException e) {
            Assert.assertFalse(true);
        }
    }
}
