/**
 * EMClustering.java
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
 * Copyright (c) 1999, Mark Hall (mhall@cs.waikato.ac.nz)
 * Copyright (c) 1999, Eibe Frank (eibe@cs.waikato.ac.nz)
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Simple EM (expectation maximisation) class.<br/> <br/> EM assigns a
 * probability distribution to each instance which indicates the probability of
 * it belonging to each of the clusters. EM can decide how many clusters to
 * create by cross validation, or you may specify apriori how many clusters to
 * generate.<br/> <br/> The cross validation performed to determine the number
 * of clusters is done in the following steps:<br/> 1. the number of clusters
 * is set to 1<br/> 2. the training set is split randomly into 10 folds.<br/>
 * 3. EM is performed 10 times using the 10 folds the usual CV way.<br/> 4. the
 * loglikelihood is averaged over all 10 results.<br/> 5. if loglikelihood has
 * increased the number of clusters is increased by 1 and the program continues
 * at step 2. <br/> <br/> The number of folds is fixed to 10, as long as the
 * number of instances in the training set is not smaller 10. If this is the
 * case the number of folds is set equal to the number of instances. <p/>
 * 
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Thomas Abeel
 */
public class EMClustering implements Clusterer {
    /** hold the normal estimators for each cluster */
    private double m_modelNormal[][][];

    /** default minimum standard deviation */
    private double m_minStdDev = 1e-6;

    private double[] m_minStdDevPerAtt;

    /** hold the weights of each instance for each cluster */
    private double m_weights[][];

    /** the prior probabilities for clusters */
    private double m_priors[];

    /** training instances */
    private Dataset m_theInstances = null;

    /** number of clusters selected by the user or cross validation */
    private int m_num_clusters;

    /** number of attributes */
    private int m_num_attribs;

    /** maximum iterations to perform */
    private int m_max_iterations = 100;

    /**
     * Initialise estimators and storage.
     * 
     * @param inst
     *            the instances
     * @throws Exception
     *             if initialization fails
     */
    private void EM_Init(Dataset data) {
        // run k means 10 times and choose best solution
        Dataset[] best = null;
        Instance datasetSTD = DatasetTools.getStandardDeviation(data);
        double bestSqE = Double.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            KMeans sk = new KMeans(m_num_clusters, 100);
            ClusterEvaluation ce = new SumOfSquaredErrors(new EuclideanDistance());
            Dataset[] tmp = sk.executeClustering(data);
            double score = ce.score(tmp);
            // sk.setSeed(m_rr.nextInt());
            // sk.setNumClusters(m_num_clusters);
            // sk.buildClusterer(inst);
            if (score < bestSqE) {
                bestSqE = score;
                best = tmp;

            }
        }

        // initialize with best k-means solution
        m_num_clusters = best.length;
        m_weights = new double[data.size()][m_num_clusters];
        // m_model = new Estimator[m_num_clusters][m_num_attribs];
        m_modelNormal = new double[m_num_clusters][m_num_attribs][3];
        m_priors = new double[m_num_clusters];

        Instance[] centers = new Instance[m_num_clusters];
        int[] clusterSizes = new int[m_num_clusters];
        Instance[] stdD = new Instance[m_num_clusters];
        for (int i = 0; i < best.length; i++) {
            centers[i] = DatasetTools.getCentroid(best[i], new EuclideanDistance());
            clusterSizes[i] = best[i].size();
            stdD[i] = DatasetTools.getStandardDeviation(best[i]);
        }

        for (int i = 0; i < m_num_clusters; i++) {
            Instance center = centers[i];
            for (int j = 0; j < m_num_attribs; j++) {
                double minStdD = (m_minStdDevPerAtt != null) ? m_minStdDevPerAtt[j] : m_minStdDev;
                double mean = center.getValue(j);
                m_modelNormal[i][j][0] = mean;
                double stdv = stdD[i].getValue(j);
                if (stdv < minStdD) {
                    stdv = datasetSTD.getValue(j);// data.attributeStats(j).numericStats.stdDev;
                    if (Double.isInfinite(stdv)) {
                        stdv = minStdD;
                    }
                    if (stdv < minStdD) {
                        stdv = minStdD;
                    }
                }
                if (stdv <= 0) {
                    stdv = m_minStdDev;
                }

                m_modelNormal[i][j][1] = stdv;
                m_modelNormal[i][j][2] = 1.0;
                // }
            }
        }

