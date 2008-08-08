/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestDataLoader {

    @Test
    public void testLoader(){
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4,",");
            System.out.println(data);
            data=FileHandler.loadSparseDataset(new File("devtools/data/sparse.tsv"), 0, " ", ":");
            System.out.println(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
