package junit.tools;

import java.io.File;
import java.util.Set;


import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestAnsi {
	@Test
	public void testUnicode() {
		int numberOfFeatures = 20;
		Dataset data;
		try {
			data = FileHandler.loadDataset(new File("devtools/data/wdbc-unicode.csv"), 1, ",");
			GreedyForwardSelection gfs = new GreedyForwardSelection(numberOfFeatures, new PearsonCorrelationCoefficient());
			gfs.build(data);
			System.out.println("\n");
			System.out.print("Top (" + numberOfFeatures + ") Important Features that selected: ");
			Set<Integer> fields = gfs.selectedAttributes();
			Object[] ff;
			ff = fields.toArray();
			int[] features = new int[ff.length];
			for (int i = 0; i < features.length; i++) {
				features[i] = Integer.parseInt(ff[i].toString());
				if (features[i] == 0)
					System.out.print("id ");
				System.out.print("L" + (features[i] + 1) + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	@Test
	public void testAnsi() {
		int numberOfFeatures = 20;
		Dataset data;
		try {
			data = FileHandler.loadDataset(new File("devtools/data/wdbc-ansi.csv"), 1, ",");
			GreedyForwardSelection gfs = new GreedyForwardSelection(numberOfFeatures, new PearsonCorrelationCoefficient());
			gfs.build(data);
			System.out.println("\n");
			System.out.print("Top (" + numberOfFeatures + ") Important Features that selected: ");
			Set<Integer> fields = gfs.selectedAttributes();
			Object[] ff;
			ff = fields.toArray();
			int[] features = new int[ff.length];
			for (int i = 0; i < features.length; i++) {
				features[i] = Integer.parseInt(ff[i].toString());
				if (features[i] == 0)
					System.out.print("id ");
				System.out.print("L" + (features[i] + 1) + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
