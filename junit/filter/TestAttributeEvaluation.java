/**
 * TestRandomForestAttributeSelection.java
 *
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.filter.attribute.eval.GainRatioAttributeEvaluation;
import net.sf.javaml.filter.attribute.eval.IAttributeRanking;
import net.sf.javaml.filter.attribute.eval.RELIEF;
import net.sf.javaml.filter.attribute.eval.RandomForestAttributeEvaluation;
import net.sf.javaml.filter.attribute.eval.RankingFromEvaluation;
import net.sf.javaml.filter.attribute.eval.SymmetricalUncertaintyAttributeEvaluation;
import net.sf.javaml.filter.attribute.eval.meta.LinearRankingEnsemble;
import net.sf.javaml.filter.attribute.eval.meta.QuadraticRankingEnsemble;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestAttributeEvaluation {

   

    @Test
    public void testRTAS() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/pima.tsv"), 8);
            // DatasetFilter filter=new EqualFrequencyBinning();
            // data=filter.filterDataset(data);
            // Dataset data = FileHandler.loadDataset(new
            // File("devtools/data/BUPA.tsv"), 6);
            IAttributeRanking[] grs = new IAttributeRanking[10];
            for (int i = 0; i < grs.length; i++)
                grs[i] = new RankingFromEvaluation(new GainRatioAttributeEvaluation());

            LinearRankingEnsemble lre = new LinearRankingEnsemble(grs);
            lre.build(data);
            
            
            IAttributeRanking[] grs2 = new IAttributeRanking[50];
            for (int i = 0; i < grs2.length; i++)
                grs2[i] = new RankingFromEvaluation(new RELIEF(5));

            LinearRankingEnsemble lre2 = new LinearRankingEnsemble(grs2);
            lre2.build(data);
            
            IAttributeRanking[] grs3 = new IAttributeRanking[50];
            for (int i = 0; i < grs3.length; i++)
                grs3[i] = new RankingFromEvaluation(new SymmetricalUncertainty());

            LinearRankingEnsemble lre3 = new LinearRankingEnsemble(grs3);
            lre3.build(data);
            
            IAttributeRanking[] grs4 = new IAttributeRanking[50];
            for (int i = 0; i < grs4.length; i++)
                grs2[i] = new RankingFromEvaluation(new RELIEF(5));

            QuadraticRankingEnsemble lre4 = new QuadraticRankingEnsemble(grs2);
            lre4.build(data);
            
            
            RandomForestAttributeEvaluation rfas = new RandomForestAttributeEvaluation(20, 1);

            GainRatioAttributeEvaluation gr = new GainRatioAttributeEvaluation();
            SymmetricalUncertainty su = new SymmetricalUncertainty();
            RELIEF rel = new RELIEF();
            gr.build(data);
            su.build(data);
            rfas.build(data);
            rel.build(data);
            for (int i = 0; i < data.numAttributes(); i++) {
                System.out.println("GR " + i + ": " + gr.evaluateAttribute(i));
                System.out.println("SU " + i + ": " + su.evaluateAttribute(i));
                System.out.println("RFAS " + i + ": " + rfas.evaluateAttribute(i));
                System.out.println("REL " + i + ": " + rel.evaluateAttribute(i));
               
            }

            RankingFromEvaluation re1 = new RankingFromEvaluation(gr);
            RankingFromEvaluation re2 = new RankingFromEvaluation(su);
            RankingFromEvaluation re3 = new RankingFromEvaluation(rfas);
            RankingFromEvaluation re4 = new RankingFromEvaluation(rel);

            re1.build(data);
            re2.build(data);
            re3.build(data);
            re4.build(data);

            System.out.println("att\tGR\tSU\tRFAS\tREL\tlre\tlre2\tlre3\tqre");
            for (int i = 0; i < data.numAttributes(); i++) {
                System.out.println(i + "\t" + re1.getRank(i) + "\t" + re2.getRank(i) + "\t" + re3.getRank(i) + "\t"
                        + re4.getRank(i)+ "\t" +lre.getRank(i)+ "\t" +lre2.getRank(i)+ "\t" +lre3.getRank(i)+ "\t" +lre4.getRank(i));
                // System.out.println("SU rank for att " + i + " = " +
                // re2.getRank(i));
                // System.out.println("RFAS rank for att " + i + " = " +
                // re3.getRank(i));
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
