/**
 * %SVN.HEADER%
 */
package junit.libsvm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;
import libsvm.LibSVM;

import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;
/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestLibSVM {

	@Test
	public void testIris(){
		try {
			Dataset data=FileHandler.loadDataset(new File("devtools/data/iris.data"),4,",");
			LibSVM svm=new LibSVM();
			svm.buildClassifier(data);
			System.out.println(EvaluateDataset.testDataset(svm, data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testBinaryIris(){
		try {
			Dataset data=FileHandler.loadDataset(new File("devtools/data/binaryiris.data"),4,",");
			LibSVM svm=new LibSVM();
			svm.buildClassifier(data);
			System.out.println(EvaluateDataset.testDataset(svm, data));
			for(Instance i:data){
				System.out.println("R="+Arrays.toString(svm.rawDecisionValues(i)));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
	}
}
