/**
 * XMeans.java
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
 * Copyright (c) 2000 Mark Hall, Malcolm Ware, Gabi Schmidberger
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import java.text.NumberFormat;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.KDTree;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.utils.ArrayOperations;
import net.sf.javaml.utils.MathUtils;

/**
 * Cluster data using the X-means algorithm.<br/> <br/> X-Means is K-Means
 * extended by an Improve-Structure part In this part of the algorithm the
 * centers are attempted to be split in its region. The decision between the
 * children of each center and itself is done comparing the BIC-values of the
 * two structures.<br/> <br/> For more information see:<br/> <br/> Dan Pelleg,
 * Andrew W. Moore: X-means: Extending K-means with Efficient Estimation of the
 * Number of Clusters. In: Seventeenth International Conference on Machine
 * Learning, 727-734, 2000. <p/>
 * 
 * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author Malcolm Ware (mfw4@cs.waikato.ac.nz)
 * @author Thomas Abeel
 * 
 */
public class XMeans implements Clusterer {
    /**
     * XXX add doc
     */
    private Dataset data = null;

    /** BIC-Score of the current model */
    private double bic = Double.MIN_VALUE;

    /** Distortion */
    private double[] m_Mle = null;

    /** maximum overall iterations */
    private int m_MaxIterations = 5;

    /**
     * maximum iterations to perform Kmeans part if negative, iterations are not
     * checked
     */
    private int m_MaxKMeans = 1000;

    /**
     * see above, but for kMeans of splitted clusters
     */
    private int m_MaxKMeansForChildren = 1000;

    /** The actual number of clusters */
    private int m_NumClusters = 2;

    /** min number of clusters to generate */
    private int m_MinNumClusters = 2;

    /** max number of clusters to generate */
    private int m_MaxNumClusters = 10;

    /** the distance function used */
    private DistanceMeasure dm = new EuclideanDistance();

    /** cluster centers */
    private Instance[] m_ClusterCenters;

    /**
     * temporary variable holding cluster assignments while iterating
     */
    private int[] m_ClusterAssignments;

    /**
     * cutoff factor - percentage of splits done in Improve-Structure part only
     * relevant, if all children lost
     */
    private double m_CutOffFactor = 0.5;

    /**
     * KDTrees class if KDTrees are used
     */
    private KDTree m_KDTree = new KDTree();

    /**
     * whether to use the KDTree (the KDTree is only initialized to be
     * configurable from the GUI)
     */
    private boolean m_UseKDTree = true;

    /** counts iterations done in main loop */
    private int m_IterationCount = 0;

    /** counter to say how often kMeans was stopped by loop counter */
    private int m_KMeansStopped = 0;

    /** Number of splits prepared */
    private int m_NumSplits = 0;

    /** Number of splits accepted (including cutoff factor decisions) */
    private int m_NumSplitsDone = 0;

    /** Number of splits accepted just because of cutoff factor */
    private int m_NumSplitsStillDone = 0;

    /**
     * Generates the X-Means clusterer.
     * 
     * @param data
     *            set of instances serving as training data
     * @throws Exception
     *             if the clusterer has not been generated successfully
     */
    public Dataset[] executeClustering(Dataset data) {
        this.data = data;

        m_NumSplits = 0;
        m_NumSplitsDone = 0;
        m_NumSplitsStillDone = 0;

        // initialize random function
        Random rg = new Random(System.currentTimeMillis());

        // num of clusters to start with
        m_NumClusters = m_MinNumClusters;

        // make list of indexes for m_Instances
        int[] allInstList = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            allInstList[i] = i;
        }
        // makes the first centers randomly
        m_ClusterCenters = makeCentersRandomly(rg, data, m_NumClusters);

        boolean finished = false;
        Instance[] children;

        // builds up a KDTree
        if (m_UseKDTree)
            m_KDTree.setInstances(data);

        // loop counter of main loop
        m_IterationCount = 0;

