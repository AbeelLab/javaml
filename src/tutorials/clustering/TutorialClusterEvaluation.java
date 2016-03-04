/**
 * %SVN.HEADER%
 */
package tutorials.clustering;

import java.io.File;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.AICScore;
import net.sf.javaml.clustering.evaluation.BICScore;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

/**
 * Shows how to use the different cluster evaluation measure that are
 * implemented in Java-ML.
 * 
 * @see net.sf.javaml.clustering.evaluation.*
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialClusterEvaluation {

    public static void main(String[] args) throws Exception {
        /* Load a dataset */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /*
         * Create a new instance of the KMeans algorithm that will create 3
         * clusters and create one that will make 4 clusters.
         */
        Clusterer km3 = new KMeans(3);
        Clusterer km4 = new KMeans(4);
        /*
         * Cluster the data, we will create 3 and 4 clusters.
         */
        Dataset[] clusters3 = km3.cluster(data);
        Dataset[] clusters4 = km4.cluster(data);

        ClusterEvaluation aic = new AICScore(3);
        ClusterEvaluation bic = new BICScore(3);
        ClusterEvaluation sse = new SumOfSquaredErrors();

        double aicScore3 = aic.score(clusters3);
        double bicScore3 = bic.score(clusters3);
        double sseScore3 = sse.score(clusters3);

        double aicScore4 = aic.score(clusters4);
        double bicScore4 = bic.score(clusters4);
        double sseScore4 = sse.score(clusters4);

        System.out.println("AIC score: " + aicScore3+"\t"+aicScore4);
        System.out.println("BIC score: " + bicScore3+"\t"+bicScore4);
        System.out.println("Sum of squared errors: " + sseScore3+"\t"+sseScore4);
        
    }
}
