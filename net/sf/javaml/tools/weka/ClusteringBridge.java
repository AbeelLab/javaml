/**
 * ClusteringBridge.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.weka;

import java.util.Vector;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.filter.UnsetClassFilter;
import weka.core.Instances;

/**
 * Provides a bridge between JMLL and the clustering algorithms in WEKA.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ClusteringBridge implements Clusterer {

	private weka.clusterers.Clusterer wekaCluster;

	public ClusteringBridge(weka.clusterers.Clusterer wekaCluster) {
		this.wekaCluster = wekaCluster;
	}

	public Dataset[] cluster(Dataset data) {
		try {
			data = new UnsetClassFilter().filterDataset(data);
			Instances insts =  new WekaUtils(data).getDataset();
			Vector<Dataset> output = new Vector<Dataset>();
			wekaCluster.buildClusterer(insts);
			for (int i = 0; i < insts.numInstances(); i++) {
				int clusterIndex = wekaCluster.clusterInstance(insts
						.instance(i));
				while (output.size() <= clusterIndex)
					output.add(new SimpleDataset());

				output.get(clusterIndex).add(data.instance(i));
			}
			return output.toArray(new Dataset[output.size()]);
		} catch (Exception e) {
			throw new WekaException(e);
		}
	}

}