        for (int j = 0; j < m_num_clusters; j++) {
            // m_priors[j] += 1.0;
            m_priors[j] = clusterSizes[j];
        }
        normalize(m_priors);
    }

    /**
     * Normalizes the doubles in the array by their sum.
     * 
     * @param doubles
     *            the array of double
     * @exception IllegalArgumentException
     *                if sum is Zero or NaN
     */
    private static void normalize(double[] doubles) {

        double sum = 0;
        for (int i = 0; i < doubles.length; i++) {
            sum += doubles[i];
        }
        normalize(doubles, sum);
    }

    /**
     * Normalizes the doubles in the array using the given value.
     * 
     * @param doubles
     *            the array of double
     * @param sum
     *            the value by which the doubles are to be normalized
     * @exception IllegalArgumentException
     *                if sum is zero or NaN
     */
    private static void normalize(double[] doubles, double sum) {

        if (Double.isNaN(sum)) {
            throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
        }
        if (sum == 0) {
            // Maybe this should just be a return.
            throw new IllegalArgumentException("Can't normalize array. Sum is zero.");
        }
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] /= sum;
        }
    }

    /**
     * calculate prior probabilites for the clusters
     * 
     * @param inst
     *            the instances
     * @throws Exception
     *             if priors can't be calculated
     */
    private void estimate_priors(Dataset inst) throws Exception {

        for (int i = 0; i < m_num_clusters; i++) {
            m_priors[i] = 0.0;
        }

        for (int i = 0; i < inst.size(); i++) {
            for (int j = 0; j < m_num_clusters; j++) {
                m_priors[j] += inst.getInstance(i).getWeight() * m_weights[i][j];
            }
        }

        normalize(m_priors);
    }

    /** Constant for normal distribution. */
    private static double m_normConst = Math.log(Math.sqrt(2 * Math.PI));

    /**
     * Density function of normal distribution.
     * 
     * @param x
     *            input value
     * @param mean
     *            mean of distribution
     * @param stdDev
     *            standard deviation of distribution
     * @return the density
     */
    private double logNormalDens(double x, double mean, double stdDev) {

        double diff = x - mean;
        return -(diff * diff / (2 * stdDev * stdDev)) - m_normConst - Math.log(stdDev);
    }

    /**
     * New probability estimators for an iteration
     */
    private void new_estimators() {
        for (int i = 0; i < m_num_clusters; i++) {
            for (int j = 0; j < m_num_attribs; j++) {
                m_modelNormal[i][j][0] = m_modelNormal[i][j][1] = m_modelNormal[i][j][2] = 0.0;
            }
        }
    }

    /**
     * The M step of the EM algorithm.
     * 
     * @param inst
     *            the training instances
     * @throws Exception
     *             if something goes wrong
     */
    private void M(Dataset inst) throws Exception {

        int i, j, l;

        new_estimators();
        Instance stdD = DatasetTools.getStandardDeviation(inst);
        for (i = 0; i < m_num_clusters; i++) {
            for (j = 0; j < m_num_attribs; j++) {
                for (l = 0; l < inst.size(); l++) {
                    Instance in = inst.getInstance(l);
                    m_modelNormal[i][j][0] += (in.getValue(j) * in.getWeight() * m_weights[l][i]);
                    m_modelNormal[i][j][2] += in.getWeight() * m_weights[l][i];
                    m_modelNormal[i][j][1] += (in.getValue(j) * in.getValue(j) * in.getWeight() * m_weights[l][i]);
                }
            }
        }

        // calcualte mean and std deviation for numeric attributes
        for (j = 0; j < m_num_attribs; j++) {
            // if (!inst.attribute(j).isNominal()) {
            for (i = 0; i < m_num_clusters; i++) {
                if (m_modelNormal[i][j][2] <= 0) {
                    m_modelNormal[i][j][1] = Double.MAX_VALUE;
                    // m_modelNormal[i][j][0] = 0;
                    m_modelNormal[i][j][0] = m_minStdDev;
                } else {

                    // variance
                    m_modelNormal[i][j][1] = (m_modelNormal[i][j][1] - (m_modelNormal[i][j][0] * m_modelNormal[i][j][0] / m_modelNormal[i][j][2]))
                            / (m_modelNormal[i][j][2]);

                    if (m_modelNormal[i][j][1] < 0) {
                        m_modelNormal[i][j][1] = 0;
                    }

                    // std dev
                    double minStdD = (m_minStdDevPerAtt != null) ? m_minStdDevPerAtt[j] : m_minStdDev;

                    m_modelNormal[i][j][1] = Math.sqrt(m_modelNormal[i][j][1]);

                    if ((m_modelNormal[i][j][1] <= minStdD)) {
                        m_modelNormal[i][j][1] = stdD.getValue(j);// inst.attributeStats(j).numericStats.stdDev;
                        if ((m_modelNormal[i][j][1] <= minStdD)) {
                            m_modelNormal[i][j][1] = minStdD;
                        }
                    }
                    if ((m_modelNormal[i][j][1] <= 0)) {
                        m_modelNormal[i][j][1] = m_minStdDev;
                    }
                    if (Double.isInfinite(m_modelNormal[i][j][1])) {
                        m_modelNormal[i][j][1] = m_minStdDev;
                    }

                    // mean
                    m_modelNormal[i][j][0] /= m_modelNormal[i][j][2];
                }
            }
            // }
        }
    }

    /**
     * The E step of the EM algorithm. Estimate cluster membership
     * probabilities.
     * 
     * @param inst
     *            the training instances
     * @param change_weights
     *            whether to change the weights
     * @return the average log likelihood
     * @throws Exception
     *             if computation fails
     */
    private double E(Dataset inst, boolean change_weights) throws Exception {

        double loglk = 0.0, sOW = 0.0;

        for (int l = 0; l < inst.size(); l++) {

            Instance in = inst.getInstance(l);

            loglk += in.getWeight() * logDensityForInstance(in);
            sOW += in.getWeight();

            if (change_weights) {
                m_weights[l] = distributionForInstance(in);
            }
        }

        // reestimate priors
        if (change_weights) {
            estimate_priors(inst);
        }
        return loglk / sOW;
    }

    /**
     * Computes the density for a given instance.
     * 
     * @param instance
     *            the instance to compute the density for
     * @return the density.
     * @exception Exception
     *                if the density could not be computed successfully
     */
    private double logDensityForInstance(Instance instance) {

        double[] a = logJointDensitiesForInstance(instance);
        double max = a[maxIndex(a)];
        double sum = 0.0;

        for (int i = 0; i < a.length; i++) {
            sum += Math.exp(a[i] - max);
        }

        return max + Math.log(sum);
    }

    /**
     * Returns the cluster probability distribution for an instance.
     * 
     * @param instance
     *            the instance to be clustered
     * @return the probability distribution
     * @throws Exception
     *             if computation fails
     */
    private double[] distributionForInstance(Instance instance) {

        return logs2probs(logJointDensitiesForInstance(instance));
    }

    /**
     * Returns index of maximum element in a given array of doubles. First
     * maximum is returned.
     * 
     * @param doubles
     *            the array of doubles
     * @return the index of the maximum element
     */
    private static/* @pure@ */int maxIndex(double[] doubles) {

        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || (doubles[i] > maximum)) {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }

    /**
     * Converts an array containing the natural logarithms of probabilities
     * stored in a vector back into probabilities. The probabilities are assumed
     * to sum to one.
     * 
     * @param a
     *            an array holding the natural logarithms of the probabilities
     * @return the converted array
     */
    private static double[] logs2probs(double[] a) {

        double max = a[maxIndex(a)];
        double sum = 0.0;

        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = Math.exp(a[i] - max);
            sum += result[i];
        }

        normalize(result, sum);

        return result;
    }

    /**
     * Returns the cluster priors.
     * 
     * @return the cluster priors
     */
    private double[] clusterPriors() {

        double[] n = new double[m_priors.length];

        System.arraycopy(m_priors, 0, n, 0, n.length);
        return n;
    }

    /**
     * Returns the logs of the joint densities for a given instance.
     * 
     * @param inst
     *            the instance
     * @return the array of values
     * @exception Exception
     *                if values could not be computed
     */
    private double[] logJointDensitiesForInstance(Instance inst) {

        double[] weights = logDensityPerClusterForInstance(inst);
        double[] priors = clusterPriors();

        for (int i = 0; i < weights.length; i++) {
            if (priors[i] > 0) {
                weights[i] += Math.log(priors[i]);
            } else {
                throw new IllegalArgumentException("Cluster empty!");
            }
        }
        return weights;
    }

    /**
     * Computes the log of the conditional density (per cluster) for a given
     * instance.
     * 
     * @param inst
     *            the instance to compute the density for
     * @return an array containing the estimated densities
     * @throws Exception
     *             if the density could not be computed successfully
     */
    private double[] logDensityPerClusterForInstance(Instance inst) {

        int i, j;
        double logprob;
        double[] wghts = new double[m_num_clusters];

        for (i = 0; i < m_num_clusters; i++) {
            // System.err.println("Cluster : "+i);
            logprob = 0.0;

            for (j = 0; j < m_num_attribs; j++) {
                logprob += logNormalDens(inst.getValue(j), m_modelNormal[i][j][0], m_modelNormal[i][j][1]);
            }

            wghts[i] = logprob;
        }
        return wghts;
    }

    /**
     * Perform the EM algorithm
     * 
     * return the log likelyhood of the data.
     */
    private double doEM() {

        m_num_attribs = m_theInstances.getInstance(0).size();// .numAttributes();

        // fit full training set
        EM_Init(m_theInstances);
        // m_loglikely = iterate(m_theInstances);
        return iterate(m_theInstances);
    }

    /**
     * iterates the E and M steps until the log likelihood of the data
     * converges.
     * 
     * @param inst
     *            the training instances.
     * @param report
     *            be verbose.
     * @return the log likelihood of the data
     * @throws Exception
     *             if something goes wrong
     */
    private double iterate(Dataset inst) {

        int i;
        double llkold = 0.0;
        double llk = 0.0;

        boolean ok = false;
        // int seed = getSeed();
        int restartCount = 0;
        while (!ok) {
            try {
                for (i = 0; i < m_max_iterations; i++) {
                    llkold = llk;
                    llk = E(inst, true);

                    if (i > 0) {
                        if ((llk - llkold) < 1e-6) {
                            break;
                        }
                    }
                    M(inst);
                }
                ok = true;
            } catch (Exception ex) {
                System.err.println("Restarting after training failure");
                ex.printStackTrace();
                // seed++;
                restartCount++;
                EM_Init(m_theInstances);
            }
        }

        return llk;
    }
/** 
 * XXX DOC
 *
 */
    public EMClustering(){
        this(4);
    }
  
    /**
     * XXX DOC
     * @param numClusters
     */
    public EMClustering(int numClusters) {
        this.m_num_clusters = numClusters;
    }

    public Dataset[] executeClustering(Dataset data) {
        m_theInstances = data;
        doEM();
        // construct clusters based on the probabiliy distributions
        Dataset[] clusters = new Dataset[m_num_clusters];
        for (int j = 0; j < m_num_clusters; j++) {
            clusters[j] = new SimpleDataset();
        }
        for (int i = 0; i < data.size(); i++) {
            double[] distr = distributionForInstance(data.getInstance(i));
            for (int j = 0; j < m_num_clusters; j++) {
                if (distr[j] > clusterThreshold) {
                    clusters[j].addInstance(data.getInstance(i));
                }
            }
        }
        return clusters;
    }

    /**
     * The minimum probability an instance should have before it belongs to a
     * cluster.
     */
    private double clusterThreshold = 0.75;
}
