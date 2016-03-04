/**
 * %SVN.HEADER%
 */
package net.sf.javaml.clustering;

import java.io.Serializable;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.filter.normalize.NormalizeMean;


/**
 * Class implementing the Cobweb and Classit clustering algorithms.<br/> <br/>
 * Note: the application of node operators (merging, splitting etc.) in terms of
 * ordering and priority differs (and is somewhat ambiguous) between the
 * original Cobweb and Classit papers. This algorithm always compares the best
 * host, adding a new leaf, merging the two best hosts, and splitting the best
 * host when considering where to place a new instance.<br/> <br/> For more
 * information see:<br/> <br/> D. Fisher (1987). Knowledge acquisition via
 * incremental conceptual clustering. Machine Learning. 2(2):139-172.<br/>
 * <br/> J. H. Gennari, P. Langley, D. Fisher (1990). Models of incremental
 * concept formation. Artificial Intelligence. 40:11-61. <p/> BibTeX:
 * 
 * <pre>
 * &#64;article{Fisher1987,
 *    author = {D. Fisher},
 *    journal = {Machine Learning},
 *    number = {2},
 *    pages = {139-172},
 *    title = {Knowledge acquisition via incremental conceptual clustering},
 *    volume = {2},
 *    year = {1987}
 * }
 * 
 * &#64;article{Gennari1990,
 *    author = {J. H. Gennari and P. Langley and D. Fisher},
 *    journal = {Artificial Intelligence},
 *    pages = {11-61},
 *    title = {Models of incremental concept formation},
 *    volume = {40},
 *    year = {1990}
 * }
 * </pre>
 * 
 * <p/>
 * 
 * @author Mark Hall
 * @author Thomas Abeel
 */
public class Cobweb implements Clusterer {
    private class Stats implements Serializable {

        /** for serialization */
        private static final long serialVersionUID = -8610544539090024102L;

        /** The number of values seen */
        private double count = 0;

        /** The sum of values seen */
        private double sum = 0;

        /** The sum of values squared seen */
        private double sumSq = 0;

        /** The std deviation of values at the last calculateDerived() call */
        private double stdDev = Double.NaN;

        /** The minimum value seen, or Double.NaN if no values seen */
        private double min = Double.NaN;

        /** The maximum value seen, or Double.NaN if no values seen */
        private double max = Double.NaN;

        /**
         * Adds a value that has been seen n times to the observed values
         * 
         * @param value
         *            the observed value
         * @param n
         *            the number of times to add value
         */
        private void add(double value, double n) {

            sum += value * n;
            sumSq += value * value * n;
            count += n;
            if (Double.isNaN(min)) {
                min = max = value;
            } else if (value < min) {
                min = value;
            } else if (value > max) {
                max = value;
            }
        }

        /**
         * Subtracts a value that has been seen n times from the observed values
         * 
         * @param value
         *            the observed value
         * @param n
         *            the number of times to subtract value
         */
        private void subtract(double value, double n) {
            sum -= value * n;
            sumSq -= value * value * n;
            count -= n;
        }

        /**
         * Tells the object to calculate any statistics that don't have their
         * values automatically updated during add. Currently updates the mean
         * and standard deviation.
         */
        private void calculateDerived() {

            stdDev = Double.NaN;
            if (count > 0) {
                stdDev = Double.POSITIVE_INFINITY;
                if (count > 1) {
                    stdDev = sumSq - (sum * sum) / count;
                    stdDev /= (count - 1);
                    if (stdDev < 0) {
                        stdDev = 0;
                    }
                    stdDev = Math.sqrt(stdDev);
                }
            }
        }
    }

    /**
     * Inner class handling node operations for Cobweb.
     * 
     * @see Serializable
     */
    private class CNode {

        /**
         * Within cluster attribute statistics
         */
        private Stats[] m_attStats;

        /**
         * Number of attributes
         */
        private int m_numAttributes;

        /**
         * Instances at this node
         */
        private Dataset m_clusterInstances = null;

        /**
         * Children of this node
         */
        private Vector<CNode> m_children = null;

        /**
         * Total instances at this node
         */
        private double m_totalInstances = 0.0;

        /**
         * Creates an empty <code>CNode</code> instance.
         * 
         * @param numAttributes
         *            the number of attributes in the data
         */
        private CNode(int numAttributes) {
            m_numAttributes = numAttributes;
        }

