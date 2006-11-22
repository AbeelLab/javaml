/**
 * Ants.java, 21-nov-2006
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
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.DistanceMeasureFactory;


public class Ants implements Clusterer{
	private Vector<Instance> tower = new Vector<Instance>();
	private Vector<Vector<Instance>> clusters = new Vector<Vector<Instance>>();
	private Instance leastSim;
	private Instance carried;	
	private Random rg=new Random(System.currentTimeMillis());
	private int actMoves;
	private int failMoves;
	private int randomTower;
	private double alfa;
	private double nFunction;
	private double probPick;
	private double probDrop;
	private double randomProb;
	
	
	// search for least similar instance in tower
	public Instance pickLeastSim (Vector<Instance> tower){
		double leastSim=0;
		int leastSimIndex=0;
		int i;
		for (i=0; i< tower.size(); i++){
			Instance x = tower.get(i);
			double error = 0;
			for (int j=0; j<tower.size();j++){
				Instance tmp =  tower.get(j);
				DistanceMeasure dm = DistanceMeasureFactory.getEuclideanDistanceMeasure();
				error += dm.calculateDistance(x, tmp);
			}
			if ( error > leastSim){
				leastSim = error;
				leastSimIndex = i;
			}
		}
		return tower.get(leastSimIndex);
	}
	// calculate alfa (scaling param)
	public double alfa(double alfa, int failMoves, int actMoves){
		double rateFail = failMoves/actMoves;	
		if ( rateFail > 0.99){
			alfa += 0.01;
		}
		if (rateFail <= 0.99){
			alfa -= 0.01;
		}
		return alfa;
	}	
	// calculate neighborhood function 
	public double nFunction(double alfa, Instance x, Vector<Instance> tower){
		double nFunction = 0;
		for (int j=0; j<tower.size();j++){
			Instance tmp =  tower.get(j);
			DistanceMeasure dm = DistanceMeasureFactory.getEuclideanDistanceMeasure();
			double delta = dm.calculateDistance(x, tmp);
			nFunction += 1 - (delta/alfa);
		}
		nFunction = Math.max(0.0, nFunction);
		return nFunction;
	}	
	// calculate probility for picking-up an instance
	public double probPick(Instance instance, double nFunction){
		double kPlus = 0.1;
		double probPick = (kPlus/(kPlus+nFunction))*(kPlus/(kPlus+nFunction));
		return probPick;
	}
	
	//calculate probility for dropping an instance
	public double probDrop(Instance instance, double nFunction){
		double kMin = 0.3;
		double probDrop = (nFunction/(kMin+nFunction))*(nFunction/(kMin+nFunction));
		return probDrop;
	}
	
	
	public void buildClusterer(Dataset data) {
		if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");
		
		// add all instances to a tower, add all towers to clusters.
		int dataSize = data.size();
		for (int i=0; i<dataSize; i++){
			tower.clear();
			Instance in = data.getInstance(i);
			tower.add(in);
			clusters.add(tower);
		}
		int numberOfTowers = clusters.size();
		System.out.println("torens in clusters: "+numberOfTowers);
		
		
		// set parameters
		alfa = rg.nextDouble();
		actMoves = 0;
		failMoves = 0;
		
		// first time pick least similar instance from random tower
		randomTower = rg.nextInt(numberOfTowers);		
		tower = clusters.get(randomTower);
		carried = pickLeastSim (tower);	
		
		for (int i=0; i< iterations; i++){	
			//move to random tower with carried instance
			if ( actMoves == 100){
				alfa = alfa(alfa, failMoves, actMoves);
			}
			
			actMoves ++;
			randomTower = rg.nextInt(numberOfTowers);
			tower = clusters.get(randomTower);
			nFunction = nFunction(alfa, carried, tower);
			probDrop = probPick(carried ,nFunction);
			randomProb = rg.nextDouble();
			// drop instance if random prob > probDrop
			if (randomProb >= probDrop){
				tower.add(carried);
				
				//move to other random tower
				randomTower = rg.nextInt(numberOfTowers);		
				tower = clusters.get(randomTower);
				leastSim = pickLeastSim (tower);
				nFunction = nFunction(alfa, leastSim, tower);
				probPick = probPick(carried ,nFunction);
				randomProb = rg.nextDouble();
				// pick instance if random prob > probPick
				if (randomProb >= probPick){
					carried = leastSim;
				}
			}
			else{
				failMoves++;
			}	
		}
	}
	
    public int getNumberOfClusters() {
        return this.numberOfClusters;
    }	
	
	public int predictCluster(Instance instance) {
//		TODO implement
    }

    public double[] predictMembershipDistribution(Instance instance) {
//    	TODO implement
    }

    /**
     * This method is only intended for testing purposes.
     * 
     */
    public Instance[] getCentroids() {
//    	TODO implement
    }
	
}
