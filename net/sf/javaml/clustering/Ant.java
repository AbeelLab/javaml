/**
 * Ant.java
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
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import java.util.Random;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.NormalizedEuclideanSimilarity;

/**
 * 
 * This class implements an Ant Based Clustering Algorithm based on ideas
 * from papers by Handl et al. and Schockaert et al.
 * 
 * XXX add references 
 * 
 * XXX add pseudocode
 * 
 * The distance measure should be normalized in the interval [0,1].
 * 
 * @param iterations
 *            number of iterations of main loop
 * @param maxFailMovesDrop
 *            maximum number of moves 'ants' are allowed to make, when failing
 *            to drop a carried instance/heap on an existing heap. when
 *            maxFailMovesDrop is reached, the carried instance/heap will be
 *            dropped in a new heap.
 * 
 * @param maxFailMovesPick
 *            maximum number of moves 'ants' are allowed to make, when failing
 *            to pick a carried instance/heap from an existing heap. when the
 *            heaps become more homogeneous, the probability to add an heap will
 *            drop to 0 when the clusters are found and nothing will change
 *            anymore. this parameter needs to be higher for bigger datasets.
 *            when maxFailMovesPick is reached, the algorithm will stop.
 * 
 * @param maxNumberMaxFailDrops
 * 
 * @author Andreas De Rijcke
 * @author Thomas Abeel
 * 
 */
public class Ant implements Clusterer {
    /**
     * default constructor
     * 
     */
    public Ant() {
        // TODO create 'good' default constructor
        this(null);
    }

    public Ant(DistanceMeasure dm){
        this(dm,3500,5,5,1250);
    }
    /**
     * XXX add docu
     * 
     * @param iterations
     * @param maxFailMovesDrop
     * @param maxFailMovesPick
     * @param maxNumberMaxFailDrops
     */
    public Ant(DistanceMeasure dm,int iterations, int maxFailMovesDrop, int maxFailMovesPick, int maxNumberMaxFailDrops) {
        this.dm=dm;
        this.iterations = iterations;
        this.maxFailMovesDrop = maxFailMovesDrop;
        this.maxFailMovesPick = maxFailMovesPick;
        this.maxNumberMaxFailDrops = maxNumberMaxFailDrops;
    }

    /**
     * XXX add doc
     */
    private Vector<Instance> carried = new Vector<Instance>();

    /**
     * XXX add doc
     */
    private Vector<Dataset> clusters = new Vector<Dataset>();

    /**
     * XXX add doc
     */
    private Random rg = new Random(System.currentTimeMillis());

    /**
     * XXX add doc
     */
    private DistanceMeasure dm;

    /**
     * XXX add doc
     */
    private int randomHeap, iterations;

    /**
     * XXX add doc
     */
    private double randomProb;

    /**
     * XXX add doc
     */
    private int failMovesDrop, failMovesPick, maxFailMovesDrop, maxFailMovesPick, numberMaxFailDrops,
            maxNumberMaxFailDrops;

    // tuning parameters with standard values
    /**
     * XXX add doc
     */
    private double m1 = 5.0, m2 = 5.0;

    /**
     * XXX add doc
     */
    private double teta1 = 0.5, teta2 = 0.5, teta3 = 0.5;

    /**
     * XXX add doc
     */
    private double n1 = 10 /* for instance drop */, n2 = 20; /* for heap drop */

    /**
     * XXX add doc
     */
    // matrix representation of fig 4.1. [i][j], i = row, j = row element
    private double[][] similarityRanges = { { 0, 0.125, 0.375, 0.625, 0.875, 1 }, { 4.0, 3.0, 2.0, 1.0, 0.0, 0.0 } };

    /**
     * XXX add doc
     */
    // matrix representation of table 4.1, values calculated on the basis of fig
    // 4.2.
    private double[][] stimPickI = { { 0.5625, 0.0, 0.0, 0.0, 0.0 }, { 0.8125, 0.6875, 0.0, 0.0, 0.0 },
            { 1, 0.9375, 0.8125, 0.0, 0.0 }, { 1.0, 1.0, 1.0, 0.9375, 0.0 }, { 1.0, 1.0, 1.0, 1.0, 1.0 }, };

