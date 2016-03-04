/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * TODO uitleg
 * 
 * @author Andreas De Rijcke
 */

public class GPlus implements ClusterEvaluation {
	public GPlus(DistanceMeasure dm) {
		this.dm = dm;
	}

	private DistanceMeasure dm;

	public double score(Dataset[] datas) {
		double maxIntraDist = Double.MIN_VALUE;
		double sMin = 0;
		double fw = 0, fb = 0;
		double nd;

		// calculate max intra cluster distance
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].instance(j);
				for (int k = j + 1; k < datas[i].size(); k++) {
					fw++;
					Instance y = datas[i].instance(k);
					double distance = dm.measure(x, y);
					if (maxIntraDist < distance) {
						maxIntraDist = distance;
					}
				}
			}
		}

		// calculate inter cluster distances
		// count sMin
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].instance(j);
				for (int k = i + 1; k < datas.length; k++) {
					for (int l = 0; l < datas[k].size(); l++) {
						Instance y = datas[k].instance(l);
						fb++;
						double distance = dm.measure(x, y);
						if (distance < maxIntraDist) {
							sMin++;
						}
					}
				}
			}
		}
		nd = fw + fb;
		double gPlus = (2 * sMin) / (nd * (nd - 1));
		return gPlus;
	}

	public boolean compareScore(double score1, double score2) {
		// should be minimized: range = [0,x] with x= fb/nd
		return score2 < score1;
	}
}