        /**
         * Creates a new leaf <code>CNode</code> instance.
         * 
         * @param numAttributes
         *            the number of attributes in the data
         * @param leafInstance
         *            the instance to store at this leaf
         */
        private CNode(int numAttributes, Instance leafInstance) {
            this(numAttributes);
            if (m_clusterInstances == null) {
                m_clusterInstances = new DefaultDataset();
            }
            m_clusterInstances.add(leafInstance);
            updateStats(leafInstance, false);
        }

        /**
         * Adds an instance to this cluster.
         * 
         * @param newInstance
         *            the instance to add
         * @throws Exception
         *             if an error occurs
         */
        private void addInstance(Instance newInstance) {// Add the instance to
            // this cluster
            if (m_clusterInstances == null) {
                m_clusterInstances = new DefaultDataset();// (newInstance.dataset(),
                // 1);
                m_clusterInstances.add(newInstance);
                updateStats(newInstance, false);

            } else if (m_children == null) {
                /*
                 * we are a leaf, so make our existing instance(s) into a child
                 * and then add the new instance as a child
                 */
                m_children = new Vector<CNode>();
                CNode tempSubCluster = new CNode(m_numAttributes, m_clusterInstances.instance(0));

                // System.out.println("Dumping
                // "+m_clusterInstances.numInstances());
                for (int i = 1; i < m_clusterInstances.size(); i++) {
                    tempSubCluster.m_clusterInstances.add(m_clusterInstances.instance(i));
                    tempSubCluster.updateStats(m_clusterInstances.instance(i), false);
                }
                m_children = new Vector<CNode>();
                m_children.addElement(tempSubCluster);
                m_children.addElement(new CNode(m_numAttributes, newInstance));

                m_clusterInstances.add(newInstance);
                updateStats(newInstance, false);

                // here is where we check against cutoff (also check cutoff
                // in findHost)
                // System.out.println(categoryUtility() + "\t" + m_cutoff);
                if (categoryUtility() < m_cutoff) {
                    // System.out.println("\tCutting (leaf add) ");
                    m_children = null;
                }

            } else {

                // otherwise, find the best host for this instance
                CNode bestHost = findHost(newInstance, false);
                if (bestHost != null) {
                    // now add to the best host
                    bestHost.addInstance(newInstance);
                }
            }
        }

        /**
         * Temporarily adds a new instance to each of this nodes children in
         * turn and computes the category utility.
         * 
         * @param newInstance
         *            the new instance to evaluate
         * @return an array of category utility values---the result of
         *         considering each child in turn as a host for the new instance
         * @throws Exception
         *             if an error occurs
         */
        private double[] cuScoresForChildren(Instance newInstance) {
            // look for a host in existing children
            double[] categoryUtils = new double[m_children.size()];

            // look for a home for this instance in the existing children
            for (int i = 0; i < m_children.size(); i++) {
                CNode temp = (CNode) m_children.elementAt(i);
                // tentitively add the new instance to this child
                temp.updateStats(newInstance, false);
                categoryUtils[i] = categoryUtility();

                // remove the new instance from this child
                temp.updateStats(newInstance, true);
            }
            return categoryUtils;
        }

        private double cuScoreForBestTwoMerged(CNode merged, CNode a, CNode b, Instance newInstance) {

            double mergedCU = -Double.MAX_VALUE;
            // consider merging the best and second
            // best.
            merged.m_clusterInstances = new DefaultDataset();// (m_clusterInstances,
            // 1);

            merged.addChildNode(a);
            merged.addChildNode(b);
            merged.updateStats(newInstance, false); // add new instance to stats
            // remove the best and second best nodes
            m_children.removeElementAt(m_children.indexOf(a));
            m_children.removeElementAt(m_children.indexOf(b));
            m_children.addElement(merged);
            mergedCU = categoryUtility();
            // restore the status quo
            merged.updateStats(newInstance, true);
            m_children.removeElementAt(m_children.indexOf(merged));
            m_children.addElement(a);
            m_children.addElement(b);
            return mergedCU;
        }