    /**
     * XXX add doc
     */
    // matrix representation of table 4.2, values calculated on the basis of fig
    // 4.2.
    private double[][] stimPickH = { { 1.0, 0.0, 0.0, 0.0, 0.0 }, { 0.5625, 0.9375, 0.0, 0.0, 0.0 },
            { 0.3125, 0.4375, 0.8125, 0.0, 0.0 }, { 0.0625, 0.1875, 0.3125, 0.6875, 0.0 },
            { 0.0625, 0.0625, 0.0625, 0.1875, 0.5625 }, };

    /**
     * XXX add doc
     */
    // matrix representation of table 4.3, values calculated on the basis of fig
    // 4.2.
    private double[][] stimDrop = { { 0.6875, 0.8125, 0.9375, 1.0, 1.0 }, { 0.3125, 0.6875, 0.8125, 0.9375, 1.0 },
            { 0.0625, 0.3125, 0.6875, 0.8125, 0.9375 }, { 0.0625, 0.0625, 0.3125, 0.6875, 0.8125 },
            { 0.0625, 0.0625, 0.0625, 0.3125, 0.6875 }, };

    /**
     * calculates mean instance of a heap H
     * 
     * @param Vector
     *            <Instance> heap
     * @return mean instance
     */
    private Instance meanH(Dataset heap) {
        return DatasetTools.getCentroid(heap, dm);
    }

    /**
     * finds minimal similarity in heap in respect to heap centrum [0,1]
     * 
     * @param Vector
     *            <Instance> heap
     * @return double minSimilar
     */
    private double minSimilarity(Dataset heap) {
        Instance meanH = meanH(heap);
        double minSimilar = dm.calculateDistance(heap.getInstance(0), meanH);
        for (int i = 1; i < heap.size(); i++) {
            double sim = dm.calculateDistance(heap.getInstance(i), meanH);
            minSimilar = Math.min(minSimilar, sim);
        }
        return minSimilar;
    }

    /**
     * find least similar instance in heap (has lowest similarity value) [0,1]
     * 
     * @param Vector
     *            <Instance> heap
     * 
     * @return index of least similar instance
     */
    private int leastSim(Dataset heap) {
        Instance meanH = meanH(heap);
        double sim1 = dm.calculateDistance(heap.getInstance(0), meanH);
        int index = 0;
        for (int i = 1; i < heap.size(); i++) {
            double sim2 = dm.calculateDistance(heap.getInstance(i), meanH);
            if (sim2 > sim1) {
                sim1 = sim2;
                index = i;
            }
        }
        return index;
    }

    /**
     * calculates average similarity of heap H [0,1]
     * 
     * @param Vector
     *            <Instance> heap
     * @return double average similarity of heap
     */
    private double avg(Dataset heap) {
        double avg = 0;
        Instance meanH = meanH(heap);
        for (int i = 0; i < heap.size(); i++) {
            avg += dm.calculateDistance(heap.getInstance(i), meanH);
        }
        avg /= heap.size();
        return avg;
    }

    /**
     * Calculates Lukasiewicz t-norm of avg(carried heap L) and the similarity
     * between clustercentra heap L and heap H.
     * 
     * @param Vector
     *            <Instance> heap carried, Vector<Instance> heap
     * @return double tW
     */
    private double tW(Dataset heapCarried, Dataset heapH) {
        Instance meanCarried = meanH(heapCarried);
        Instance meanH = meanH(heapH);
        double similarity = dm.calculateDistance(meanCarried, meanH);
        double avgCarried = avg(heapCarried);
        double tW = Math.max(0, similarity + avgCarried - 1);
        return tW;
    }

