/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.Assert;
import libsvm.LibSVM;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.ARFFHandler;

import org.junit.Test;


public class TestARFFHandler {

    @Test
    public void testARFFHandlerIris() {
        try {
            Dataset data = ARFFHandler.loadARFF(new File("devtools/data/iris.arff"), 4);
            Assert.assertEquals(4, data.noAttributes());
            Assert.assertEquals(150, data.size());
            
            LibSVM svm=new LibSVM();
            CrossValidation cv=new CrossValidation(svm);
            System.out.println(cv.crossValidation(data, 10));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}
