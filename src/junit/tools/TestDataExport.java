/**
 * %SVN.HEADER%
 */
package junit.tools;


import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestDataExport {

    @Test
    public void testDataExport() {
        Dataset data = new DefaultDataset();
        for (int i = 0; i < 10; i++) {
            Instance tmpInstance = InstanceTools.randomInstance(25);
            data.add(tmpInstance);
        }
        try {
            FileHandler.exportDataset(data, new File("test.out.tsv"), true);
            new File("test.out.tsv.gz").delete();
        } catch (IOException e) {
           Assert.assertTrue(false);
        }
    }
}
