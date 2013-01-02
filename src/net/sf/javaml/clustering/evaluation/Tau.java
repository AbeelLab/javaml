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
public class Tau implements ClusterEvaluation {
    public Tau(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm;
	public double score(Dataset[] datas) {
		double maxIntraDist[] = new double[datas.length];
		double sPlus = 0, sMin = 0;
		double fw = 0, fb = 0;
		double t = 0, nd;
		
		for (int i = 0; i < datas.length; i++) {
			maxIntraDist[i] = Double.MIN_VALUE;
			for (int j = 0; j < datas[i].size(); j++) {
				Instance x = datas[i].instance(j);
				// calculate intra cluster distances, count their number and
				// find max.
				// count t.
				for (int k = j + 1; k < datas[i].size(); k++) {
					Instance y = datas[i].instance(k);
					double distance = dm.measure(x, y);
					fw++;
					if (maxIntraDist[i] < distance) {
						maxIntraDist[i] = distance;
					}
					// 2 distances (2 pairs of points): t+1
					t++;
				}
				// calculate inter cluster distances, count their number and
				// find min.
				// count sPlus, sMin and t.
				for (int k = i + 1; k < datas.length; k++) {
					for (int l = 0; l < datas[k].size(); l++) {
						Instance y = datas[k].instance(l);
						double distance = dm.measure(x, y);
						fb++;
						if (distance < maxIntraDist[i]) {
							sMin++;
						}
						// 2 distances (2 pairs of points) compaired: t+1
						t++;
						if (distance > maxIntraDist[i]) {
							sPlus++;
						}
						// 2 distances (2 pairs of points) compaired: t+1
						t++;
					}
				}
			}
		}
		nd = fw + fb;
		double tau = (sPlus - sMin)/ Math.sqrt((nd * (nd - 1) / 2 - t) * (nd * (nd - 1) / 2));
		return tau;
	}

	public boolean compareScore(double score1, double score2) {
		// should be maximized
		return score2 > score1;
	}

}
