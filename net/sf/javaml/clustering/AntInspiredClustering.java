/**
 * AntInspiredClustering.java, 30-nov-2006
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
package net.sf.javaml.clustering;

import java.text.NumberFormat;
import java.util.Random;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * TODO The distance scaling is only suitable for Euclidean distance. THIS
 * SHOULD BE CHECKED
 * 
 * TODO clustering still does not work properly
 * @author Thomas Abeel
 * 
 */
public class AntInspiredClustering implements Clusterer {

    private int iterations = 500;

    private Random rg = new Random(System.currentTimeMillis());

    private DistanceMeasure dm = new CosineSimilarity();

    private int maxDropCount = 10;

    private Vector<Instance> centroids = null;

    private double scaleFactor=1;
    private double offSet=0;

    

    public void buildClusterer(Dataset data) {
        this.iterations=data.size()*10;
        double max=data.getMaximumInstance().getValue(0);
        double min=data.getMinimumInstance().getValue(0);
        for(int i=0;i<data.getMaximumInstance().size();i++){
            if(data.getMaximumInstance().getValue(i)>max){
                max=data.getMaximumInstance().getValue(i);
            }
            if(data.getMinimumInstance().getValue(i)<min){
                min=data.getMinimumInstance().getValue(i);
            }
        }
        this.scaleFactor=max-min;
        this.offSet=-min;
        //new instance: +offset dan /scaleFactor
        /*
         * Pseudocode ---------- put all instances in a seperate cluster
         * 
         * for number of iterations
         * 
         * while the ant is not carrying an instance
         * 
         * pick an instance from an existing cluster with a probability - each
         * time select a random cluster to pick from - try to take the least
         * similar item from a cluster - destroy cluster if no items remain
         * 
         * while the ant is carrying an instance
         * 
         * try to drop the instance in a cluster - drop chance depends on the
         * similarity of the item with the cluster - if after a predefined
         * number of steps the item is not dropped, create new cluster and drop
         * it there
         * 
         */
        Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();
        for (int i = 0; i < data.size(); i++) {
            Vector<Instance> tmp = new Vector<Instance>();
            tmp.add(data.getInstance(i));
            clusters.add(tmp);

       }
       NumberFormat nf=NumberFormat.getInstance();
       nf.setMaximumFractionDigits(4);
        /**
         * The scaling parameter for the dissimilarities
         */
        double alfa=rg.nextDouble();
        double failedMoves = 0;
        double moves = 0;

        for (int i = 0; i < this.iterations; i++) {
            System.out.println("Iteration: " + i);
            // pickItem
            System.out.println("\tPicking item");
            Instance pickedItem = null;
            moves++;
            while (pickedItem == null) {
                int randomIndex = rg.nextInt(clusters.size());
                
                int leastSimilarIndex = getLeastSimilarInstance(clusters.get(randomIndex));
                double pickChance = this.pickProbability(clusters.get(randomIndex).get(leastSimilarIndex), clusters
                        .get(randomIndex), alfa);
                if(pickChance>0){
                    System.out.println("\t\tPicking chance [" + randomIndex + "," + leastSimilarIndex +","+clusters.get(randomIndex).size()+ ";"+nf.format(alfa)+"]: " + pickChance);
                    
                }
                if (rg.nextDouble() <= pickChance) {// pick up item and move on
                    pickedItem = clusters.get(randomIndex).remove(leastSimilarIndex);
                    if (clusters.get(randomIndex).size() == 0) {
                        clusters.remove(randomIndex);
                    }
                    alfa+=0.01;
                    if(alfa>1)
                        alfa=1;
                } else{
                    alfa-=0.0001;
                    if(alfa<=0){
                        alfa=0.01;
                    }
                    //System.out.println("\t\t\tPick failed");
                }

               
            }
            // dropItem
            System.out.println("\tDropping item");
            boolean itemDropped = false;
            //int dropCount = 0;
            while (pickedItem!=null &&!itemDropped) {
                int randomIndex = rg.nextInt(clusters.size());
                double dropChance = this.dropProbability(pickedItem, clusters.get(randomIndex), alfa);
                System.out.println("\t\tDropping chance ["+randomIndex+","+clusters.get(randomIndex).size()+ ";"+nf.format(alfa)+"]="+dropChance );
                if (rg.nextDouble() <= dropChance) {
                    // drop item and move on
                    clusters.get(randomIndex).add(pickedItem);
                    System.out.println("\t\tDropping in existing cluster");
                    itemDropped = true;
                    alfa-=0.01;
                    if(alfa<=0){
                        alfa=0.01;
                    }
                } else {
                    alfa+=0.0001;
                    if(alfa>1)
                        alfa=1;
                    //failedMoves++;
                   
                    //dropCount++;
//                    if (dropCount >= maxDropCount) {
//                        // create new cluster and force drop
//                        System.out.println("\t\tCreating new cluster");
//                        Vector<Instance> tmp = new Vector<Instance>();
//                        tmp.add(pickedItem);
//                        clusters.add(tmp);
//                        itemDropped = true;
//                    }
                }
//                moves++;
//                if (moves >= 100) {
//                    if (failedMoves / moves > 0.99)
//                        alfa -= 0.01;
//                    else
//                        alfa += 0.01;
//                    moves = 0;
//                    if (alfa < 0.01) {
//                        alfa = 0.01;
//                    }
//                    if (alfa > 1) {
//                        alfa = 1;
//                    }
//                    failedMoves = 0;
//                    System.out.println("\t\tAlfaDrop update: " + alfa);
//                }

            }
        }
        // Clean up, create centroids and set number of clusters
        this.centroids = new Vector<Instance>();
        
        for (int i = 0; i < clusters.size(); i++) {
            Vector<Instance> tmpCluster = clusters.get(i);
            if (tmpCluster.size() > 0) {
                int instanceLength = tmpCluster.get(0).size();
                float[] sum = new float[instanceLength];
                for (int j = 0; j < tmpCluster.size(); j++) {
                    for (int k = 0; k < instanceLength; k++) {
                        sum[k] += tmpCluster.get(j).getValue(k);
                    }
                }
                for (int k = 0; k < instanceLength; k++) {
                    sum[k] /= tmpCluster.size();
                }
                this.centroids.add(new SimpleInstance(sum));
            } else {
                System.err.println("An empty cluster, this should not happen");
            }
            
        }
        System.out.println("Generated clusters: "+clusters.size());
        for(int i=0;i<clusters.size();i++){
               System.out.println("cluster "+i+": "+clusters.get(i).size());
        }
        

    }

