/**
 * SumOfAveragePairwiseSimilarities.java, 16-nov-2006
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.evaluation;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;

/**
 * I_1 from the Zhao 2001 paper
 * TODO uitleg
 * @author Thomas Abeel
 *
 */
public class SumOfAveragePairwiseSimilarities implements ClusterEvaluation{

    public double score(Clusterer c, Dataset data) {
        Dataset[] datas = new Dataset[c.getNumberOfClusters()];
        for (int i = 0; i < c.getNumberOfClusters(); i++) {
            datas[i] = new SimpleDataset();
        }
        for (int i = 0; i < data.size(); i++) {
            Instance in = data.getInstance(i);
            datas[c.predictCluster(in)].addInstance(in);
        }
        
        DistanceMeasure dm=DistanceMeasureFactory.getCosineSimilarity();
        double sum=0;
        for(int i=0;i<c.getNumberOfClusters();i++){
            double tmpSum=0;
            for(int j=0;j<datas[i].size();j++){
                for(int k=0;k<datas[i].size();k++){
                    double error=dm.calculateDistance(datas[i].getInstance(j),datas[i].getInstance(k));
                    tmpSum+=error;
                }
                
                
            }
            sum+=tmpSum/datas[i].size();
        }
       return sum;
    }

    public boolean compareScore(double score1, double score2) {
        //should be maxed
        return score2>score1;
    }

}
