/**
 * TestLogistic.java
 *
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.util.Random;

import net.sf.javaml.classification.Logistic;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaUtils;

import org.junit.Assert;
import org.junit.Test;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class TestLogistic {
    @Test
    public void testLogistic() {
        try {

            Dataset data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
            doJavaML(data);
            doWeka(data);

          
            data = FileHandler.loadDataset(new File("devtools/data/iris.tsv"), 4);
            doJavaML(data);
            doWeka(data);

           
            data = FileHandler.loadDataset(new File("devtools/data/pima.tsv"), 8);
            doJavaML(data);
            doWeka(data);

         
            data = FileHandler.loadDataset(new File("devtools/data/wdbc.csv"), 1, ",");
            doJavaML(data);
            doWeka(data);

           

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    private void doWeka(Dataset data) throws Exception {
        weka.classifiers.functions.Logistic wekaL = new weka.classifiers.functions.Logistic();
        Instances wData = WekaUtils.datasetToWeka(data);
        Evaluation eval = new Evaluation(wData);
        eval.crossValidateModel(wekaL, wData, 10, new Random());
        System.out.println("wekaL\t" + eval.fMeasure(0)+"\t"+eval.fMeasure(1));
        
    }

    private void doJavaML(Dataset data) {
        Logistic l = new Logistic();
        CrossValidation cv = new CrossValidation(l);
        System.out.println("Java-ML-0:"+cv.crossValidation(data, 0, 5).getFMeasure());
        System.out.println("Java-ML-1:"+cv.crossValidation(data, 1, 5).getFMeasure());
    }
}
