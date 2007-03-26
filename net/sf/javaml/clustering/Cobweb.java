/**
 * Cobweb.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General private License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General private License for more details.
 * 
 * You should have received a copy of the GNU General private License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (C) 2001 Mark Hall
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.NormalizedEuclideanDistance;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.filter.NormalizeMean;
import net.sf.javaml.filter.NormalizeMidrange;
import net.sf.javaml.utils.MathUtils;

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

        /** The mean of values at the last calculateDerived() call */
        private double mean = Double.NaN;

        /** The minimum value seen, or Double.NaN if no values seen */
        private double min = Double.NaN;

        /** The maximum value seen, or Double.NaN if no values seen */
        private double max = Double.NaN;

        /**
         * Adds a value to the observed values
         * 
         * @param value
         *            the observed value
         */
        private void add(double value) {

            add(value, 1);
        }

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
         * Removes a value to the observed values (no checking is done that the
         * value being removed was actually added).
         * 
         * @param value
         *            the observed value
         */
        private void subtract(double value) {
            subtract(value, 1);
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

            mean = Double.NaN;
            stdDev = Double.NaN;
            if (count > 0) {
                mean = sum / count;
                stdDev = Double.POSITIVE_INFINITY;
                if (count > 1) {
                    stdDev = sumSq - (sum * sum) / count;
                    stdDev /= (count - 1);
                    if (stdDev < 0) {
                        // System.err.println("Warning: stdDev value = " +
                        // stdDev
                        // + " -- rounded to zero.");
                        stdDev = 0;
                    }
                    stdDev = Math.sqrt(stdDev);
                }
            }
        }

        // /**
        // * Returns a string summarising the stats so far.
        // *
        // * @return the summary string
        // */
        // private String toString() {
        //
        // calculateDerived();
        // return
        // "Count " + Utils.doubleToString(count, 8) + '\n'
        // + "Min " + Utils.doubleToString(min, 8) + '\n'
        // + "Max " + Utils.doubleToString(max, 8) + '\n'
        // + "Sum " + Utils.doubleToString(sum, 8) + '\n'
        // + "SumSq " + Utils.doubleToString(sumSq, 8) + '\n'
        // + "Mean " + Utils.doubleToString(mean, 8) + '\n'
        // + "StdDev " + Utils.doubleToString(stdDev, 8) + '\n';
        // }

        // /**
        // * Tests the paired stats object from the command line.
        // * reads line from stdin, expecting two values per line.
        // *
        // * @param args ignored.
        // */
        // private static void main(String [] args) {
        //
        // try {
        // Stats ps = new Stats();
        // java.io.LineNumberReader r = new java.io.LineNumberReader(
        // new java.io.InputStreamReader(System.in));
        // String line;
        // while ((line = r.readLine()) != null) {
        // line = line.trim();
        // if (line.equals("") || line.startsWith("@") || line.startsWith("%"))
        // {
        // continue;
        // }
        // java.util.StringTokenizer s
        // = new java.util.StringTokenizer(line, " ,\t\n\r\f");
        // int count = 0;
        // double v1 = 0;
        // while (s.hasMoreTokens()) {
        // double val = (new Double(s.nextToken())).doubleValue();
        // if (count == 0) {
        // v1 = val;
        // } else {
        // System.err.println("MSG: Too many values in line \""
        // + line + "\", skipped.");
        // break;
        // }
        // count++;
        // }
        // if (count == 1) {
        // ps.add(v1);
        // }
        // }
        // ps.calculateDerived();
        // System.err.println(ps);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // System.err.println(ex.getMessage());
        // }
        // }

    } // Stats

    private class AttributeStats {

        /** The number of int-like values */
        private int intCount = 0;

        /** The number of real-like values (i.e. have a fractional part) */
        private int realCount = 0;

        /** The number of missing values */
        private int missingCount = 0;

        /** The number of distinct values */
        private int distinctCount = 0;

        /** The number of values that only appear once */
        private int uniqueCount = 0;

        /** The total number of values (i.e. number of instances) */
        private int totalCount = 0;

        /** Stats on numeric value distributions */
        // perhaps Stats should be moved from weka.experiment to weka.core
        Stats numericStats;

        /** Counts of each nominal value */
        private int[] nominalCounts;

        // /**
        // * Updates the counters for one more observed distinct value.
        // *
        // * @param value the value that has just been seen
        // * @param count the number of times the value appeared
        // */
        // protected void addDistinct(double value, int count) {
        //      
        // if (count > 0) {
        // if (count == 1) {
        // uniqueCount++;
        // }
        // if (MathUtils.eq(value, (double)((int)value))) {
        // intCount += count;
        // } else {
        // realCount += count;
        // }
        // if (nominalCounts != null) {
        // nominalCounts[(int)value] = count;
        // }
        // if (numericStats != null) {
        // numericStats.add(value, count);
        // numericStats.calculateDerived();
        // }
        // }
        // distinctCount++;
        // }

        // /**
        // * Returns a human readable representation of this AttributeStats
        // instance.
        // *
        // * @return a String represtinging these AttributeStats.
        // */
        // private String toString() {
        //
        // StringBuffer sb = new StringBuffer();
        // sb.append(Utils.padLeft("Type", 4)).append(Utils.padLeft("Nom", 5));
        // sb.append(Utils.padLeft("Int", 5)).append(Utils.padLeft("Real", 5));
        // sb.append(Utils.padLeft("Missing", 12));
        // sb.append(Utils.padLeft("Unique", 12));
        // sb.append(Utils.padLeft("Dist", 6));
        // if (nominalCounts != null) {
        // sb.append(' ');
        // for (int i = 0; i < nominalCounts.length; i++) {
        // sb.append(Utils.padLeft("C[" + i + "]", 5));
        // }
        // }
        // sb.append('\n');
        //
        // long percent;
        // percent = Math.round(100.0 * intCount / totalCount);
        // if (nominalCounts != null) {
        // sb.append(Utils.padLeft("Nom", 4)).append(' ');
        // sb.append(Utils.padLeft("" + percent, 3)).append("% ");
        // sb.append(Utils.padLeft("" + 0, 3)).append("% ");
        // } else {
        // sb.append(Utils.padLeft("Num", 4)).append(' ');
        // sb.append(Utils.padLeft("" + 0, 3)).append("% ");
        // sb.append(Utils.padLeft("" + percent, 3)).append("% ");
        // }
        // percent = Math.round(100.0 * realCount / totalCount);
        // sb.append(Utils.padLeft("" + percent, 3)).append("% ");
        // sb.append(Utils.padLeft("" + missingCount, 5)).append(" /");
        // percent = Math.round(100.0 * missingCount / totalCount);
        // sb.append(Utils.padLeft("" + percent, 3)).append("% ");
        // sb.append(Utils.padLeft("" + uniqueCount, 5)).append(" /");
        // percent = Math.round(100.0 * uniqueCount / totalCount);
        // sb.append(Utils.padLeft("" + percent, 3)).append("% ");
        // sb.append(Utils.padLeft("" + distinctCount, 5)).append(' ');
        // if (nominalCounts != null) {
        // for (int i = 0; i < nominalCounts.length; i++) {
        // sb.append(Utils.padLeft("" + nominalCounts[i], 5));
        // }
        // }
        // sb.append('\n');
        // return sb.toString();
        // }
    }

    /**
     * Inner class handling node operations for Cobweb.
     * 
     * @see Serializable
     */
    private class CNode {
        /**
         * Cluster number of this node
         */
        private int m_clusterNum = -1;

        /**
         * Within cluster attribute statistics
         */
        private AttributeStats[] m_attStats;

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

        // /**
        // * Cluster number of this node
        // */
        // private int m_clusterNum = -1;

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
                m_clusterInstances = new SimpleDataset();// (leafInstance.dataset(),
                // 1);
            }
            m_clusterInstances.addInstance(leafInstance);
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
                m_clusterInstances = new SimpleDataset();// (newInstance.dataset(),
                // 1);
                m_clusterInstances.addInstance(newInstance);
                updateStats(newInstance, false);

            } else if (m_children == null) {
                /*
                 * we are a leaf, so make our existing instance(s) into a child
                 * and then add the new instance as a child
                 */
                m_children = new Vector<CNode>();
                CNode tempSubCluster = new CNode(m_numAttributes, m_clusterInstances.getInstance(0));

                // System.out.println("Dumping
                // "+m_clusterInstances.numInstances());
                for (int i = 1; i < m_clusterInstances.size(); i++) {
                    tempSubCluster.m_clusterInstances.addInstance(m_clusterInstances.getInstance(i));
                    tempSubCluster.updateStats(m_clusterInstances.getInstance(i), false);
                }
                m_children = new Vector<CNode>();
                m_children.addElement(tempSubCluster);
                m_children.addElement(new CNode(m_numAttributes, newInstance));

                m_clusterInstances.addInstance(newInstance);
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
            merged.m_clusterInstances = new SimpleDataset();// (m_clusterInstances,
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
                m_clusterInstances.addInstance(newInstance);
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
                    m_clusterInstances.addInstance(newInstance);
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
                Instance temp = child.m_clusterInstances.getInstance(i);
                m_clusterInstances.addInstance(temp);
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

            // if (m_children == null) {
            // throw new Exception("categoryUtility: No children!");
            // }

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
                // if (m_clusterInstances.attribute(i).isNominal()) {
                // for (int j = 0; j <
                // m_clusterInstances.attribute(i).numValues(); j++) {
                // double x = child.getProbability(i, j);
                // double y = getProbability(i, j);
                // sum += (x * x) - (y * y);
                // }
                // } else {
                // numeric attribute
                sum += ((m_normal / child.getStandardDev(i)) - (m_normal / getStandardDev(i)));

                // }
            }
            return (child.m_totalInstances / m_totalInstances) * sum;
        }

        // /**
        // * Returns the probability of a value of a nominal attribute in this
        // * node
        // *
        // * @param attIndex
        // * the index of the attribute
        // * @param valueIndex
        // * the index of the value of the attribute
        // * @return the probability
        // * @throws Exception
        // * if the requested attribute is not nominal
        // */
        // private double getProbability(int attIndex, int valueIndex) {
        //
        // // if (!m_clusterInstances.attribute(attIndex).isNominal()) {
        // // throw new Exception("getProbability: attribute is not nominal");
        // // }
        //
        // if (m_attStats[attIndex].totalCount <= 0) {
        // return 0;
        // }
        //
        // return (double) m_attStats[attIndex].nominalCounts[valueIndex] /
        // (double) m_attStats[attIndex].totalCount;
        // }

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
            // if (!m_clusterInstances.attribute(attIndex).isNumeric()) {
            // throw new Exception("getStandardDev: attribute is not numeric");
            // }

            m_attStats[attIndex].numericStats.calculateDerived();
            double stdDev = m_attStats[attIndex].numericStats.stdDev;
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
                m_attStats = new AttributeStats[m_numAttributes];
                for (int i = 0; i < m_numAttributes; i++) {
                    m_attStats[i] = new AttributeStats();
                    // if (m_clusterInstances.attribute(i).isNominal()) {
                    // m_attStats[i].nominalCounts = new
                    // int[m_clusterInstances.attribute(i).numValues()];
                    // } else {
                    m_attStats[i].numericStats = new Stats();
                    // }
                }
            }
            for (int i = 0; i < m_numAttributes; i++) {
                // if (!updateInstance.isMissing(i)) {
                double value = updateInstance.getValue(i);
                // if (m_clusterInstances.attribute(i).isNominal()) {
                // m_attStats[i].nominalCounts[(int) value] += (delete) ? (-1.0
                // * updateInstance.weight())
                // : updateInstance.weight();
                // m_attStats[i].totalCount += (delete) ? (-1.0 *
                // updateInstance.weight()) : updateInstance
                // .weight();
                // } else {
                if (delete) {
                    m_attStats[i].numericStats.subtract(value, updateInstance.getWeight());
                } else {
                    m_attStats[i].numericStats.add(value, updateInstance.getWeight());
                }
                // }
                // }
            }
            m_totalInstances += (delete) ? (-1.0 * updateInstance.getWeight()) : (updateInstance.getWeight());
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

            m_clusterNum = cl_num[0];
            cl_num[0]++;
            if (m_children != null) {
                for (int i = 0; i < m_children.size(); i++) {
                    CNode child = m_children.elementAt(i);
                    child.assignClusterNums(cl_num);
                }
            }
        }

        // /**
        // * Recursively build a string representation of the Cobweb tree
        // *
        // * @param depth
        // * depth of this node in the tree
        // * @param text
        // * holds the string representation
        // */
        // private void dumpTree(int depth, StringBuffer text) {
        //
        // if (depth == 0)
        // determineNumberOfClusters();
        //
        // if (m_children == null) {
        // text.append("\n");
        // for (int j = 0; j < depth; j++) {
        // text.append("| ");
        // }
        // text.append("leaf " + m_clusterNum + " [" + m_clusterInstances.size()
        // + "]");
        // } else {
        // for (int i = 0; i < m_children.size(); i++) {
        // text.append("\n");
        // for (int j = 0; j < depth; j++) {
        // text.append("| ");
        // }
        // text.append("node " + m_clusterNum + " [" + m_clusterInstances.size()
        // + "]");
        // ((CNode) m_children.elementAt(i)).dumpTree(depth + 1, text);
        // }
        // }
        // }

        // /**
        // * Returns the instances at this node as a string. Appends the cluster
        // * number of the child that each instance belongs to.
        // *
        // * @return a <code>String</code> value
        // * @throws Exception
        // * if an error occurs
        // */
        // private String dumpData() throws Exception {
        // if (m_children == null) {
        // return m_clusterInstances.toString();
        // }
        //
        // // construct instances string with cluster numbers attached
        // CNode tempNode = new CNode(m_numAttributes);
        // tempNode.m_clusterInstances = new Instances(m_clusterInstances, 1);
        // for (int i = 0; i < m_children.size(); i++) {
        // tempNode.addChildNode((CNode) m_children.elementAt(i));
        // }
        // Instances tempInst = tempNode.m_clusterInstances;
        // tempNode = null;
        //
        // Add af = new Add();
        // af.setAttributeName("Cluster");
        // String labels = "";
        // for (int i = 0; i < m_children.size(); i++) {
        // CNode temp = (CNode) m_children.elementAt(i);
        // labels += ("C" + temp.m_clusterNum);
        // if (i < m_children.size() - 1) {
        // labels += ",";
        // }
        // }
        // af.setNominalLabels(labels);
        // af.setInputFormat(tempInst);
        // tempInst = Filter.useFilter(tempInst, af);
        // tempInst.setRelationName("Cluster " + m_clusterNum);
        //
        // int z = 0;
        // for (int i = 0; i < m_children.size(); i++) {
        // CNode temp = (CNode) m_children.elementAt(i);
        // for (int j = 0; j < temp.m_clusterInstances.numInstances(); j++) {
        // tempInst.instance(z).setValue(m_numAttributes, (double) i);
        // z++;
        // }
        // }
        // return tempInst.toString();
        // }

        // /**
        // * Recursively generate the graph string for the Cobweb tree.
        // *
        // * @param text
        // * holds the graph string
        // * @throws Exception
        // * if generation fails
        // */
        // private void graphTree(StringBuffer text) throws Exception {
        //
        // text.append("N" + m_clusterNum + " [label=\"" + ((m_children == null)
        // ? "leaf " : "node ") + m_clusterNum
        // + " " + " (" + m_clusterInstances.numInstances() + ")\" "
        // + ((m_children == null) ? "shape=box style=filled " : "")
        // + (m_saveInstances ? "data =\n" + dumpData() + "\n,\n" : "") +
        // "]\n");
        // if (m_children != null) {
        // for (int i = 0; i < m_children.size(); i++) {
        // CNode temp = (CNode) m_children.elementAt(i);
        // text.append("N" + m_clusterNum + "->" + "N" + temp.m_clusterNum +
        // "\n");
        // }
        //
        // for (int i = 0; i < m_children.size(); i++) {
        // CNode temp = (CNode) m_children.elementAt(i);
        // temp.graphTree(text);
        // }
        // }
        // }
    }

    /**
     * Normal constant.
     */
    private static final double m_normal = 1.0 / (2 * Math.sqrt(Math.PI));

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
            m_cobwebTree = new CNode(newInstance.size(), newInstance);
        } else {
            m_cobwebTree.addInstance(newInstance);
        }
    }

    private void printNode(CNode x, int level) {
        String tabs = "";
        for (int i = 0; i < level; i++)
            tabs += "\t";
        System.out.println(tabs + "" + x.m_clusterInstances.size());
        if (x.m_children != null) {
            for (CNode y : x.m_children) {
                printNode(y, level + 1);
            }
        }
    }

    private Filter filter = new NormalizeMean();

    public Dataset[] executeClustering(Dataset data) {

        data = filter.filterDataset(data);
        System.out.println(data);
        m_numberOfClusters = -1;
        m_cobwebTree = null;
        m_numberSplits = 0;
        m_numberMerges = 0;
        for (int i = 0; i < data.size(); i++) {
            updateClusterer(data.getInstance(i));
        }
        determineNumberOfClusters();
        System.out.println("Datapoints: " + data.size());
        System.out.println("Clusters: " + m_numberOfClusters);
        System.out.println("Size at root: " + m_cobwebTree.m_clusterInstances.size());
        printNode(m_cobwebTree, 0);
        // StringBuffer tmp=new StringBuffer();
        // m_cobwebTree.dumpTree(0, tmp);
        // System.out.println(tmp.toString());
        // init "m_numberOfClusters"
        // numberOfClusters();
        // TODO return clusters;

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
            Dataset tmp = new SimpleDataset();
            Dataset fromTree = tree.m_clusterInstances;
            for (int i = 0; i < fromTree.size(); i++) {
                tmp.addInstance(filter.unfilterInstance(fromTree.getInstance(i)));
            }
            clusters.add(tmp);
        }

    }
}
