/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClusterer;
import weka.clusterers.XMeans;

/**
 * Tutorial how to use a Weka classifier in Java-ML.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialWekaClusterer {

    public static void main(String[] args) throws Exception {
        /* Load data */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /* Create Weka classifier */
        XMeans xm = new XMeans();
        /* Wrap Weka clusterer in bridge */
        Clusterer jmlxm = new WekaClusterer(xm);
        /* Perform clustering */
        Dataset[] clusters = jmlxm.cluster(data);
        /* Output results */
        System.out.println(clusters.length);
    }
}
