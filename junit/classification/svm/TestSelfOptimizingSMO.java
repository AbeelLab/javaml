/**
 * TestSelfOptimizingSMO.java
 *
 * %SVN.HEADER%
 */
package junit.classification.svm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

//import net.sf.javaml.classification.svm.SelfOptimizingBinaryLinearSMO;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestSelfOptimizingSMO {

    @Test
    public void testBinaryLinearSMO() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/BUPA.tsv"), 6);
            System.out.println("Size: " + data.size());
            System.out.println("numAtt: " + data.numAttributes());
//            SelfOptimizingBinaryLinearSMO smo = new SelfOptimizingBinaryLinearSMO();
//            smo.buildClassifier(data);
//            System.out.println(smo.getC());
//            System.out.println(Arrays.toString(smo.getWeights()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testPow2(){
        for(int i=-10;i<11;i++)
            System.out.println(Math.pow(2, i));
    }

}
