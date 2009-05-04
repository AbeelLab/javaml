/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import net.sf.javaml.classification.ZeroR;
import net.sf.javaml.classification.evaluation.EvaluateDataset;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.classification.meta.Bagging;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.sampling.NormalBootstrapping;

public class TestBagging {

	@Test
	public void testBagging() throws IOException {
		ZeroR[] zeros = new ZeroR[10];
		for (int i = 0; i < zeros.length; i++) {
			zeros[i] = new ZeroR();
		}
		Dataset data = FileHandler.loadDataset(new File(
				"devtools/data/colon.csv.gz"), 0, ",");
		Bagging bagger = new Bagging(zeros, new NormalBootstrapping(), 0);
		bagger.buildClassifier(data);
		Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(
				bagger, data);
	}
}
