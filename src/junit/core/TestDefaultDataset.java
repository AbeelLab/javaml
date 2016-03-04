/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.NormalizedEuclideanSimilarity;
import net.sf.javaml.tools.InstanceTools;

import org.junit.Assert;
import org.junit.Test;

public class TestDefaultDataset {
	static final double delta = 0.000001;

	@Test
	public void testKnearest() {
		DefaultDataset data = new DefaultDataset();
		for (int i = 0; i < 5; i++)
			data.add(InstanceTools.randomInstance(1));
		Instance five = new DenseInstance(new double[] { 0.5 });
		Assert.assertTrue(data.kNearest(1, five, new EuclideanDistance())
				.size() == 1);
		Assert.assertTrue(data.kNearest(2, five, new EuclideanDistance())
				.size() == 2);
		Assert.assertEquals(data.kNearest(1, five, new EuclideanDistance()),
				data.kNearest(1, five, new NormalizedEuclideanSimilarity(data)));
		Assert.assertEquals(data.kNearest(2, five, new EuclideanDistance()),
				data.kNearest(2, five, new NormalizedEuclideanSimilarity(data)));
	}

	@Test
	public void testKnearestMissingValues() {
		DefaultDataset data = new DefaultDataset();
		for (int i = 0; i < 5; i++)
			data.add(InstanceTools.randomInstance(1));
		data.get(0).put(0, Double.NaN);

		// Instance five = new DenseInstance(new double[] { 0.5 });
		Assert.assertEquals(1,
				data.kNearest(1, data.get(0), new EuclideanDistance()).size());

	}

	@Test
	public void testDatasetRemove() {
		Dataset data = new DefaultDataset();
		double[] val = new double[] { 0.0 };
		data.add(new DenseInstance(val));
		val[0]++;
		data.add(new DenseInstance(val));
		val[0]++;
		data.add(new DenseInstance(val));
		val[0]++;
		data.add(new DenseInstance(val));
		val[0]++;
		data.add(new DenseInstance(val));
		val[0]++;
		data.add(new DenseInstance(val));
		val[0]++;
		Assert.assertEquals(data.size(), 6);
		Assert.assertEquals(data.get(0).value(0), 0.0, delta);
		Assert.assertEquals(data.get(1).value(0), 1.0, delta);
		Assert.assertEquals(data.get(2).value(0), 2.0, delta);
		Assert.assertEquals(data.get(3).value(0), 3.0, delta);
		Assert.assertEquals(data.get(4).value(0), 4.0, delta);
		Assert.assertEquals(data.get(5).value(0), 5.0, delta);

		Instance i = data.remove(4);
		Assert.assertEquals(i.value(0), 4.0, delta);
		Assert.assertEquals(data.size(), 5);

		Assert.assertEquals(data.get(0).value(0), 0.0, delta);
		Assert.assertEquals(data.get(1).value(0), 1.0, delta);
		Assert.assertEquals(data.get(2).value(0), 2.0, delta);
		Assert.assertEquals(data.get(3).value(0), 3.0, delta);
		Assert.assertEquals(data.get(4).value(0), 5.0, delta);

	}

	@Test
	public void testDatasetRemoveClass() {
		Dataset data = new DefaultDataset();
		double[] val = new double[] { 0.0 };
		data.add(new DenseInstance(val, "0"));
		val[0]++;
		data.add(new DenseInstance(val, "1"));
		val[0]++;
		data.add(new DenseInstance(val, "2"));
		val[0]++;
		data.add(new DenseInstance(val, "3"));
		val[0]++;
		data.add(new DenseInstance(val, "4"));
		val[0]++;
		data.add(new DenseInstance(val, "5"));
		val[0]++;
		Assert.assertEquals(data.size(), 6);
		Assert.assertEquals(data.classes().size(), 6);
		Assert.assertEquals(data.get(0).value(0), 0.0, delta);
		Assert.assertEquals(data.get(1).value(0), 1.0, delta);
		Assert.assertEquals(data.get(2).value(0), 2.0, delta);
		Assert.assertEquals(data.get(3).value(0), 3.0, delta);
		Assert.assertEquals(data.get(4).value(0), 4.0, delta);
		Assert.assertEquals(data.get(5).value(0), 5.0, delta);

		Instance i = data.remove(4);
		Assert.assertEquals(i.value(0), 4.0, delta);
		Assert.assertEquals(data.size(), 5);
		Assert.assertEquals(6, data.classes().size());

		Assert.assertEquals(data.get(0).value(0), 0.0, delta);
		Assert.assertEquals(data.get(1).value(0), 1.0, delta);
		Assert.assertEquals(data.get(2).value(0), 2.0, delta);
		Assert.assertEquals(data.get(3).value(0), 3.0, delta);
		Assert.assertEquals(data.get(4).value(0), 5.0, delta);

	}

}
