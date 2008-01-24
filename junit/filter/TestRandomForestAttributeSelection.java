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
import net.sf.javaml.filter.attribute.eval.RELIEF;
import net.sf.javaml.filter.attribute.eval.RandomForestAttributeEvaluation;
import net.sf.javaml.filter.attribute.eval.RankingFromEvaluation;
import net.sf.javaml.filter.attribute.eval.SymmetricalUncertaintyAttributeEvaluation;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestRandomForestAttributeSelection {

    @Test
    public void testRTAS() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
            // DatasetFilter filter=new EqualFrequencyBinning();
            // data=filter.filterDataset(data);

            RandomForestAttributeEvaluation rfas = new RandomForestAttributeEvaluation(20, 0);

            GainRatioAttributeEvaluation gr = new GainRatioAttributeEvaluation();
            SymmetricalUncertaintyAttributeEvaluation su = new SymmetricalUncertaintyAttributeEvaluation();
            RELIEF rel=new RELIEF();
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
            
            RankingFromEvaluation re1=new RankingFromEvaluation(gr);
            RankingFromEvaluation re2=new RankingFromEvaluation(su);
            RankingFromEvaluation re3=new RankingFromEvaluation(rfas);
            RankingFromEvaluation re4=new RankingFromEvaluation(rel);
            
            re1.build(data);
            re2.build(data);
            re3.build(data);
            re4.build(data);

            System.out.println("att\tGR\tSU\tRFAS\tREL");
            for (int i = 0; i < data.numAttributes(); i++) {
                System.out.println(i+"\t"+ re1.getRank(i)+"\t"+re2.getRank(i)+"\t"+re3.getRank(i)+"\t"+re4.getRank(i));
//                System.out.println("SU rank for att " + i + " = " + re2.getRank(i));
//                System.out.println("RFAS rank for att " + i + " = " + re3.getRank(i));
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