        /**
         * Finds a host for the new instance in this nodes children. Also
         * considers merging the two best hosts and splitting the best host.
         * 
         * @param newInstance
         *            the instance to find a host for
         * @param structureFrozen
         *            true if the instance is not to be added to the tree and
         *            instead the best potential host is to be returned
         * @return the best host
         * @throws Exception
         *             if an error occurs
         */
        private CNode findHost(Instance newInstance, boolean structureFrozen) {

            if (!structureFrozen) {
                updateStats(newInstance, false);
            }

            // look for a host in existing children and also consider as a new
            // leaf
            double[] categoryUtils = cuScoresForChildren(newInstance);

            // make a temporary new leaf for this instance and get CU
            CNode newLeaf = new CNode(m_numAttributes, newInstance);
            m_children.addElement(newLeaf);
            double bestHostCU = categoryUtility();
            CNode finalBestHost = newLeaf;

            // remove new leaf when seaching for best and second best nodes to
            // consider for merging and splitting
            m_children.removeElementAt(m_children.size() - 1);

            // now determine the best host (and the second best)
            int best = 0;
            int secondBest = 0;
            for (int i = 0; i < categoryUtils.length; i++) {
                if (categoryUtils[i] > categoryUtils[secondBest]) {
                    if (categoryUtils[i] > categoryUtils[best]) {
                        secondBest = best;
                        best = i;
                    } else {
                        secondBest = i;
                    }
                }
            }

            CNode a = (CNode) m_children.elementAt(best);
            CNode b = (CNode) m_children.elementAt(secondBest);
            if (categoryUtils[best] > bestHostCU) {
                bestHostCU = categoryUtils[best];
                finalBestHost = a;
                // System.out.println("Node is best");
            }

            if (structureFrozen) {
                if (finalBestHost == newLeaf) {
                    return null; // *this* node is the best host
                } else {
                    return finalBestHost;
                }
            }

            double mergedCU = -Double.MAX_VALUE;
            CNode merged = new CNode(m_numAttributes);
            if (a != b) {
                mergedCU = cuScoreForBestTwoMerged(merged, a, b, newInstance);

                if (mergedCU > bestHostCU) {
                    bestHostCU = mergedCU;
                    finalBestHost = merged;
                }
            }

            // Consider splitting the best
            double splitCU = -Double.MAX_VALUE;
            double splitBestChildCU = -Double.MAX_VALUE;
            double splitPlusNewLeafCU = -Double.MAX_VALUE;
            double splitPlusMergeBestTwoCU = -Double.MAX_VALUE;
            if (a.m_children != null) {
                Vector<CNode> tempChildren = new Vector<CNode>();

                for (int i = 0; i < m_children.size(); i++) {
                    CNode existingChild = (CNode) m_children.elementAt(i);
                    if (existingChild != a) {
                        tempChildren.addElement(existingChild);
                    }
                }
                for (int i = 0; i < a.m_children.size(); i++) {
                    CNode promotedChild = (CNode) a.m_children.elementAt(i);
                    tempChildren.addElement(promotedChild);
                }
                // also add the new leaf
                tempChildren.addElement(newLeaf);

                Vector<CNode> saveStatusQuo = m_children;
                m_children = tempChildren;
                splitPlusNewLeafCU = categoryUtility(); // split + new leaf
                // remove the new leaf
                tempChildren.removeElementAt(tempChildren.size() - 1);
                // now look for best and second best
                categoryUtils = cuScoresForChildren(newInstance);

                // now determine the best host (and the second best)
                best = 0;
                secondBest = 0;
                for (int i = 0; i < categoryUtils.length; i++) {
                    if (categoryUtils[i] > categoryUtils[secondBest]) {
                        if (categoryUtils[i] > categoryUtils[best]) {
                            secondBest = best;
                            best = i;
                        } else {
                            secondBest = i;
                        }
                    }
                }
                CNode sa = (CNode) m_children.elementAt(best);
                CNode sb = (CNode) m_children.elementAt(secondBest);
                splitBestChildCU = categoryUtils[best];

                // now merge best and second best
                CNode mergedSplitChildren = new CNode(m_numAttributes);
                if (sa != sb) {
                    splitPlusMergeBestTwoCU = cuScoreForBestTwoMerged(mergedSplitChildren, sa, sb, newInstance);
                }
                splitCU = (splitBestChildCU > splitPlusNewLeafCU) ? splitBestChildCU : splitPlusNewLeafCU;
                splitCU = (splitCU > splitPlusMergeBestTwoCU) ? splitCU : splitPlusMergeBestTwoCU;

                if (splitCU > bestHostCU) {
                    bestHostCU = splitCU;
                    finalBestHost = this;
                    // tempChildren.removeElementAt(tempChildren.size()-1);
                } else {
                    // restore the status quo
                    m_children = saveStatusQuo;
                }
            }

            if (finalBestHost != this) {
                // can commit the instance to the set of instances at this node
                m_clusterInstances.add(newInstance);
            } else {
                m_numberSplits++;
            }

            if (finalBestHost == merged) {
                m_numberMerges++;
                m_children.removeElementAt(m_children.indexOf(a));
                m_children.removeElementAt(m_children.indexOf(b));
                m_children.addElement(merged);
            }

            if (finalBestHost == newLeaf) {
                finalBestHost = new CNode(m_numAttributes);
                m_children.addElement(finalBestHost);
            }

            if (bestHostCU < m_cutoff) {
                if (finalBestHost == this) {
                    // splitting was the best, but since we are cutting all
                    // children
                    // recursion is aborted and we still need to add the
                    // instance
                    // to the set of instances at this node
                    m_clusterInstances.add(newInstance);
                }
                m_children = null;
                finalBestHost = null;
            }

            if (finalBestHost == this) {
                // splitting is still the best, so downdate the stats as
                // we'll be recursively calling on this node
                updateStats(newInstance, true);
            }

            return finalBestHost;
        }