        /**
         * "finished" does get true as soon as: 1. number of clusters gets >=
         * m_MaxClusters, 2. in the last round, none of the centers have been
         * split
         * 
         * if number of clusters is already >= m_MaxClusters part 1 (=
         * Improve-Params) is done at least once.
         */
        while (!finished && !stopIteration(m_IterationCount, m_MaxIterations)) {

            /*
             * ======================================= 1. Improve-Params
             * conventional K-means =======================================
             */

            m_IterationCount++;

            // prepare to converge
            boolean converged = false;

            // initialize assignments to -1
            m_ClusterAssignments = initAssignments(data.size());
            // stores a list of indexes of instances belonging to each center
            int[][] instOfCent = new int[m_ClusterCenters.length][];

            // KMeans loop counter
            int kMeansIteration = 0;

            // converge in conventional K-means
            // ----------------------------------
            while (!converged && !stopKMeansIteration(kMeansIteration, m_MaxKMeans)) {

                kMeansIteration++;
                converged = true;

                // assign instances to centers
                // -------------------------------------
                converged = assignToCenters(m_UseKDTree ? m_KDTree : null, m_ClusterCenters, instOfCent, allInstList,
                        m_ClusterAssignments, kMeansIteration);

                // compute new centers = centers of mass of points
                converged = recomputeCenters(m_ClusterCenters, instOfCent); // model
            }

            /*
             * ===================== 2. Improve-Structure =====================
             */

            // BIC before split distortioning the centres
            m_Mle = distortion(instOfCent, m_ClusterCenters);
            bic = calculateBIC(instOfCent, m_ClusterCenters, m_Mle);

            int currNumCent = m_ClusterCenters.length;
            Instance[] splitCenters = new Instance[currNumCent * 2];
            int splitIndex = 0;
            // store BIC values of parent and children
            double[] pbic = new double[currNumCent];
            double[] cbic = new double[currNumCent];

            // split each center
            for (int i = 0; i < currNumCent; i++) {

                Instance currCenter = m_ClusterCenters[i];
                int[] currInstList = instOfCent[i];
                int currNumInst = instOfCent[i].length;

                // not enough instances; than continue with next
                if (currNumInst <= 2) {
                    pbic[i] = Double.MAX_VALUE;
                    cbic[i] = 0.0;
                    // add center itself as dummy
                    splitCenters[splitIndex++] = currCenter;
                    splitCenters[splitIndex++] = currCenter;
                    continue;
                }

                // split centers ----------------------------------------------
                double variance = m_Mle[i] / (double) currNumInst;
                children = splitCenter(rg, currCenter, variance);
                // initialize assignments to -1
                int[] oneCentAssignments = initAssignments(currNumInst);
                int[][] instOfChCent = new int[2][]; // todo maybe split
                // didn't work

                // converge the children --------------------------------------
                converged = false;
                int kMeansForChildrenIteration = 0;
                while (!converged && !stopKMeansIteration(kMeansForChildrenIteration, m_MaxKMeansForChildren)) {
                    kMeansForChildrenIteration++;
                    converged = assignToCenters(children, instOfChCent, currInstList, oneCentAssignments);
                    if (!converged) {
                        recomputeCentersFast(children, instOfChCent);
                    }
                }

                // store new centers for later decision if they are taken
                splitCenters[splitIndex++] = children[0];// .instance(0));
                splitCenters[splitIndex++] = children[1];// .instance(1));
                // compare parent and children model by their BIC-value
                pbic[i] = calculateBIC(currInstList, currCenter, m_Mle[i]);
                double[] chMLE = distortion(instOfChCent, children);
                cbic[i] = calculateBIC(instOfChCent, children, chMLE);

            } // end of loop over clusters

            // decide which one to split and make new list of cluster centers
            Instance[] newClusterCenters = null;
            newClusterCenters = newCentersAfterSplit(pbic, cbic, m_CutOffFactor, splitCenters);
            /**
             * Compare with before Improve-Structure
             */
            int newNumClusters = newClusterCenters.length;
            if (newNumClusters != m_NumClusters) {

                // initialize assignments to -1
                int[] newClusterAssignments = initAssignments(data.size());

                // stores a list of indexes of instances belonging to each
                // center
                int[][] newInstOfCent = new int[newClusterCenters.length][];

                // assign instances to centers
                // -------------------------------------
                converged = assignToCenters(m_UseKDTree ? m_KDTree : null, newClusterCenters, newInstOfCent,
                        allInstList, newClusterAssignments, m_IterationCount);

                double[] newMle = distortion(newInstOfCent, newClusterCenters);
                double newBic = calculateBIC(newInstOfCent, newClusterCenters, newMle);
                if (newBic > bic) {
                    bic = newBic;
                    m_ClusterCenters = newClusterCenters;
                    m_ClusterAssignments = newClusterAssignments;
                }
            }

            newNumClusters = m_ClusterCenters.length;
            // decide if finished: max num cluster reached
            // or last centers where not split at all
            if ((newNumClusters >= m_MaxNumClusters) || (newNumClusters == m_NumClusters)) {
                finished = true;
            }
            m_NumClusters = newNumClusters;
        }
        Dataset[] out = new Dataset[m_NumClusters];
        for (int i = 0; i < out.length; i++) {
            out[i] = new SimpleDataset();
        }
        for (int i = 0; i < data.size(); i++) {
            Instance inst = data.getInstance(i);
            int index = clusterProcessedInstance(inst, m_ClusterCenters);
            out[index].addInstance(inst);
        }
        return out;
    }

    /**
     * Creates and initializes integer array, used to store assignments.
     * 
     * @param numInstances
     *            length of array used for assignments
     * @return integer array used for storing assignments
     */
    private int[] initAssignments(int numInstances) {
        int[] ass = new int[numInstances];
        for (int i = 0; i < numInstances; i++)
            ass[i] = -1;
        return ass;
    }

    /**
     * Returns new center list.
     * 
     * The following steps 1. and 2. both take care that the number of centers
     * does not exceed maxCenters.
     * 
     * 1. Compare BIC values of parent and children and takes the one as new
     * centers which do win (= BIC-value is smaller).
     * 
     * 2. If in 1. none of the children are chosen && and cutoff factor is > 0
     * cutoff factor is taken as the percentage of "best" centers that are still
     * taken.
     * 
     * @param pbic
     *            array of parents BIC-values
     * @param cbic
     *            array of childrens BIC-values
     * @param cutoffFactor
     *            cutoff factor
     * @param splitCenters
     *            all children
     * @return the new centers
     */
    private Instance[] newCentersAfterSplit(double[] pbic, double[] cbic, double cutoffFactor, Instance[] splitCenters) {

        // store if split won
        boolean splitPerCutoff = false;
        boolean takeSomeAway = false;
        boolean[] splitWon = new boolean[m_ClusterCenters.length];
        int numToSplit = 0;
        Instance[] newCenters = null;

        // how many would be split, because the children have a better bic value
        for (int i = 0; i < cbic.length; i++) {
            if (cbic[i] > pbic[i]) {
                // decide for splitting ----------------------------------------
                splitWon[i] = true;
                numToSplit++;
            }
        }

        // no splits yet so split per cutoff factor
        if ((numToSplit == 0) && (cutoffFactor > 0)) {
            splitPerCutoff = true;

            // how many to split per cutoff factor
            numToSplit = (int) ((double) m_ClusterCenters.length * m_CutOffFactor);
        }

        // prepare indexes of values in ascending order
        double[] diff = new double[m_NumClusters];
        for (int j = 0; j < diff.length; j++) {
            diff[j] = pbic[j] - cbic[j];
        }
        int[] sortOrder = ArrayOperations.sort(diff);

        // check if maxNumClusters would be exceeded
        int possibleToSplit = m_MaxNumClusters - m_NumClusters;

        if (possibleToSplit > numToSplit) {
            // still enough possible, do the whole amount
            possibleToSplit = numToSplit;
        } else
            takeSomeAway = true;

        // prepare for splitting the one that are supposed to be split
        if (splitPerCutoff) {
            for (int j = 0; (j < possibleToSplit) && (cbic[sortOrder[j]] > 0.0); j++) {
                splitWon[sortOrder[j]] = true;
            }
            m_NumSplitsStillDone += possibleToSplit;
        } else {
            // take some splits away if max number of clusters would be exceeded
            if (takeSomeAway) {
                int count = 0;
                int j = 0;
                for (; j < splitWon.length && count < possibleToSplit; j++) {
                    if (splitWon[sortOrder[j]] == true)
                        count++;
                }

                while (j < splitWon.length) {
                    splitWon[sortOrder[j]] = false;
                    j++;
                }
            }
        }

        // finally split
        if (possibleToSplit > 0)
            newCenters = newCentersAfterSplit(splitWon, splitCenters);
        else
            newCenters = m_ClusterCenters;
        return newCenters;
    }

    /**
     * Returns new centers. Depending on splitWon: if true takes children, if
     * false takes parent = current center.
     * 
     * @param splitWon
     *            array of boolean to indicate to take split or not
     * @param splitCenters
     *            list of splitted centers
     * @return the new centers
     */
    private Instance[] newCentersAfterSplit(boolean[] splitWon, Instance[] splitCenters) {

        int countTrue = 0, countFalse = 0;
        for (int i = 0; i < splitWon.length; i++) {
            if (splitWon[i]) {
                countTrue++;
            } else {
                countFalse++;
            }
        }
        Instance[] newCenters = new Instance[2 * countTrue + countFalse];
        int sIndex = 0;
        int index = 0;
        for (int i = 0; i < splitWon.length; i++) {
            if (splitWon[i]) {
                m_NumSplitsDone++;
                newCenters[index] = splitCenters[sIndex++];
                index++;
                newCenters[index] = splitCenters[sIndex++];
                index++;
            } else {
                sIndex++;
                sIndex++;
                newCenters[index] = m_ClusterCenters[i];
                index++;
            }
        }
        return newCenters;
    }

    /**
     * Controls that counter does not exceed max iteration value. Special
     * function for kmeans iterations.
     * 
     * @param iterationCount
     *            current value of counter
     * @param max
     *            maximum value for counter
     * @return true if iteration should be stopped
     */
    private boolean stopKMeansIteration(int iterationCount, int max) {
        boolean stopIterate = false;
        if (max >= 0)
            stopIterate = (iterationCount >= max);
        if (stopIterate)
            m_KMeansStopped++;
        return stopIterate;
    }

    /**
     * Checks if iterationCount has to be checked and if yes (this means max is >
     * 0) compares it with max
     * 
     * @param iterationCount
     *            the current iteration count
     * @param max
     *            the maximum number of iterations
     * @return true if maximum has been reached
     */
    private boolean stopIteration(int iterationCount, int max) {
        boolean stopIterate = false;
        if (max >= 0)
            stopIterate = (iterationCount >= max);
        return stopIterate;
    }

    /**
     * Recompute the new centers. New cluster center is center of mass of its
     * instances. Returns true if cluster stays the same.
     * 
     * @param centers
     *            the input and output centers
     * @param instOfCent
     *            the instances to the centers
     * @param model
     *            data model information
     * @return true if converged.
     */
    private boolean recomputeCenters(Instance[] centers, int[][] instOfCent) {
        boolean converged = true;

        for (int i = 0; i < centers.length; i++) {
            double val;
            float[] centerValues = centers[i].toArray();
            for (int j = 0; j < centers[i].size(); j++) {

                val = meanOrMode(data, instOfCent[i], j);

                for (int k = 0; k < instOfCent[i].length; k++)

                    if (converged && m_ClusterCenters[i].getValue(j) != val)
                        converged = false;
                if (!converged) {
                    // m_ClusterCenters[i].setValue(j, val);
                    centerValues[j] = (float) val;
                }
            }
            m_ClusterCenters[i] = new SimpleInstance(centerValues, centers[i].getWeight(), centers[i].isClassSet(),
                    centers[i].getClassValue());

        }
        return converged;
    }

    /**
     * Recompute the new centers - 2nd version Same as recomputeCenters, but
     * does not check if center stays the same.
     * 
     * @param centers
     *            the input center and output centers
     * @param instOfCentIndexes
     *            the indexes of the instances to the centers
     * @param model
     *            data model information
     */
    private void recomputeCentersFast(Instance[] centers, int[][] instOfCentIndexes) {
        for (int i = 0; i < centers.length; i++) {
            float[] centerValues = centers[i].toArray();
            double val;
            for (int j = 0; j < centerValues.length; j++) {
                val = meanOrMode(data, instOfCentIndexes[i], j);
                centerValues[j] = (float) val;
            }
            centers[i] = new SimpleInstance(centerValues, centers[i].getWeight(), centers[i].isClassSet(), centers[i]
                    .getClassValue());
        }
    }

    /**
     * Computes Mean Or Mode of one attribute on a subset of m_Instances. The
     * subset is defined by an index list.
     * 
     * @param instances
     *            all instances
     * @param instList
     *            the indexes of the instances the mean is computed from
     * @param attIndex
     *            the index of the attribute
     * @return mean value
     */
    private double meanOrMode(Dataset instances, int[] instList, int attIndex) {
        double result, found;
        // int[] counts;
        int numInst = instList.length;

        // if (instances.attribute(attIndex).isNumeric()) {
        result = found = 0;
        for (int j = 0; j < numInst; j++) {
            Instance currInst = instances.getInstance(instList[j]);
            // if (!currInst.isMissing(attIndex)) {
            found += currInst.getWeight();
            result += currInst.getWeight() * currInst.getValue(attIndex);
            // }
        }
        if (MathUtils.eq(found, 0)) {
            return 0;
        } else {
            return result / found;
        }

    }

    /**
     * Assigns instances to centers.
     * 
     * @param tree
     *            KDTree on all instances
     * @param centers
     *            all the input centers
     * @param instOfCent
     *            the instances to each center
     * @param allInstList
     *            list of all instances
     * @param assignments
     *            assignments of instances to centers
     * @param iterationCount
     *            the number of iteration
     * @return true if converged
     * @throws Exception
     *             is something goes wrong
     */
    private boolean assignToCenters(KDTree tree, Instance[] centers, int[][] instOfCent, int[] allInstList,
            int[] assignments, int iterationCount) {

        boolean converged = true;
        if (tree != null) {
            // using KDTree structure for assigning
            converged = assignToCenters(tree, centers, instOfCent, assignments, iterationCount);
            // throw new RuntimeException("Tree not supported");
        } else {
            converged = assignToCenters(centers, instOfCent, allInstList, assignments);
        }
        return converged;
    }

    /**
     * Assign instances to centers using KDtree. First part of conventionell
     * K-Means, returns true if new assignment is the same as the last one.
     * 
     * @param kdtree
     *            KDTree on all instances
     * @param centers
     *            all the input centers
     * @param instOfCent
     *            the instances to each center
     * @param assignments
     *            assignments of instances to centers
     * @param iterationCount
     *            the number of iteration
     * @return true if converged
     * @throws Exception
     *             in case instances are not assigned to cluster
     */
    private boolean assignToCenters(KDTree kdtree, Instance[] centers, int[][] instOfCent, int[] assignments,
            int iterationCount) {

        int numCent = centers.length;
        int numInst = data.size();
        int[] oldAssignments = new int[numInst];

        // WARNING: assignments is "input/output-parameter"
        // should not be null
        if (assignments == null) {
            assignments = new int[numInst];
            for (int i = 0; i < numInst; i++) {
                assignments[0] = -1;
            }
        }

        // WARNING: instOfCent is "input/output-parameter"
        // should not be null
        if (instOfCent == null) {
            instOfCent = new int[numCent][];
        }

        // save old assignments
        for (int i = 0; i < assignments.length; i++) {
            oldAssignments[i] = assignments[i];
        }

        // use tree to get new assignments
        kdtree.centerInstances(centers, assignments, Math.pow(.8, iterationCount));
        boolean converged = true;

        // compare with previous assignment
        for (int i = 0; converged && (i < assignments.length); i++) {
            converged = (oldAssignments[i] == assignments[i]);
            // if (assignments[i] == -1)
            // throw new Exception("Instance " + i + " has not been assigned to
            // cluster.");
        }

        if (!converged) {
            int[] numInstOfCent = new int[numCent];
            for (int i = 0; i < numCent; i++)
                numInstOfCent[i] = 0;

            // count num of assignments per center
            for (int i = 0; i < numInst; i++)
                numInstOfCent[assignments[i]]++;

            // prepare instancelists per center
            for (int i = 0; i < numCent; i++) {
                instOfCent[i] = new int[numInstOfCent[i]];
            }
            // write instance lists per center
            for (int i = 0; i < numCent; i++) {
                int index = -1;
                for (int j = 0; j < numInstOfCent[i]; j++) {
                    index = nextAssignedOne(i, index, assignments);
                    instOfCent[i][j] = index;
                }
            }
        }

        return converged;
    }

    /**
     * Assign instances to centers. Part of conventional K-Means, returns true
     * if new assignment is the same as the last one.
     * 
     * @param centers
     *            all the input centers
     * @param instOfCent
     *            the instances to each center
     * @param allInstList
     *            list of all indexes
     * @param assignments
     *            assignments of instances to centers
     * @return true if converged
     */
    private boolean assignToCenters(Instance[] centers, int[][] instOfCent, int[] allInstList, int[] assignments) {

        // todo: undecided situations
        boolean converged = true; // true if new assignment is the same
        // as the old one
        int[] counts = new int[centers.length];
        int numInst = allInstList.length;
        int numCent = centers.length;
        int[] numInstOfCent = new int[numCent];
        for (int i = 0; i < numCent; i++)
            numInstOfCent[i] = 0;

        // WARNING: assignments is "input/output-parameter"
        // should not be null
        if (assignments == null) {
            assignments = new int[numInst];
            for (int i = 0; i < numInst; i++) {
                assignments[i] = -1;
            }
        }

        // WARNING: instOfCent is "input/output-parameter"
        // should not be null
        if (instOfCent == null) {
            instOfCent = new int[numCent][];
        }

        // set assignments
        for (int i = 0; i < numInst; i++) {
            Instance inst = data.getInstance(allInstList[i]);
            int newC = clusterProcessedInstance(inst, centers);
            counts[newC]++;
            if (converged && newC != assignments[i]) {
                converged = false;
            }

            numInstOfCent[newC]++;
            if (!converged)
                assignments[i] = newC;
        }
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == 0)
                converged = false;
        }

        // the following is only done
        // if assignments are not the same, because too much effort
        if (!converged) {
            // PFD(0, "assignToCenters -> it has NOT converged");
            for (int i = 0; i < numCent; i++) {
                instOfCent[i] = new int[numInstOfCent[i]];
            }

            for (int i = 0; i < numCent; i++) {
                int index = -1;
                for (int j = 0; j < numInstOfCent[i]; j++) {
                    index = nextAssignedOne(i, index, assignments);
                    instOfCent[i][j] = allInstList[index];
                }
            }
        }
        return converged;
    }

    /**
     * Searches along the assignment array for the next entry of the center in
     * question.
     * 
     * @param cent
     *            index of the center
     * @param lastIndex
     *            index to start searching
     * @param assignments
     *            assignments
     * @return index of the instance the center cent is assigned to
     */
    private int nextAssignedOne(int cent, int lastIndex, int[] assignments) {
        int len = assignments.length;
        int index = lastIndex + 1;
        while (index < len) {
            if (assignments[index] == cent) {
                return (index);
            }
            index++;
        }
        return (-1);
    }

    /**
     * Split centers in their region. Generates random vector of length =
     * variance and adds and substractsx to cluster vector to get two new
     * clusters.
     * 
     * @param rg
     *            random function
     * @param center
     *            the center that is split here
     * @param variance
     *            variance of the cluster
     * @param model
     *            data model valid
     * @return a pair of new centers
     */
    private Instance[] splitCenter(Random rg, Instance center, double variance) {
        m_NumSplits++;
        float[] randomValues = new float[center.size()];
        ArrayOperations.fillRandom(randomValues, rg);
        Instance[] children = new Instance[2];

        ArrayOperations.changeLength(Math.sqrt(variance), randomValues);

        // add random vector to center
        float[] centerValues = center.toArray();
        centerValues = ArrayOperations.add(centerValues, randomValues);
        children[0] = new SimpleInstance(centerValues, center.getWeight(), center.isClassSet(), center.getClassValue());
        ;
        // substract random vector to center
        centerValues = center.toArray();
        randomValues = new float[center.size()];
        ArrayOperations.fillRandom(randomValues, rg);
        centerValues = ArrayOperations.substract(centerValues, randomValues);
        children[1] = new SimpleInstance(centerValues, center.getWeight(), center.isClassSet(), center.getClassValue());
        return children;
    }

    /**
     * Generates new centers randomly. Used for starting centers.
     * 
     * @param random0
     *            random number generator
     * @param model
     *            data model of the instances
     * @param numClusters
     *            number of clusters
     * @return new centers
     */
    private Instance[] makeCentersRandomly(Random random0, Dataset model, int numClusters) {
        Instance[] clusterCenters = new Instance[numClusters];
        m_NumClusters = numClusters;

        // makes the new centers randomly
        for (int i = 0; i < numClusters; i++) {
            int instIndex = Math.abs(random0.nextInt()) % data.size();
            clusterCenters[i] = data.getInstance(instIndex);
        }
        return clusterCenters;
    }

    /**
     * Returns the BIC-value for the given center and instances.
     * 
     * @param instList
     *            The indices of the instances that belong to the center
     * @param center
     *            the center.
     * @param mle
     *            maximum likelihood
     * @param model
     *            the data model
     * @return the BIC value
     */
    private double calculateBIC(int[] instList, Instance center, double mle) {
        int[][] w1 = new int[1][instList.length];
        for (int i = 0; i < instList.length; i++) {
            w1[0][i] = instList[i];
        }
        double[] m = { mle };
        Instance[] w2 = new Instance[1];// (model, 1);
        w2[0] = center;
        return calculateBIC(w1, w2, m);
    }

    /**
     * Calculates the BIC for the given set of centers and instances.
     * 
     * @param instOfCent
     *            The instances that belong to their respective centers
     * @param centers
     *            the centers
     * @param mle
     *            maximum likelihood
     * @return The BIC for the input.
     */
    private double calculateBIC(int[][] instOfCent, Instance[] centers, double[] mle) {
        double loglike = 0.0;
        int numInstTotal = 0;
        int numCenters = centers.length;
        int numDimensions = centers[0].size();// .numAttributes();
        int numParameters = (numCenters - 1) + // probabilities
                numCenters * numDimensions + // means
                numCenters; // variance params
        for (int i = 0; i < centers.length; i++) {
            loglike += logLikelihoodEstimate(instOfCent[i].length, centers[i], mle[i], centers.length * 2);
            numInstTotal += instOfCent[i].length;
        }
        /*
         * diff thats how we did it loglike -= ((centers.numAttributes() + 1.0) *
         * centers.numInstances() * 1) Math.log(count);
         */
        loglike -= numInstTotal * Math.log(numInstTotal);
        // System.out.println ("numInstTotal " + numInstTotal +
        // "calculateBIC res " + loglike);
        loglike -= (numParameters / 2.0) * Math.log(numInstTotal);
        // System.out.println ("numParam " +
        // + numParameters +
        // " calculateBIC res " + loglike);
        return loglike;
    }

    /**
     * Calculates the log-likelihood of the data for the given model, taken at
     * the maximum likelihood point.
     * 
     * @param numInst
     *            number of instances that belong to the center
     * @param center
     *            the center
     * @param distortion
     *            distortion
     * @param numCent
     *            number of centers
     * @return the likelihood estimate
     */
    private double logLikelihoodEstimate(int numInst, Instance center, double distortion, int numCent) {
        // R(n) num of instances of the center -> numInst
        // K num of centers -> not used
        //
        // todo take the diff comments away
        double loglike = 0;
        /* if is new */
        if (numInst > 1) {
            /* diff variance is new */
            //
            // distortion = Sum over instances x of the center(x-center)
            // different to paper; sum should be squared
            //
            // (Sum of distances to center) / R(n) - 1.0
            // different to paper; should be R(n)-K
            double variance = distortion / (numInst - 1.0);

            //
            // -R(n)/2 * log(pi*2)
            //
            double p1 = -(numInst / 2.0) * Math.log(Math.PI * 2.0);
            /*
             * diff thats how we had it double p2 = -((ni *
             * center.numAttributes()) / 2) * distortion;
             */
            //
            // -(R(n)*M)/2 * log(variance)
            //
            double p2 = -(numInst * center.size()) / 2 * Math.log(variance);

            /*
             * diff thats how we had it, the difference is a bug in x-means
             * double p3 = - (numInst - numCent) / 2;
             */
            //
            // -(R(n)-1)/2
            //
            double p3 = -(numInst - 1.0) / 2.0;

            //
            // R(n)*log(R(n))
            //
            double p4 = numInst * Math.log(numInst);

            /*
             * diff x-means doesn't have this part double p5 = - numInst *
             * Math.log(numInstTotal);
             */

            /*
             * loglike = -(ni / 2) * Math.log(Math.PI * 2) - (ni *
             * center.numAttributes()) / 2.0) * logdistortion - (ni - k) / 2.0 +
             * ni * Math.log(ni) - ni * Math.log(r);
             */
            loglike = p1 + p2 + p3 + p4; // diff + p5;
            // the log(r) is something that can be reused.
            // as is the log(2 PI), these could provide extra speed up later on.
            // since distortion is so expensive to compute, I only do that once.
        }
        return loglike;
    }

    /**
     * Calculates the maximum likelihood estimate for the variance.
     * 
     * @param instOfCent
     *            indices of instances to each center
     * @param centers
     *            the centers
     * @return the list of distortions distortion.
     */
    private double[] distortion(int[][] instOfCent, Instance[] centers) {
        double[] distortion = new double[centers.length];
        for (int i = 0; i < centers.length; i++) {
            distortion[i] = 0.0;
            for (int j = 0; j < instOfCent[i].length; j++) {
                distortion[i] += dm.calculateDistance(data.getInstance(instOfCent[i][j]), centers[i]);
            }
        }
        /*
         * diff not done in x-means res *= 1.0 / (count -
         * centers.numInstances());
         */
        return distortion;
    }

    /**
     * Clusters an instance.
     * 
     * @param instance
     *            the instance to assign a cluster to.
     * @param centers
     *            the centers to cluster the instance to.
     * @return a cluster index.
     */
    private int clusterProcessedInstance(Instance instance, Instance[] centers) {

        double minDist = Integer.MAX_VALUE;
        int bestCluster = 0;
        for (int i = 0; i < centers.length; i++) {
            double dist = dm.calculateDistance(instance, centers[i]);

            if (dist < minDist) {
                minDist = dist;
                bestCluster = i;
            }
        }
        ;
        return bestCluster;
    }

    
}