    private int getLeastSimilarInstance(Vector<Instance> data) {
        int leastSimilarInstanceIndex = 0;
        double bestDistance = 0;
        for (int i = 0; i < data.size(); i++) {
            double totalDistance = 0;
            for (int j = 0; j < data.size(); j++) {
                totalDistance += (dm.calculateDistance(data.get(i), data.get(j)) / data.size());
            }
            if (totalDistance > bestDistance) {
                leastSimilarInstanceIndex = i;
                bestDistance = totalDistance;
            }

        }
        return leastSimilarInstanceIndex;
    }

    private double dropProbability(Instance i, Vector<Instance> neighborhood, double alfa) {
        double kMinus = 0.3;
        double fI = neighborhoodFunction(i, neighborhood, alfa);
        double single = fI / (kMinus + fI);
        return single * single;
    }

    private double pickProbability(Instance i, Vector<Instance> neighborhood, double alfa) {
        if(neighborhood.size()==1){
            return 1;
        }
        double kPlus = 0.1;
        double single = kPlus / (kPlus + neighborhoodFunction(i, neighborhood, alfa));
        return single * single;
    }

    private double neighborhoodFunction(Instance i, Vector<Instance> neighborhood, double alfa) {
        double neighborhoodSize = 10;
        double sum = 0;
        //System.out.println("Alfa: "+alfa);
        for (Instance x : neighborhood) {
            double dist=dm.calculateDistance(i, x);
            //System.out.println("\tAlfa:"+alfa +"\tDistance: "+dist);
            sum += (1 - dist / alfa);
        }
        
        sum/=neighborhood.size();
        //System.out.println("Total sum: "+sum);
        //System.out.println("f(i)= " + Math.max(0.0, sum ));
        return Math.max(0.0, sum );
    }

    public int predictCluster(Instance instance) {
        if (this.centroids == null)
            throw new RuntimeException("The cluster should first be constructed");
        int tmpCluster = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.getNumberOfClusters(); i++) {
            double dist = dm.calculateDistance(centroids.get(i), instance);
            if (dist < minDistance) {
                minDistance = dist;
                tmpCluster = i;
            }
        }
        return tmpCluster;
    }

    public double[] predictMembershipDistribution(Instance instance) {
        double[] tmp = new double[this.getNumberOfClusters()];
        tmp[this.predictCluster(instance)] = 1;
        return tmp;
    }

    public int getNumberOfClusters() {
        return this.centroids.size();

    }

    public Instance[] getCentroids() {
        Instance[] tmp = new Instance[centroids.size()];
        tmp = centroids.toArray(tmp);
        return tmp;
    }

}