    /**
     * Finds stimulus value for drop/pick action
     * 
     * @param double
     *            avg(heap H)
     * @param double
     *            for pick: minSimilar(heap H); for drop: tW
     * @param int
     *            i: for pick instance = 1, for pick heap = 2, for drop = 3
     * @return double stimulus
     */
    private double stimulus(double param1, double param2, int i) {
        int indexParam1 = 0;
        int indexParam2 = 0;
        for (int j = 0; j < 6; j++) {
            if (param1 > similarityRanges[0][j] && param1 < similarityRanges[0][j + 1]) {
                indexParam1 = (int) similarityRanges[1][j];
            }
            if (param2 > similarityRanges[0][j] && param2 < similarityRanges[0][j + 1]) {
                indexParam2 = (int) similarityRanges[1][j];
            }
        }
        double stimulus = 0;
        if (i == 1) {
            stimulus = stimPickI[indexParam2][indexParam1];
        } else if (i == 2) {
            stimulus = stimPickH[indexParam2][indexParam1];
        } else if (i == 3) {
            stimulus = stimDrop[indexParam2][indexParam1];
        } else {
            throw new RuntimeException("failure: no stimulus calculated!!!");
        }
        return stimulus;
    }

    /**
     * Probability to pick 1 instance from heap H or whole heap [0,1]
     * 
     * @param stimI =
     *            stimulus to pick 1 instance.
     * @param stimH =
     *            stimulus to pick the complete heap.
     * @param int
     *            i: for pick instance = 1, for pick heap.
     * 
     * @return double probability to pick
     */
    private double probPick(double stimI, double stimH, int i) {
        double probPick = 0;
        if (i == 1) {
            probPick = (stimI / (stimI + stimH)) * (Math.pow(stimI, m1) / (Math.pow(teta1, m1) + Math.pow(stimI, m1)));
        } else if (i == 2) {
            probPick = (stimH / (stimI + stimH)) * (Math.pow(stimH, m2) / (Math.pow(teta2, m2) + Math.pow(stimH, m2)));
        }
        return probPick;
    }

    /**
     * probability to drop instance/heap [0,1]
     * 
     * @param stim =
     *            stimulus to drop.
     * @param n:
     *            for instance standard 10, for heap 20
     * @return double probability to drop
     */
    private double probDrop(double stim, double n) {
        double probDrop = Math.pow(stim, n) / (Math.pow(teta3, n) + Math.pow(stim, n));
        return probDrop;
    }

