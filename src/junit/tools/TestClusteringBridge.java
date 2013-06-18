/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClusterer;

import org.junit.Test;

import weka.clusterers.SimpleKMeans;

public class TestClusteringBridge {

    @Test
    public void testLoadDataset() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            System.out.println(data);
            SimpleKMeans km = new SimpleKMeans();
            km.setNumClusters(3);
            WekaClusterer wc = new WekaClusterer(km);

            Dataset[] clusters = wc.cluster(data);
            for (Dataset d : clusters) {
                System.out.println("cluster:====");
                for (Instance i : d) {
                    System.out.println(i.classValue());
                }
            }
           
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
