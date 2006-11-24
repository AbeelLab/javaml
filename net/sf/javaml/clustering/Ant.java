/**
 * Ant.java, 21-nov-2006
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
 * Copyright (c) 2006, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import java.util.Vector;
import java.util.Random;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.CosineSimilarity;

public class Ant implements Clusterer {
    Vector<Instance> tower = new Vector<Instance>();

    Vector<Instance> tower1 = new Vector<Instance>();

    Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();

    private Instance leastSim;

    private Instance carried;

    private Instance[] centroids;

    private Random rg = new Random(System.currentTimeMillis());

    private int numberOfClusters;

    private int iterations;

    private int actMoves;

    private int failMoves;

    private int maxFailMoves;

    private int randomTower;

    private double alfa;

    private double nFunction;

    private double probPick;

    private double probDrop;

    private double randomProb;

    private DistanceMeasure dm =new CosineSimilarity();

    /**public Ant() {
        this(100, 10);
    }
	
	*/
    
    public Ant(int iterations, int maxFailMoves) {
        this.iterations = iterations;
        this.maxFailMoves = maxFailMoves;
    }

    // methode: search for least similar instance in tower
    public Instance pickLeastSim(Vector<Instance> tower) {
        double leastSim = 0;
        int leastSimIndex = 0;
        int i;
        double error;
        for (i = 0; i < tower.size(); i++) {
            Instance x = tower.get(i);
            error = 0;
            for (int j = 0; j < tower.size(); j++) {             
            	Instance y = tower.get(j);
            	double newError = dm.calculateDistance(x, y);
            	error += Math.abs(newError);
            }
            if (error > leastSim) {
                leastSim = error;
                leastSimIndex = i;
            }
        }
        Instance pickLeastSim = tower.get(leastSimIndex);
        return pickLeastSim;
    }

    // methode: calculate alfa (scaling param)
    public double alfa(double alfa, int failMoves, int actMoves) {
        double rateFail = failMoves / actMoves;
        if (rateFail > 0.99) {
            alfa += 0.01;
        }
        if (rateFail <= 0.99 && rateFail >= 0.01) {
            alfa -= 0.01;
        }
        return alfa;
    }

    // methode: calculate neighborhood function
    public double nFunction(double alfa, Instance x, Vector<Instance> tower) {
        double nFunction = 0;
        for (int j = 0; j < tower.size(); j++) {
            Instance tmp = tower.get(j);
            double delta = dm.calculateDistance(x, tmp);
            nFunction += 1 - (Math.abs(delta) / alfa);
        }
        return nFunction;
    }

    // methode: calculate probility for picking-up an instance
    public double probPick(Instance instance, double nFunction) {
        double kPlus = 0.1;
        double probPick = (kPlus / (kPlus + nFunction)) * (kPlus / (kPlus + nFunction));
        return probPick;
    }

    // methode: calculate probility for dropping an instance
    public double probDrop(Instance instance, double nFunction) {
        double kMin = 0.3;
        double probDrop = (nFunction / (kMin + nFunction)) * (nFunction / (kMin + nFunction));
        return probDrop;
    }

    // main
    public void buildClusterer(Dataset data) {
        if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");

        // add all instances to a tower, add all towers to clusters.
        System.out.println("dataSize: " + data.size());
         for (int i = 0; i < data.size(); i++) {
            Vector<Instance> tmpTower = new Vector<Instance>();
            Instance in = data.getInstance(i);
            tmpTower.add(in);
            clusters.add(tmpTower);
        }

        // set initial parameters
        // set alfa to random value between 0 and 1.
        alfa = rg.nextDouble();
        //System.out.println("first random alfa: " + alfa);
        actMoves = 0;
        failMoves = 0;
        tower.clear();

        // first, pick least similar instance from a random tower
        //System.out.println("torens in clusters: " + clusters.size());
        randomTower = rg.nextInt(clusters.size());
        //System.out.println("randomTower: " + randomTower);
       // System.out.println("size of random tower: " + clusters.get(randomTower).size());
        tower = clusters.get(randomTower);
        //System.out.println("tower: " + tower);
        carried = tower.get(0);//pickLeastSim(tower);
        tower.remove(0);
        //System.out.println("carried: " + carried);
        //System.out.println("size of random tower: " + clusters.get(randomTower).size());
        if (tower.size() == 0) {
            clusters.remove(randomTower);
        }
        //System.out.println("torens in clusters: " + clusters.size());

        // main loop
        for (int i = 0; i < iterations; i++) {
        	//System.out.println("--------");
        	//System.out.println("--------");
            //System.out.println("Main loop");
        	System.out.println("iterations: " + i);
            // move to random tower with carried instance
            // if number of moves reaches 100, recalculate alfa.
        	actMoves++;
        	//System.out.println("actMoves: " + actMoves);
        	if (actMoves == 100) {
            	//System.out.println("actMoves: " + actMoves);
            	alfa = alfa(alfa, failMoves, actMoves);
                actMoves = 0;
                //System.out.println("recalculated alfa: " + alfa);
            }
            //System.out.println("--drop instance on random tower");
            randomTower = rg.nextInt(clusters.size());
            //System.out.println("randomTower: " + randomTower);
            tower = clusters.get(randomTower);
            nFunction = nFunction(alfa, carried, tower);
            probDrop = probDrop(carried, nFunction);
            randomProb = rg.nextDouble();
            //System.out.println("randomProb: " + randomProb);
            // drop instance if random prob > probDrop
            if (randomProb <= probDrop) {
            	//System.out.println("size of random tower: " + clusters.get(randomTower).size());
            	tower.add(carried);
                carried = null;
                failMoves = 0;
                //System.out.println("failMoves: " + failMoves);
                //System.out.println("size of random tower: " + clusters.get(randomTower).size());
            } else {
                failMoves++;
                //System.out.println("failMoves: " + failMoves);
            }
            if (failMoves >= maxFailMoves) {
                //System.out.println("max fail moves: creating new tower");
                Vector<Instance> newTower = new Vector<Instance>();
                newTower.add(carried);
                clusters.add(newTower);
                //System.out.println("after max fail > torens in clusters: " + clusters.size());
                failMoves = 0;
                carried = null;
            }
            
            while (carried == null) {
            	//System.out.println("--pick new instance fron random tower");
                // move to other random tower
                randomTower = rg.nextInt(clusters.size());
                //System.out.println("randomTower: " + randomTower);
                tower = clusters.get(randomTower);
                //System.out.println("size of random tower: " + clusters.get(randomTower).size());
                leastSim = pickLeastSim(tower);
                int indexLS = tower.indexOf(leastSim);
                //System.out.println("indexOfleastSim: " + indexLS);
                nFunction = nFunction(alfa, leastSim, tower);
                probPick = probPick(leastSim, nFunction);
                
                randomProb = rg.nextDouble();
                //System.out.println("randomProb: " + randomProb);
                // pick instance if random prob > probPick
                if (randomProb <= probPick) {
                	//System.out.println("size of random tower: " + clusters.get(randomTower).size());
                	carried = leastSim;
                    tower.remove(indexLS);
                    //System.out.println("size of random tower: " + clusters.get(randomTower).size()); 
                }
                if (tower.size() == 0) {
                    clusters.remove(randomTower);
                }
            }
            //System.out.println("torens in clusters: " + clusters.size());
            //System.out.println("End main loop");
            //System.out.println("--------");
            
        }
        // calculate centriods of each tower/cluster
        numberOfClusters = clusters.size();
        System.out.println("numberOfClusters: " + numberOfClusters);
        this.centroids = new Instance[numberOfClusters];
        for (int i = 0; i < numberOfClusters; i++) {
            tower = clusters.get(i);
            int instanceLength = data.getInstance(0).size();
            float sum[] = new float[instanceLength];
            for (int j = 0; j < tower.size(); j++) {
                float tmp[] = tower.get(j).getArrayForm();
                for (int k = 0; k < instanceLength; k++) {
                    sum[k] += tmp[k];
                }
            }
            for (int j = 0; j < instanceLength; j++) {
                sum[j] /= tower.size();
            }
            this.centroids[i] = new SimpleInstance(sum);
        }

    }

    public int getNumberOfClusters() {
        return this.numberOfClusters;
    }

    public int predictCluster(Instance instance) {
        if (this.centroids == null)
            throw new RuntimeException("The cluster should first be constructed");
        int tmpCluster = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < this.numberOfClusters; i++) {
//            System.out.println("INSTANCE:" +instance);
//            System.out.println("CENTROID: " +centroids[i]);
//            System.out.println("DM: "+dm);
            
            double dist = dm.calculateDistance(centroids[i], instance);
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

    /**
     * This method is only intended for testing purposes.
     * 
     */
    public Instance[] getCentroids() {
        return this.centroids;
    }

}
