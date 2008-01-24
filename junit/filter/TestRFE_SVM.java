/**
 * TestRFE_SVM.java
 *
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.filter.attribute.eval.RELIEF;
import net.sf.javaml.filter.attribute.eval.RankingFromEvaluation;
import net.sf.javaml.filter.attribute.eval.RecursiveFeatureEliminationSVM;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestRFE_SVM {

    @Test
    public void testRFE_SVM() {

        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
            // RELIEF rel=new RELIEF();
            RankingFromEvaluation rel = new RankingFromEvaluation(new RELIEF());

            RecursiveFeatureEliminationSVM rfe = new RecursiveFeatureEliminationSVM(2, 1, 0.10);

            System.out.println("REL");
            rel.build(data);
            System.out.println("RFE");
            rfe.build(data);
            for (int i = 0; i < data.numAttributes(); i++) {
                System.out.println(i + "\t" + rel.getRank(i) + "\t" + rfe.getRank(i));
                // System.out.println("SU rank for att " + i + " = " +
                // re2.getRank(i));
                // System.out.println("RFAS rank for att " + i + " = " +
                // re3.getRank(i));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
