/**
 * %SVN.HEADER%
 */
package junit.classification;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.exception.TrainingRequiredException;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

import be.abeel.util.TimeInterval;
/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestKNN {

	@Test (expected=TrainingRequiredException.class)
	public void testNoTraining(){
		
		KNearestNeighbors knn=new KNearestNeighbors(1);
		knn.classDistribution(InstanceTools.randomInstance(5));
		
	}
	
	@Test
	public void testSingleClass2() {
		try {
			Dataset data = new DefaultDataset();
			for (int i = 0; i < 10; i++) {
				Instance is = InstanceTools.randomInstance(5);
				is.setClassValue("class");
				data.add(is);
			}
			KNearestNeighbors knn = new KNearestNeighbors(1);
			knn.buildClassifier(data);
			Map<Object, Double> distr = knn.classDistribution(InstanceTools.randomInstance(5));
			System.out.println(distr);
			Assert.assertFalse(Double.isNaN(distr.get("class")));
			
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	public void testSingleClass(){
		Dataset data=new DefaultDataset();
		for(int i=0;i<10;i++){
			Instance is=InstanceTools.randomInstance(5);
			is.setClassValue("class");
		}
		KNearestNeighbors knn=new KNearestNeighbors(1);
		knn.buildClassifier(data);
		Map<Object,Double>distr=knn.classDistribution(InstanceTools.randomInstance(5));
		System.out.println(distr);
	}
	
    @Test
    public void testKNNIris() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            System.out.println("Loader: " + data.classes());
            Classifier knn = new KNearestNeighbors(5);
            CrossValidation cv = new CrossValidation(knn);
            Map<Object, PerformanceMeasure> p = cv.crossValidation(data, 5, new Random(10));
            System.out.println(p);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Test KNN usage on the sparse sample.
     * 
     */
    @Test
    public void testSparseKNN() {

        try {
            /* Load a data set */
            Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, "\t", ":");
            /*
             * Contruct a KNN classifier that uses 5 neighbors to make a
             * decision.
             */
            Classifier knn = new KNearestNeighbors(5);
            knn.buildClassifier(data);
            System.out.println("Building complete!");
            /*
             * Load a data set, this can be a different one, but we will use the
             * same one.
             */
            Dataset dataForClassification = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0,
                    "\t", ":");
            /* Counters for correct and wrong predictions. */
            int correct = 0, wrong = 0;
            int count = 0;
            /* Classify all instances and check with the correct class values */
            for (int i = 0; i < 15; i++) {
                Instance inst = dataForClassification.instance(i);

                long time = System.currentTimeMillis();
                System.out.print("Processing instance: " + ++count + "\t");
                Object predictedClassValue = knn.classify(inst);
                Object realClassValue = inst.classValue();
                if (predictedClassValue.equals(realClassValue))
                    correct++;
                else
                    wrong++;
                System.out.println(new TimeInterval(System.currentTimeMillis() - time));

            }
            System.out.println("Correct predictions  " + correct);
            System.out.println("Wrong predictions " + wrong);

        } catch (IOException e) {
            Assert.assertTrue(false);
        }

    }
}