        /**
         * Adds the supplied node as a child of this node. All of the child's
         * instances are added to this nodes instances
         * 
         * @param child
         *            the child to add
         */
        private void addChildNode(CNode child) {
            for (int i = 0; i < child.m_clusterInstances.size(); i++) {
                Instance temp = child.m_clusterInstances.instance(i);
                m_clusterInstances.add(temp);
                updateStats(temp, false);
            }

            if (m_children == null) {
                m_children = new Vector<CNode>();
            }
            m_children.addElement(child);
        }

        /**
         * Computes the utility of all children with respect to this node
         * 
         * @return the category utility of the children with respect to this
         *         node.
         * @throws Exception
         *             if there are no children
         */
        private double categoryUtility() {

            double totalCU = 0;

            for (int i = 0; i < m_children.size(); i++) {
                CNode child = m_children.elementAt(i);
                totalCU += categoryUtilityChild(child);
            }

            totalCU /= (double) m_children.size();
            return totalCU;
        }

        /**
         * Computes the utility of a single child with respect to this node
         * 
         * @param child
         *            the child for which to compute the utility
         * @return the utility of the child with respect to this node
         * @throws Exception
         *             if something goes wrong
         */
        private double categoryUtilityChild(CNode child) {

            double sum = 0;
            for (int i = 0; i < m_numAttributes; i++) {
                sum += ((m_normal / child.getStandardDev(i)) - (m_normal / getStandardDev(i)));
            }
            return (child.m_totalInstances / m_totalInstances) * sum;
        }

        /**
         * Returns the standard deviation of a numeric attribute
         * 
         * @param attIndex
         *            the index of the attribute
         * @return the standard deviation
         * @throws Exception
         *             if an error occurs
         */
        private double getStandardDev(int attIndex) {
            m_attStats[attIndex].calculateDerived();
            double stdDev = m_attStats[attIndex].stdDev;
            if (Double.isNaN(stdDev) || Double.isInfinite(stdDev)) {
                return m_acuity;
            }

            return Math.max(m_acuity, stdDev);
        }

        /**
         * Update attribute stats using the supplied instance.
         * 
         * @param updateInstance
         *            the instance for updating
         * @param delete
         *            true if the values of the supplied instance are to be
         *            removed from the statistics
         */
        private void updateStats(Instance updateInstance, boolean delete) {

            if (m_attStats == null) {
                m_attStats = new Stats[m_numAttributes];
                for (int i = 0; i < m_numAttributes; i++) {
                    m_attStats[i] = new Stats();

                }
            }
            for (int i = 0; i < m_numAttributes; i++) {
                double value = updateInstance.value(i);
                if (delete) {
                    m_attStats[i].subtract(value, 1);
                } else {
                    m_attStats[i].add(value, 1);
                }

            }
            m_totalInstances += (delete) ? -1.0 : 1;
        }

