/**
 * SpeedTestXMeans.java
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
package net.sf.javaml.test;

import java.text.NumberFormat;
import java.util.Locale;

import net.sf.javaml.clustering.XMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.DatasetGenerator;

import org.apache.commons.math.stat.StatUtils;

public class SpeedTestXMeans{

    public static void main(String[] args) {
        System.out.println("Datasetsize\tclusters\ttime ");
        for (int i = 10; i < 1000000; i *= 2) {
            double[] times = new double[10];
            double[] clusterNumber=new double[10];
            for (int j = 0; j < 10; j++) {

                
                int space = 300;
                Dataset data = DatasetGenerator.createClusterSquareDataset(space, 10, i);
                long time = System.currentTimeMillis();
                XMeans km = new XMeans();
                Dataset[] clusters = km.executeClustering(data);

                times[j] = System.currentTimeMillis() - time;
                // CIndex cindex=new CIndex(new EuclideanDistance());
                // System.out.println("C-index score: "+cindex.score(clusters));
                clusterNumber[j]=clusters.length;
            }
            NumberFormat nf=NumberFormat.getInstance(Locale.US);
            nf.setMaximumFractionDigits(2);
            
            System.out.println((4 * i) + "\t" + nf.format(StatUtils.mean(clusterNumber))+"\t"+nf.format(StatUtils.min(clusterNumber))+"\t"+nf.format(StatUtils.max(clusterNumber))+"\t"+nf.format(StatUtils.mean(times))+"\t"+nf.format(StatUtils.min(times))+"\t"+nf.format(StatUtils.max(times)));

        }
    }

}