    /**
     * XXX add doc
     */
    // main
    public Dataset[] executeClustering(Dataset data) {
        if(dm==null)
            dm = new NormalizedEuclideanSimilarity(data);
        if (data.size() == 0) {
            throw new RuntimeException("The dataset should not be empty");
        }
        // add one instance to a cluster
        for (int i = 0; i < data.size(); i++) {
            Dataset tmpHeap = new SimpleDataset();
            Instance in = data.getInstance(i);
            tmpHeap.addInstance(in);
            clusters.add(tmpHeap);
        }
        failMovesDrop = 0;
        failMovesPick = 0;
        numberMaxFailDrops = 0;
        // first, load ant with instance from a random heap and remove instance/
        randomHeap = rg.nextInt(clusters.size());
       // heap = getVector(clusters.get(randomHeap));
        carried.add(clusters.get(randomHeap).getInstance(0));
        clusters.remove(randomHeap);
        
        // main algorithm
        int j = 0;
        boolean stopSign = false;
        while (j < iterations && !stopSign) {
            j++;
            // drop instance / heap
            while (carried != null && !stopSign) {
                double probDropC = 0;
                // move ant to random heap with carried instance
                randomHeap = rg.nextInt(clusters.size());
                //heap = getVector(clusters.get(randomHeap));
                // calculated drop probability
                if (clusters.get(randomHeap).size() == 1) {
                    if (carried.size() == 1) {
                        probDropC = Math.pow(dm.calculateDistance(clusters.get(randomHeap).getInstance(0), carried.get(0)), 5);
                    } else {// never add a large heap to a cluster with a single
                            // element
                        probDropC = 0;
                    }
                } else if (clusters.get(randomHeap).size() > 1) {
                    Dataset carriedData = new SimpleDataset(carried);
                    double avgHeap = avg(clusters.get(randomHeap));
                    double tW = tW(carriedData, clusters.get(randomHeap));
                    double stimToDrop = stimulus(avgHeap, tW, 3);
                    if (carried.size() == 1) {
                        probDropC = probDrop(stimToDrop, n1);
                    } else {
                        probDropC = probDrop(stimToDrop, n2);
                    }
                } else {// should never happen
                    // clusters.remove(heap);
                    throw new RuntimeException("empty heap found");
                }
                // generate random drop probability value.
                randomProb = rg.nextDouble();
                // drop instance if random prob <= probDrop
                if (randomProb <= probDropC) {
                    for (int i = 0; i < carried.size(); i++) {
                        clusters.get(randomHeap).addInstance(carried.get(i));
                    }
                    failMovesDrop = 0;
                    carried = null;
                    numberMaxFailDrops = 0;
                    
                } else {
                    failMovesDrop++;
                }

                if (failMovesDrop >= maxFailMovesDrop) {
                   Dataset newHeap = new SimpleDataset(carried);
                   clusters.add(newHeap);
                    failMovesDrop = 0;
                    carried = null;
                    numberMaxFailDrops++;
                }
                if (numberMaxFailDrops >= maxNumberMaxFailDrops) {
                    System.out.println("'numberMaxFailDrops too high: STOP");
                    stopSign = true;
                }
            }
            /*
             * PICK UP AN INSTANCE OR A HEAP
             * 
             * If this fails too often, the algorithm terminates.
             * 
             */
            while (carried == null && !stopSign) {
                // move to other random heap
                randomHeap = rg.nextInt(clusters.size());
                //heap = getVector(clusters.get(randomHeap));
                if (clusters.get(randomHeap).size() == 1) {
                    // pick instance from heap and remove heap from clusters
                    carried=new Vector<Instance>();
                    carried.add(clusters.get(randomHeap).getInstance(0));
                    clusters.remove(randomHeap);
                    failMovesPick = 0;
                } else if (clusters.get(randomHeap).size() == 2) {
                    // pick 1 instance and remove from heap
                    carried=new Vector<Instance>();
                    int randomIndex=rg.nextInt(2);
                    carried.add(clusters.get(randomHeap).getInstance(randomIndex));
                    clusters.set(randomHeap, DatasetTools.removeInstance(clusters.get(randomHeap), randomIndex));
                    failMovesPick = 0;
                } else if (clusters.get(randomHeap).size() > 2) {
                    // calculate stimulus and pick probability for picking 1
                    // instance or heap
                    double averageSim = avg(clusters.get(randomHeap));
                    double minSim = minSimilarity(clusters.get(randomHeap));
                    double stimI = stimulus(averageSim, minSim, 1);
                    double stimH = stimulus(averageSim, minSim, 2);
                    double probPickI = probPick(stimI, stimH, 1);
                    double probPickH = probPick(stimI, stimH, 2);
                    randomProb = rg.nextDouble();
                    // for highest probability, generate random probability
                    // value.
                    if (probPickI > probPickH) {
                        // pick instance if randomProb <= propPick
                        if (randomProb <= probPickI) {
                            // pick least similar instance and remove from heap
                            int indexLeastSimInstance = leastSim(clusters.get(randomHeap));
                            carried = new Vector<Instance>();
                            carried.add(clusters.get(randomHeap).getInstance(indexLeastSimInstance));
                            // carried = tmp;
                            clusters.set(randomHeap, DatasetTools.removeInstance(clusters.get(randomHeap),indexLeastSimInstance));
                            failMovesPick = 0;
                        } else {
                            failMovesPick++;
                        }
                    } else if (probPickI < probPickH) {
                        // pick instance if randomProb <= propPick
                        if (randomProb <= probPickH) {
                            // pick heap and remove from clusters
                            carried = new Vector<Instance>();
                            for(int i=0;i<clusters.get(randomHeap).size();i++){
                                carried.add(clusters.get(randomHeap).getInstance(i));
                            }
                            clusters.remove(randomHeap);
                            
                            failMovesPick = 0;
                        } else {
                            failMovesPick++;
                        }
                    }
                    // if fail moves to pick grows to high, stop algorithm

                    if (failMovesPick >= maxFailMovesPick) {
                        System.err.println("'failMovesPick too high: STOP");
                        stopSign = true;
                    }
                }
            }
            if(clusters.size()==1){
                System.err.println("'Merged all data in iteration: "+j);
                stopSign=true;
            }
        }
        Dataset[] output = new Dataset[clusters.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = clusters.get(i);
        }
        return output;
       }
}
