/**
 * TestLibSVM.java
 *
 * %SVN.HEADER%
 */
package junit.classification.svm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import external.libsvm.LibSVM;

public class TestLibSVM {

    @Test
    public void testLibSVM() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
            LibSVM ls = new LibSVM();
//            ls.buildClassifier(data);

            CrossValidation cv = new CrossValidation(ls);
            PerformanceMeasure pm=cv.crossValidation(data, 0, 4);
            PerformanceMeasure pm2=cv.crossValidation(data, 1, 4);
            System.out.println("Class0");
            System.out.println(pm);
            System.out.println("\t"+pm.getFMeasure());
            System.out.println(Arrays.toString(ls.getWeights()));
            System.out.println("Class1");
            System.out.println(pm2);
            System.out.println("\t"+pm2.getFMeasure());
            
            System.out.println(Arrays.toString(ls.getWeights()));
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
