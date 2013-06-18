/**
 * TestSelfOptimizingSMO.java
 *
 * %SVN.HEADER%
 */
package junit.classification.svm;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import libsvm.SelfOptimizingLinearLibSVM;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;



public class TestSelfOptimizingLinearLibSVM {

	@Test
	public void testSelfOptimizingLinearLibSVM() {
		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4,",");
			System.out.println("Size: " + data.size());
			System.out.println("numAtt: " + data.noAttributes());
			SelfOptimizingLinearLibSVM smo = new SelfOptimizingLinearLibSVM();
			CrossValidation cv = new CrossValidation(smo);
			Map<Object,PerformanceMeasure>results=cv.crossValidation(data, 5, new Random(10));
            
            for(Object o:results.keySet()){
                PerformanceMeasure pm=results.get(o);
                System.out.println(o+"\t"+pm);
            }
		} catch (IOException e) {

			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
}
