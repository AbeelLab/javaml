/**
 * %SVN.HEADER%
 */
package junit.sampling;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import be.abeel.util.Pair;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestCV {
	@Test
	public void testCV() throws IOException {
		Dataset data=FileHandler.loadDataset(new File("devtools/data/iris.data"),4,",");
		Sampling s=Sampling.SubSampling;
		
		for(int i=0;i<5;i++){
			Pair<Dataset, Dataset> datas=s.sample(data, (int)(data.size()*0.8),i);
			Classifier c=new LibSVM();
			c.buildClassifier(datas.x());
			Map pms=EvaluateDataset.testDataset(c, datas.y());
			System.out.println(pms);
		}
		
		
	}
}
