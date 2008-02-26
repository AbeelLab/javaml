/**
 * TestEMClustering.java
 *
 * %SVN.HEADER%
 */
package junit.clustering;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.UnsetClassFilter;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.ClusteringBridge;
import net.sf.javaml.tools.weka.WekaUtils;

import org.junit.Test;

import weka.clusterers.EM;
import weka.core.Instances;

/**
 * Test class for the Java-WEKA clustering bridge
 * 
 * @author Thomas Abeel
 * 
 */
public class TestEMClustering {

	@Test
	public void testBasicClustering() {
		try {
			Dataset data = new SimpleDataset();
			double[] d = new double[] { 1, 0.014095, 0.15265, 0.0083603 };
			data.add(new SimpleInstance(d));
			d = new double[] { 0.014095, 1, 0.070601, 0.22234 };
			data.add(new SimpleInstance(d));
			d = new double[] { 0.15265, 0.070601, 1, 0.022505 };
			data.add(new SimpleInstance(d));
			d = new double[] { 0.0083603, 0.22234, 0.022505, 1 };
			data.add(new SimpleInstance(d));

			ClusteringBridge em = new ClusteringBridge(new EM());
			Dataset[] clusters = em.executeClustering(data);
			System.out.println("Size: " + clusters.length);

			Instances insts = new WekaUtils(data).getDataset();
			EM emWeka = new EM();
			// emWeka.setNumClusters(3);
			emWeka.setMaxIterations(50);
			emWeka.buildClusterer(insts);
			for (int i = 0; i < insts.numInstances(); i++)
				System.out.println(emWeka.clusterInstance(insts.instance(i)));
			// }
			// System.out.println("Weka clusters: "+emWeka.getNumClusters());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testDatasets() {
		try {
			System.out.println("Iris data");
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.tsv"), 4);
			process(data);
			/*
			 * Other tests take a long time to complete, so only enable when
			 * needed.
			 */

			// System.out.println("-------------------");
			// System.out.println("BUPA data");
			// data = FileHandler.loadDataset(new
			// File("devtools/data/BUPA.tsv"),
			// 6);
			// process(data);
			//
			// System.out.println("-------------------");
			// System.out.println("Pima Indians");
			// data = FileHandler.loadDataset(new
			// File("devtools/data/pima.tsv"),
			// 8);
			// process(data);
			//
			// System.out.println("-------------------");
			// System.out.println("Wisconson cancer");
			// data = FileHandler.loadDataset(new
			// File("devtools/data/wdbc.csv"),
			// 1, ",");
			// process(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void process(Dataset data) {
		try {
			ClusteringBridge em = new ClusteringBridge(new EM());
			Dataset[] clusters = em.executeClustering(data);
			System.out.println("Java-ML size: " + clusters.length);

			Dataset data2 = new UnsetClassFilter().filterDataset(data);
			Instances insts =  new WekaUtils(data2).getDataset();
			EM emWeka = new EM();
			// emWeka.setNumClusters(3);
			// emWeka.setMaxIterations(50);
			emWeka.buildClusterer(insts);
			int max = 0;
			for (int i = 0; i < insts.numInstances(); i++) {
				int j = emWeka.clusterInstance(insts.instance(i));
				if (j > max)
					max = j;
			}
			System.out.println("Weka size: " + clusters.length);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
