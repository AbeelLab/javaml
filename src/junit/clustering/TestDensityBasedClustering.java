/**
 * %SVN.HEADER%
 */
package junit.clustering;

import java.io.File;

import net.sf.javaml.clustering.DensityBasedSpatialClustering;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.ChebychevDistance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestDensityBasedClustering {
    @Test
    public void testDBScan() {
        try {

            Dataset data = FileHandler.loadDataset(new File("devtools/data/test.lst"), ";");
            DensityBasedSpatialClustering dbscan = new DensityBasedSpatialClustering(30, 5, new ChebychevDistance());
            Dataset[] clusters = dbscan.cluster(data);
            for (Dataset ds : clusters) {
                System.out.println(ds.size());
                Assert.assertTrue(ds.size() >= 5);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        }

    }
}