        /**
         * Recursively assigns numbers to the nodes in the tree.
         * 
         * @param cl_num
         *            an <code>int[]</code> value
         * @throws Exception
         *             if an error occurs
         */
        private void assignClusterNums(int[] cl_num) {
            // System.out.println("Assign numbers...");
            if (m_children != null && m_children.size() < 2) {
                throw new RuntimeException("assignClusterNums: tree not built correctly!");
            }

            // this.m_clusterNum = cl_num[0];
            cl_num[0]++;
            if (m_children != null) {
                for (int i = 0; i < m_children.size(); i++) {
                    CNode child = m_children.elementAt(i);
                    child.assignClusterNums(cl_num);
                }
            }
        }

    }

    /**
     * Normal constant.
     */
    private static final double m_normal = 1.0 / (2 * Math.sqrt(Math.PI));

    /**
     * default constructor
     * 
     */
    public Cobweb() {
        this(0.5, 0.01);
    }

    /**
     * XXX DOC
     * 
     * @param acuity
     * @param cutoff
     * 
     */
    public Cobweb(double acuity, double cutoff) {
        this.m_acuity = acuity;
        this.m_cutoff = cutoff * m_normal;
    }

    /**
     * Acuity (minimum standard deviation).
     */
    private double m_acuity = 0.50;

    /**
     * Cutoff (minimum category utility).
     */
    private double m_cutoff = 0.01 * Cobweb.m_normal;

    /**
     * Holds the root of the Cobweb tree.
     */
    private CNode m_cobwebTree = null;

    /**
     * Number of clusters (nodes in the tree). Must never be queried directly,
     * only via the method numberOfClusters(). Otherwise it's not guaranteed
     * that it contains the correct value.
     * 
     * @see #numberOfClusters()
     * @see #m_numberOfClustersDetermined
     */
    private int m_numberOfClusters = -1;

    /** whether the number of clusters was already determined */
    private boolean m_numberOfClustersDetermined = false;

    /** the number of splits that happened */
    private int m_numberSplits;

    /** the number of merges that happened */
    private int m_numberMerges;

    /**
     * determines the number of clusters if necessary
     * 
     * @see #m_numberOfClusters
     * @see #m_numberOfClustersDetermined
     */
    private void determineNumberOfClusters() {
        if (!m_numberOfClustersDetermined && (m_cobwebTree != null)) {
            int[] numClusts = new int[1];
            numClusts[0] = 0;
            m_cobwebTree.assignClusterNums(numClusts);
            m_numberOfClusters = numClusts[0];
            m_numberOfClustersDetermined = true;
        }
    }

    /**
     * Adds an instance to the clusterer.
     * 
     * @param newInstance
     *            the instance to be added
     * @throws Exception
     *             if something goes wrong
     */
    private void updateClusterer(Instance newInstance) {
        m_numberOfClustersDetermined = false;

        if (m_cobwebTree == null) {
            m_cobwebTree = new CNode(newInstance.noAttributes(), newInstance);
        } else {
            m_cobwebTree.addInstance(newInstance);
        }
    }

    private DatasetFilter filter = new NormalizeMean();

    public Dataset[] cluster(Dataset data) {

        filter.filter(data);
        m_numberOfClusters = -1;
        m_cobwebTree = null;
        m_numberSplits = 0;
        m_numberMerges = 0;
        for (int i = 0; i < data.size(); i++) {
            updateClusterer(data.instance(i));
        }
        determineNumberOfClusters();
        // printNode(m_cobwebTree, 0);

        Vector<Dataset> clusters = new Vector<Dataset>();
        createClusters(m_cobwebTree, clusters);
        Dataset[] out = new Dataset[clusters.size()];
        clusters.toArray(out);
        return out;
    }

    private void createClusters(CNode tree, Vector<Dataset> clusters) {
        if (tree.m_children != null) {
            for (CNode y : tree.m_children) {
                createClusters(y, clusters);
            }
        } else {
            Dataset tmp = new DefaultDataset();
            Dataset fromTree = tree.m_clusterInstances;
            for (int i = 0; i < fromTree.size(); i++) {
                tmp.add(fromTree.instance(i));
            }
            clusters.add(tmp);
        }

    }
}
