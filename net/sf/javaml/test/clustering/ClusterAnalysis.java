/**
 * ClusterAnalysis.java
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
package net.sf.javaml.test.clustering;

import java.text.NumberFormat;
import java.util.Locale;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.XMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.DatasetGenerator;

import org.apache.commons.math.stat.StatUtils;

public class ClusterAnalysis {

    public static void main(String[]args){
        
        XMeans km=new XMeans();
        new ClusterAnalysis(km);
    }
    
    public ClusterAnalysis( Clusterer c){
        int ITERATIONS=10;
       
        double[] time = new double[ITERATIONS];
        double[] clusterNumber = new double[ITERATIONS];
//        Class[] scoreClasses = { AICScore.class, BICScore.class, CIndex.class, Gamma.class, GPlus.class,
//                HybridCentroidSimilarity.class, HybridPairwiseSimilarities.class, MinMaxCut.class, PointBiserial.class,
//                SumOfAveragePairwiseSimilarities.class, SumOfSquaredErrors.class, SumOfCentroidSimilarities.class,
//                Tau.class, TraceScatterMatrix.class, WB.class };// ={"AIC","BIC","Cindex","Gamma","GPlus","HybridCentroidSim","HybridPairwiseSim","MinMaxCut","PointBiseral","SumPairwiseSim","SumCentroidSim","SumSquarredErrors","Tau","TraceScatter","WB"};
//        double[][]scores=new double[scoreClasses.length][ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            int space = 300;
            Dataset data = DatasetGenerator.createClusterSquareDataset(space, 10,1000);
            System.err.println("Iteration: "+i);
            long tmpTime = System.currentTimeMillis();
            Dataset[] clusters = c.executeClustering(data);
            time[i] = System.currentTimeMillis() - tmpTime;
            clusterNumber[i] = clusters.length;
//            for(int j=0;j<scoreClasses.length;j++){
//                ClusterEvaluation ce=(ClusterEvaluation)scoreClasses[j].newInstance();
//                scores[j][i]=ce.score(clusters);
//            }
        }
        System.out.println("Dataset size: 1000 ");
        System.out.println("Clusterer: "+c.hashCode());
        System.out.println("Avg time\tAvg clusters\tMin clusters\tMax clusters");
        NumberFormat nf= NumberFormat.getInstance(Locale.US);
        System.out.println(nf.format(StatUtils.mean(time))+"\t"+nf.format(StatUtils.mean(clusterNumber))+"\t"+nf.format(StatUtils.min(clusterNumber))+"\t"+nf.format(StatUtils.max(clusterNumber)));
        System.out.println("Distribution: clusterSize\tnumberOfTimes");
        System.out.println(distribution(clusterNumber));
    }

    private String distribution(double[] clusterNumber) {
       int max=(int)StatUtils.max(clusterNumber);
       int min=(int)StatUtils.min(clusterNumber);
       double[]distro=new double[max-min+1];
       for(int i=0;i<clusterNumber.length;i++){
           distro[(int)clusterNumber[i]-min]++;
       }
       String out="";
       for(int i=0;i<distro.length;i++){
           out+=(i+min)+"\t"+distro[i];
       }
       return out;
       
    }
}
