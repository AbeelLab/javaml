package junit.tools;

import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetTools;

import org.junit.Test;

public class TestDatasetTools {

	@Test
	public void testAverage(){
		Dataset data=new DefaultDataset();
		data.add(new DenseInstance(new double[]{1}));
		data.add(new DenseInstance(new double[]{2}));
		data.add(new DenseInstance(new double[]{3}));
		data.add(new DenseInstance(new double[]{10}));
		Instance avg=DatasetTools.average(data);
		Assert.assertEquals(4.0,avg.value(0));
	}
}
