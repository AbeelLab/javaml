/**
 * SimpleRelief.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.attribute.eval;

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.ManhattanDistance;
import net.sf.javaml.filter.normalize.DatasetNormalizeMidrange;

/**
 * Basic implementation of the RELIEF algorithm for attribute evaluation.
 * 
 * @{jmlSource}
 * 
 * @version %SVN.VERSION%
 * 
 * @author Thomas Abeel
 * 
 */
public class SimpleRELIEF implements IAttributeEvaluation {

    private double[] weights = null;

    public void build(Dataset data) {
        Random rg = new Random();

        // Normalize the data to [0,1]
        DatasetNormalizeMidrange dnm = new DatasetNormalizeMidrange(0.5, 1);
        data = dnm.filterDataset(data);

        weights = new double[data.numAttributes()];
        // number of iterations
        int m = data.size();

        for (int i = 0; i < m; i++) {
            Instance random = data.instance(rg.nextInt(data.size()));
            findNearest(data, random);
            for (int j = 0; j < weights.length; j++)
                weights[j] = weights[j] - diff(j, random, nearestHit) / m + diff(j, random, nearestMiss) / m;
        }
    }

    private Instance nearestHit;

    private Instance nearestMiss;

    private double diff(int index, Instance a, Instance b) {
        return Math.abs(a.value(index) - b.value(index));
    }

    private void findNearest(Dataset data, Instance random) {
        ManhattanDistance dist = new ManhattanDistance();
        double hitD = Double.MAX_VALUE;
        double missD = Double.MAX_VALUE;
        for (Instance i : data) {
            if (!i.equals(random)) {
                double d = dist.calculateDistance(i, random);
                if (i.classValue() == random.classValue()) {
                    if (d < hitD) {
                        nearestHit = i;
                        hitD = d;
                    }
                } else {
                    if (d < missD) {
                        nearestMiss = i;
                        missD = d;
                    }
                }

            }
        }

    }

    public double evaluateAttribute(int attribute) {
        return weights[attribute];
    }

}
