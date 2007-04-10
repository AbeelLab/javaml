/**
 * DatasetTools.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import net.sf.javaml.distance.DistanceMeasure;

/**
 * This class provides utility methods on datasets.
 * 
 * @author Thomas Abeel
 * 
 */
final public class DatasetTools {

    /**
     * Returns the instance of the given dataset that is closest to the instance
     * that is given as a parameter.
     * 
     * @param data
     *            the dataset to search in
     * @param inst
     *            the instance for which we need to find the closest
     * @return
     */
    public static Instance getClosest(Dataset data, DistanceMeasure dm, Instance inst) {
        Instance closest = data.getInstance(0);
        double bestDistance = dm.calculateDistance(inst, closest);
        for (int i = 1; i < data.size(); i++) {
            double tmpDistance = dm.calculateDistance(inst, data.getInstance(i));
            if (dm.compare(tmpDistance, bestDistance)) {
                bestDistance = tmpDistance;
                closest = data.getInstance(i);
            }
        }
        return closest;
    }

    /**
     * Return an instance with on each position the standard deviation for that
     * attribute.
     * 
     * @param data
     * @return
     */
    public static Instance getStandardDeviation(Dataset data) {
        int numAttributes = data.getInstance(0).size();
        float[] stdValues = new float[numAttributes];
        double[] attr = new double[data.size()];
        for (int i = 0; i < numAttributes; i++) {
            StandardDeviation std = new StandardDeviation();
            for (int j = 0; j < data.size(); j++) {
                attr[j] = data.getInstance(j).getValue(i);
            }
            stdValues[i] = (float) std.evaluate(attr);
        }
        return new SimpleInstance(stdValues);

    }

    /**
     * Return the centroid of this cluster when using the given distance
     * measure.
     * 
     * @param dm
     *            the distance measure to use when calculating the centroid.
     * @return the centroid of this dataset
     */
    public static Instance getCentroid(Dataset data, DistanceMeasure dm) {
        if (data.size() == 0)
            return null;
        int instanceLength = data.getInstance(0).size();
        float[] sumPosition = new float[instanceLength];
        for (int i = 0; i < data.size(); i++) {
            Instance in = data.getInstance(i);
            for (int j = 0; j < instanceLength; j++) {

                sumPosition[j] += in.getWeight() * in.getValue(j);

            }

        }
        for (int j = 0; j < instanceLength; j++) {
            sumPosition[j] /= data.size();
        }
        return new SimpleInstance(sumPosition);

    }

    /**
     * Performs an epsilon range query for this instance relative to the
     * supplied dataset.
     * 
     * @param data
     *            the dataset on which to run the query.
     * @param epsilon
     *            Specifies the range for the query
     * @param instance
     *            The instance that is used as query-object for epsilon range
     *            query
     * @param dm
     *            The distance measure used to calculated the distances.
     * @return List with all the Instances that are within the specified range
     */
    public static List<Instance> epsilonRangeQuery(Dataset data, double epsilon, Instance instance, DistanceMeasure dm) {
        ArrayList<Instance> epsilonRange_List = new ArrayList<Instance>();

        for (int i = 0; i < data.size(); i++) {
            Instance tmp = data.getInstance(i);
            double distance = dm.calculateDistance(tmp, instance);
            if (distance < epsilon) {
                epsilonRange_List.add(tmp);
            }
        }

        return epsilonRange_List;
    }
    
    /**
     * Removes an instance from a dataset and returns the resulting dataset.
     * 
     * XXX DOC
     * 
     * @param data
     * @param index
     * @return
     */
    public static Dataset removeInstance(Dataset data,int index){
        Dataset out=new SimpleDataset();
        for(int i=0;i<data.size();i++){
            if(i!=index)
                out.addInstance(data.getInstance(i));
        }
        return out;
        
    }

}
