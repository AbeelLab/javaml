/**
 * %SVN.HEADER%
 */
package junit.clustering;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestKMeans {

    @Test
    public void testKMean() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            Clusterer km = new KMeans();
            Dataset[]clusters=km.cluster(data);
            System.out.println("Cluster count: "+clusters.length);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
