/**
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import net.sf.javaml.tools.DatasetTools;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

public class TestEqualWidthBinning {

	@Test
	public void testLymphomaBootstrap() {
		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/lymphoma.csv.gz"), 0, ",");
			Dataset bootstrap = DatasetTools.bootstrap(data, data.size(),
					new Random(7));
			EqualWidthBinning eb = new EqualWidthBinning();
			eb.build(bootstrap);
			eb.filter(bootstrap);
			Instance min = DatasetTools.minAttributes(bootstrap);
			Instance max = DatasetTools.maxAttributes(bootstrap);
			for (int i = 0; i < bootstrap.noAttributes(); i++) {
				Assert.assertTrue(min.value(i) == 0);
				Assert.assertTrue(max.value(i) == 9);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testLymphoma() {
		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/lymphoma.csv.gz"), 0, ",");
			EqualWidthBinning eb = new EqualWidthBinning();
			eb.build(data);
			eb.filter(data);
			Instance min = DatasetTools.minAttributes(data);
			Instance max = DatasetTools.maxAttributes(data);
			for (int i = 0; i < data.noAttributes(); i++) {
				Assert.assertTrue(min.value(i) == 0);
				Assert.assertTrue(max.value(i) == 9);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testColon() {
		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/colon.csv.gz"), 0, ",");
			EqualWidthBinning eb = new EqualWidthBinning();
			eb.build(data);
			eb.filter(data);
			Instance min = DatasetTools.minAttributes(data);
			Instance max = DatasetTools.maxAttributes(data);
			for (int i = 0; i < data.noAttributes(); i++) {
				Assert.assertTrue(min.value(i) == 0);
				Assert.assertTrue(max.value(i) == 9);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testSmall() {
		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
			EqualWidthBinning eb = new EqualWidthBinning();
			// ga.setNumNeigbors(4);

			System.out.println("Before binning: ");
			System.out.println(data);
			System.out.println(DatasetTools.minAttributes(data));
			System.out.println(DatasetTools.maxAttributes(data));
			System.out.println("Binning...");
			eb.build(data);
			eb.filter(data);
			System.out.println("--");
			System.out.println("After binning: ");
			System.out.println(data);
			Instance min = DatasetTools.minAttributes(data);
			Instance max = DatasetTools.maxAttributes(data);
			for (int i = 0; i < data.noAttributes(); i++) {
				Assert.assertTrue(min.value(i) == 0);
				Assert.assertTrue(max.value(i) == 9);
			}

			// System.out.println(ga.evaluateAttribute(0));
			// System.out.println(ga.evaluateAttribute(1));
			// System.out.println(ga.evaluateAttribute(2));
			// System.out.println(ga.evaluateAttribute(3));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertTrue(false);
		}

	}

}
