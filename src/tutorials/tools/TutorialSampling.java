/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;
import java.util.Map;

import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.data.FileHandler;
import be.abeel.util.Pair;

/**
 * Sample program illustrating how to use sampling.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialSampling {

	public static void main(String[] args) throws Exception {

		Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");

		Sampling s = Sampling.SubSampling;

		for (int i = 0; i < 5; i++) {
			Pair<Dataset, Dataset> datas = s.sample(data, (int) (data.size() * 0.8), i);
			Classifier c = new LibSVM();
			c.buildClassifier(datas.x());
			Map<Object,PerformanceMeasure> pms = EvaluateDataset.testDataset(c, datas.y());
			System.out.println(pms);

		}

	}
}
