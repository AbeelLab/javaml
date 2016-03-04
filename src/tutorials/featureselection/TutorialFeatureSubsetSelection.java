/**
 * %SVN.HEADER%
 */
package tutorials.featureselection;

import java.io.File;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.tools.data.FileHandler;

/**
 * Shows the basic steps to create use a feature subset selection algorithm.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialFeatureSubsetSelection {

    public static void main(String[] args) throws Exception {
        /* Load the iris data set */
        Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
        /*
         * Construct a greedy forward subset selector that will use the Pearson
         * correlation to determine the relation between each attribute and the
         * class label. The first parameter indicates that only one, i.e. 'the
         * best' attribute will be selected.
         */
        GreedyForwardSelection ga = new GreedyForwardSelection(1, new PearsonCorrelationCoefficient());
        /* Apply the algorithm to the data set */
        ga.build(data);
        /* Print out the attribute that has been selected */
        System.out.println(ga.selectedAttributes());
    }
}
