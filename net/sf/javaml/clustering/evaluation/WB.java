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
public class WB implements ClusterEvaluation {
    public WB(DistanceMeasure dm) {
        this.dm = dm;
    }

    private DistanceMeasure dm;

    public double score(Dataset[] datas) {
        double dw = 0, fw = 0;
        double db = 0, fb = 0;

        for (int i = 0; i < datas.length; i++) {
            for (int j = 0; j < datas[i].size(); j++) {
                Instance x = datas[i].instance(j);
                // calculate sum of intra cluster distances dw and count their
                // number.
                for (int k = j + 1; k < datas[i].size(); k++) {
                    Instance y = datas[i].instance(k);
                    double distance = dm.measure(x, y);
                    dw += distance;
                    fw++;
                }
                // calculate sum of inter cluster distances dw and count their
                // number.
                for (int k = i + 1; k < datas.length; k++) {
                    for (int l = 0; l < datas[k].size(); l++) {
                        Instance y = datas[k].instance(l);
                        double distance = dm.measure(x, y);
                        db += distance;
                        fb++;
                    }
                }
            }
        }
        double wb = (dw / fw) / (db / fb);
        return wb;
    }

    public boolean compareScore(double score1, double score2) {
        // should be minimized ( smallest intra cluster distances)
        return score2 < score1;
    }
}
