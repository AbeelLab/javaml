/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    KDTree.java
 *    Copyright (C) 2000 University of Waikato
 *
 */

package net.sf.javaml.core;

import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Class implementing the KDTree search algorithm for nearest neighbour search.<br/>
 * The connection to dataset is only a reference. For the tree structure the
 * indexes are stored in an array. <br/>
 * 
 * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
 * @author Malcolm Ware (mfw4@cs.waikato.ac.nz)
 * @author Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
 * @author Thomas Abeel
 */
public class KDTree {

    /**
     * Ranges of the whole KDTree. lowest and highest value and width (= high -
     * low) for each dimension
     */
    private double[][] m_Universe;

    /** root node */
    private KDTreeNode m_Root = null;

    /**
     * Indexlist of the instances of this kdtree. Instances get sorted according
     * to the splits. the nodes of the KDTree just hold their start and end
     * indices
     */
    private int[] m_InstList;

    /** The euclidean distance function to use */
    private DistanceMeasure dm = new EuclideanDistance();

    /** minimal relative width of a KDTree rectangle */
    double m_MinBoxRelWidth = 1.0E-2;

    /** maximal number of instances in a leaf */
    int m_MaxInstInLeaf = 40;

    /**
     * Index in range array of attributes for MIN and MAX and WIDTH (range) of
     * an attribute.
     */
    public static int R_MIN = 0;

    public static int R_MAX = 1;

    public static int R_WIDTH = 2;

    /**
     * Default Constructor
     */
    public KDTree() {
    }

    Dataset data;

    /**
     * Constructor, copies all options from an existing KDTree.
     * 
     * @param tree
     *            the KDTree to copy from
     */
    public KDTree(KDTree tree) {
        m_Universe = tree.m_Universe;
        data = tree.data;
        dm = tree.dm; // added
        m_MinBoxRelWidth = tree.m_MinBoxRelWidth;
        m_MaxInstInLeaf = tree.m_MaxInstInLeaf;
    }

    /**
     * sets the instances and builds the KDTree
     * 
     * @param instances
     *            the instances to build the tree from
     * @throws Exception
     *             if something goes wrong
     */
    public void setInstances(Dataset instances) throws Exception {
        buildKDTree(instances);
    }

    /**
     * Builds the KDTree. It is adviseable to run the replace missing attributes
     * filter on the passed instances first.
     * 
     * @param instances
     *            instances to build the tree of
     * @throws Exception
     *             if something goes wrong
     */
    private void buildKDTree(Dataset instances) throws Exception {
        data = instances;
        int numInst = data.size();

        // Make the global index list
        m_InstList = new int[numInst];

        for (int i = 0; i < numInst; i++) {
            m_InstList[i] = i;
        }

        // make the tree starting with the roor node
        m_Root = new KDTreeNode();

        // set global ranges
        m_Universe = DatasetTools.getRanges(data, dm);

        // build the tree
        int[] num = new int[1];
        num[0] = 0;
        m_Root.makeKDTreeNode(num, m_Universe, // ranges,
                0, // index of first instance index
                numInst - 1); // index of last instance index
    }

    /**
     * Adds one instance to the KDTree. This updates the KDTree structure to
     * take into account the newly added training instance.
     * 
     * @param instance
     *            the instance to be added. Usually the newly added instance in
     *            the training set.
     * @throws Exception
     *             if something goes wrong or instances are null
     */
    public void update(Instance instance) throws Exception { // better to
        // change
        // to addInstance
        if (data == null)
            throw new Exception("No instances supplied yet. Have to call "
                    + "setInstances(instances) with a set of Instances " + "first.");

        boolean success = m_Root.addInstance(instance);
        if (!success) {
            // make a new tree
            buildKDTree(data);
        }
    }

    /**
     * Adds one instance to KDTree loosly. It only changes the ranges in
     * EuclideanDistance, and does not affect the structure of the KDTree.
     * 
     * @param instance
     *            the new instance. Usually this is the test instance supplied
     *            to update the range of attributes in the distance function.
     */
    public void addInstanceInfo(Instance instance) {
        updateRanges(instance);
    }

    private void updateRanges(Instance instance) {
        // TODO Auto-generated method stub

    }

    /**
     * string representing the tree
     * 
     * @return string representing the tree
     */
    public String toString() {
        StringBuffer text = new StringBuffer();
        KDTreeNode tree = m_Root;
        if (m_Root == null) {
            text.append("KDTree not built yet.");
            return text.toString();
        }
        int[] num = new int[1];
        num[0] = 0;

        text.append("\nKDTree build:");
        text.append(tree.statToString(true, true));
        // tree in string format:
        text.append(tree.nodeToString(true));
        return text.toString();
    }

    /**
     * Assigns instances to centers using KDTree.
     * 
     * @param centers
     *            the current centers
     * @param assignments
     *            the centerindex for each instance
     * @param pc
     *            the threshold value for pruning.
     * @throws Exception
     *             if something goes wrong
     */
    public void centerInstances(Instance[] centers, int[] assignments, double pc) throws Exception {

        int[] centList = new int[centers.length];
        for (int i = 0; i < centers.length; i++)
            centList[i] = i;

        m_Root.determineAssignments(centers, centList, assignments, pc);
    }

    /**
     * Returns array of boolean set true or false if instance is part of next
     * left kdtree.
     * 
     * @param left
     *            list of boolean values, true if instance belongs to left
     * @param startIdx
     * @param endIdx
     * @param splitDim
     *            index of splitting attribute
     * @param splitValue
     *            value at which the node is split
     * @return number of instances that belong to the left
     */
    private int checkSplitInstances(boolean[] left, int startIdx, int endIdx, int splitDim, double splitValue) {

        // length of left should be same as length of instList
        int numLeft = 0;
        for (int i = startIdx, j = 0; i <= endIdx; i++, j++) {
            // value <= splitValue
            if (valueIsSmallerEqual(data.getInstance(m_InstList[i]), splitDim, splitValue)) {
                left[j] = true;
                numLeft++;
            } else {
                left[j] = false;
            }
        }
        return numLeft;
    }

    // WEKA euclidean distance method
    private boolean valueIsSmallerEqual(Instance instance, int splitDim, double splitValue) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Sorts instances newly into left and right part.
     * 
     * @param left
     *            list of flags, set true by this method if instance should go
     *            to the left follow node
     * @param startIdx
     * @param endIdx
     * @param startLeft
     */
    private void splitInstances(boolean[] left, int startIdx, int endIdx, int startLeft) {
        int tmp;
        // shuffling indices in the left node to the left of the array and those
        // in
        // right node to the right side of the array (see makeKDTreeNode() for
        // referred startLeft, numLeft and startRight variables).
        // After for loop starting from startLeft, numLeft indices will be on
        // left
        // and the rest will be on right starting from startRight
        for (int i = startIdx, j = 0; i <= endIdx; i++, j++) {
            if (left[j]) {
                tmp = m_InstList[startLeft];
                m_InstList[startLeft++] = m_InstList[i]; // instList[i];
                m_InstList[i] = tmp;
            }
        }
    }

    /**
     * -------------------------------------------------------------------------------- *
     * variables for nearest neighbour search
     * --------------------------------------------------------------------------------
     */

    /** index/indices of current target */
    private int[] m_NearestList;

    /** length of nearest list (can be larger than k) */
    private int m_NearestListLength = 0;

    /** true if more than of k nearest neighbours */
    private boolean m_MultipleFurthest = false;

    /** number of nearest neighbours k */
    private int m_kNN = 0;

    /** distance to current furthest of the neighbours */
    private double m_MaxMinDist = Double.MAX_VALUE;

    /** index of the furthest of the neighbours in m_NearestList */
    private int m_FurthestNear = 0;

    /** distance to current nearest neighbour */
    private double[] m_DistanceList;

    /**
     * Returns the distances to the kNearest or 1 nearest neighbour currently
     * found with either the kNearestNeighbours or the nearestNeighbour method.
     * 
     * @return distances[] array containing the distances of the
     *         nearestNeighbours. The length and ordering of the array is the
     *         same as that of the instances returned by nearestNeighbour
     *         functions.
     * @throws Exception
     *             Throws an exception if called before calling
     *             kNearestNeighbours or nearestNeighbours.
     */
    public double[] getDistances() throws Exception {
        if (data == null || m_DistanceList == null)
            throw new Exception("The tree has not been supplied with a set of "
                    + "instances or getDistances() has been called " + "before calling kNearestNeighbours().");
        return m_DistanceList;
    }

    /** debug pls remove after use. */
    private boolean print = false;

    /**
     * Returns the k nearest neighbours to the supplied instance.
     * 
     * @param target
     *            The instance to find the nearest neighbours for.
     * @param k
     *            The number of neighbours to find.
     * @return the neighbors
     * @throws Exception
     *             Throws an exception if the nearest neighbour could not be
     *             found.
     */
    public Instance[] kNearestNeighbours(Instance target, int k) throws Exception {
        if (data == null)
            throw new Exception("No instances supplied yet. Have to call "
                    + "setInstances(instances) with a set of Instances " + "first.");

        m_kNN = k;
       
        m_NearestList = new int[data.size()];
        m_DistanceList = new double[data.size()];
        m_NearestListLength = 0;
        for (int i = 0; i < m_DistanceList.length; i++) {
            m_DistanceList[i] = Double.MAX_VALUE;
        }
        //double maxDist = m_Root.kNearestNeighbour(target);
        m_Root.kNearestNeighbour(target);
        combSort11(m_DistanceList, m_NearestList);
        postProcessDistances(m_DistanceList);

        Instance[] nearest = new SimpleInstance[m_NearestListLength];
        double[] newDistanceList = new double[m_NearestListLength];
        for (int i = 0; i < m_NearestListLength; i++) {
            nearest[i] = data.getInstance(m_NearestList[i]);
            newDistanceList[i] = m_DistanceList[i];
        }

        m_DistanceList = newDistanceList;
        return nearest;
    }

    // /WEKA Euclidean distance method
    private void postProcessDistances(double[] distanceList) {
        // TODO Auto-generated method stub

    }

    public static void combSort11(double arrayToSort[], int linkedArray[]) {
        int switches, j, top, gap;
        double hold1;
        int hold2;
        gap = arrayToSort.length;
        do {
            gap = (int) (gap / 1.3);
            switch (gap) {
            case 0:
                gap = 1;
                break;
            case 9:
            case 10:
                gap = 11;
                break;
            default:
                break;
            }
            switches = 0;
            top = arrayToSort.length - gap;
            for (int i = 0; i < top; i++) {
                j = i + gap;
                if (arrayToSort[i] > arrayToSort[j]) {
                    hold1 = arrayToSort[i];
                    hold2 = linkedArray[i];
                    arrayToSort[i] = arrayToSort[j];
                    linkedArray[i] = linkedArray[j];
                    arrayToSort[j] = hold1;
                    linkedArray[j] = hold2;
                    switches++;
                }// endif
            }// endfor
        } while (switches > 0 || gap > 1);
    }

    /**
     * Returns the nearest neighbour to the supplied instance.
     * 
     * @param target
     *            The instance to find the nearest neighbour for.
     * @return the nearest neighbor
     * @throws Exception
     *             Throws an exception if the neighbours could not be found.
     */
    public Instance nearestNeighbour(Instance target) throws Exception {
        return (kNearestNeighbours(target, 1))[0];
    }

//    /**
//     * Find k nearest neighbours to target. This is the main method.
//     * 
//     * @param target
//     *            the instance to find nearest neighbour for
//     * @param kNN
//     *            the number of neighbors to find
//     * @param nearestList
//     * @param distanceList
//     * @return
//     * @throws Exception
//     *             if something goes wrong
//     */
//    // redundant no longer needed
//    public int findKNearestNeighbour(Instance target, int kNN, int[] nearestList, double[] distanceList)
//            throws Exception {
//        m_kNN = kNN;
//        m_NearestList = nearestList;
//        m_DistanceList = distanceList;
//        m_NearestListLength = 0;
//        for (int i = 0; i < distanceList.length; i++) {
//            distanceList[i] = Double.MAX_VALUE;
//        }
//        int[] num = new int[1];
//        num[0] = 0;
//        //double minDist = m_Root.kNearestNeighbour(target);
//        return m_NearestListLength;
//    }

    /**
     * Get the distance of the furthest of the nearest neighbour returns the
     * index of this instance in the index list.
     * 
     * @return the index of the instance
     */
    private int checkFurthestNear() {
        double max = 0.0;
        int furthestNear = 0;
        for (int i = 0; i < m_kNN; i++) {
            if (m_DistanceList[i] > max) {
                max = m_DistanceList[i];
                furthestNear = i;
            }
        }
        return furthestNear;
    }

    /***************************************************************************
     * 
     * A class for storing a KDTree node.
     * 
     **************************************************************************/
    private class KDTreeNode {

        /** node number (only for debug) */
        private int m_NodeNumber;

        /**
         * left subtree; contains instances with smaller or equal to split
         * value.
         */
        private KDTreeNode m_Left = null;

        /** right subtree; contains instances with larger than split value. */
        private KDTreeNode m_Right = null;

        /** value to split on. */
        private double m_SplitValue;

        /** attribute to split on. */
        private int m_SplitDim;

        /**
         * Every subtree stores the beginning index and the end index of the
         * range in the main instancelist, that contains its own instances
         */
        private int m_Start = 0;

        private int m_End = 0;

        /**
         * lowest and highest value and width (= high - low) for each dimension
         */
        private double[][] m_NodeRanges;

        /**
         * Gets the splitting dimension.
         * 
         * @return splitting dimension
         */
        public int getSplitDim() {
            return m_SplitDim;
        }

        /**
         * Gets the splitting value.
         * 
         * @return splitting value
         */
        public double getSplitValue() {
            return m_SplitValue;
        }

        /**
         * Checks if node is a leaf.
         * 
         * @return true if it is a leaf
         */
        public boolean isALeaf() {
            return (m_Left == null);
        }

        /**
         * Makes a KDTreeNode. Use this, if ranges are already defined.
         * 
         * @param num
         *            number of the current node
         * @param ranges
         *            the ranges
         * @param start
         *            start index of the instances
         * @param end
         *            index of the instances
         * @throws Exception
         *             if instance couldn't be retrieved
         */
        private void makeKDTreeNode(int[] num, double[][] ranges, int start, int end) throws Exception {
            m_NodeRanges = ranges;
            makeKDTreeNode(num, start, end);
        }

        /** flag for normalizing */
        boolean m_NormalizeNodeWidth = false;

        /**
         * Makes a KDTreeNode.
         * 
         * @param num
         *            the node number
         * @param start
         *            the start index of the instances in the index list
         * @param end
         *            the end index of the instances in the index list
         * @throws Exception
         *             if instance couldn't be retrieved
         */
        void makeKDTreeNode(int[] num, int start, int end) throws Exception {

            num[0]++;
            m_NodeNumber = num[0];
            m_Start = start;
            m_End = end;
            m_Left = null;
            m_Right = null;
            m_SplitDim = -1;
            m_SplitValue = -1;

            double relWidth = 0.0;
            boolean makeALeaf = false;
            int numInst = end - start + 1;

            // if number of instances is under a maximum, then the node is a
            // leaf
            if (numInst <= m_MaxInstInLeaf) {
                makeALeaf = true;
            }

            // set ranges and split parameter
            if (m_NodeRanges == null)
                m_NodeRanges = initializeRanges(m_InstList, start, end);

            // set outer ranges
            if (m_Universe == null) {
                m_Universe = m_NodeRanges;
            }

            m_SplitDim = widestDim(m_NormalizeNodeWidth);
            if (m_SplitDim >= 0) {
                m_SplitValue = splitValue(m_SplitDim);
                // set relative width
                relWidth = m_NodeRanges[m_SplitDim][R_WIDTH] / m_Universe[m_SplitDim][R_WIDTH];
            }

            // check if thin enough to make a leaf
            if (relWidth <= m_MinBoxRelWidth) {
                makeALeaf = true;
            }

            // split instance list into two
            // first define which one have to go left and right..
            int numLeft = 0;
            boolean[] left = new boolean[numInst];
            if (!makeALeaf) {
                numLeft = checkSplitInstances(left, start, end, m_SplitDim, m_SplitValue);

                // if one of the sides would be empty, make a leaf
                // which means, do nothing
                if ((numLeft == 0) || (numLeft == numInst)) {
                    makeALeaf = true;
                }
            }

            if (makeALeaf) {
                // TODO I think we don't need any of the following:
                // sum =
                // sum is a row vector that has added up all rows
                // summags =
                // is one double that contains the sum of the scalar product
                // of all row vectors with themselves
            } else {
                // and now really make two lists
                int startLeft = start;
                int startRight = start + numLeft;
                splitInstances(left, start, end, startLeft);

                // make left subKDTree
                int endLeft = startLeft + numLeft - 1;
                m_Left = new KDTreeNode();
                m_Left.makeKDTreeNode(num, startLeft, endLeft);

                // make right subKDTree
                int endRight = end;
                m_Right = new KDTreeNode();
                m_Right.makeKDTreeNode(num, startRight, endRight);
            }
        }

        // WEKA Euclidean distance method
        private double[][] initializeRanges(int[] instList, int start, int end) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * Returns the widest dimension.
         * 
         * @param normalize
         *            if true normalization is used
         * @param classIdx
         *            the index of the class attribute
         * @return attribute index that has widest range
         */
        private int widestDim(boolean normalize) {

            double widest = 0.0;
            int w = -1;
            if (normalize) {
                for (int i = 0; i < m_NodeRanges.length; i++) {
                    double newWidest = m_NodeRanges[i][R_WIDTH] / m_Universe[i][R_WIDTH];
                    if (newWidest > widest) {

                        widest = newWidest;
                        w = i;
                    }
                }
            } else {
                for (int i = 0; i < m_NodeRanges.length; i++) {
                    if (m_NodeRanges[i][R_WIDTH] > widest) {

                        widest = m_NodeRanges[i][R_WIDTH];
                        w = i;
                    }
                }
            }
            return w;
        }

        /**
         * Returns the split value of a given dimension.
         * 
         * @param dim
         *            dimension where split happens
         * @return the split value
         */
        private double splitValue(int dim) {

            double split = getMiddle(m_NodeRanges[dim]);
            return split;
        }

        // WEKA euclidean distance function
        private double getMiddle(double[] ds) {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * Add an instance to the node or subnode. Returns false if adding
         * cannot be done. Looks for the subnode the instance actually belongs
         * to. Corrects the end boundary of the instance list by coming up
         * 
         * @param instance
         *            the instance to add
         * @return true if adding was done
         * @throws Exception
         *             if something goes wrong
         */
        public boolean addInstance(Instance instance) throws Exception {

            boolean success = false;
            if (!isALeaf()) {
                // go further down the tree to look for the leaf the instance
                // should
                // be in
                double instanceValue = instance.getValue(m_SplitDim);
                boolean instanceInLeft = instanceValue <= m_SplitValue;
                if (instanceInLeft) {
                    success = m_Left.addInstance(instance);
                    if (success) {
                        // go into right branch to correct instance list
                        // boundaries
                        m_Right.afterAddInstance();
                    }
                } else {
                    success = m_Right.addInstance(instance);
                }

                // instance was included
                if (success) {
                    // correct end index of instance list of this node
                    m_End++;
                    // correct ranges
                    m_NodeRanges = updateRanges(instance, m_NodeRanges);
                }

            } else { // found the leaf to insert instance

                // ranges have been updated

                m_NodeRanges = updateRanges(instance, m_NodeRanges);

                int index = data.size() - 1;

                int InstList[] = new int[data.size()];
                System.arraycopy(m_InstList, 0, InstList, 0, m_End + 1); // m_InstList.squeezeIn(m_End,
                // index);
                if (m_End < m_InstList.length - 1)
                    System.arraycopy(m_InstList, m_End + 1, InstList, 0, m_InstList.length);
                m_InstList[m_End] = index;

                m_End++;

                int numInst = m_End - m_Start + 1;

                // leaf did get too big?
                if (numInst > m_MaxInstInLeaf) {
                    // split leaf
                    int[] num = new int[1];
                    num[0] = m_NodeNumber;
                    this.makeKDTreeNode(num, m_NodeRanges, m_Start, m_End);
                }
                success = true;
            }
            return success;
        }

        // WEKA euclidean distance function
        private double[][] updateRanges(Instance instance, double[][] nodeRanges) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * Corrects the boundaries of all nodes to the right of the leaf where
         * the instance was added to.
         */
        public void afterAddInstance() {

            m_Start++;
            m_End++;
            if (!isALeaf()) {
                m_Left.afterAddInstance();
                m_Right.afterAddInstance();
            }
        }

        /**
         * Returns statistics about the KDTree.
         * 
         * @param nodes
         *            give number of nodes
         * @param leaves
         *            give number of leaves
         * @return a text string that contains the statistics to the KDTree
         */
        public String statToString(boolean nodes, boolean leaves) {

            int count = 1;
            int stats[] = new int[2];
            if (this.m_Left != null)
                count = this.m_Left.collectStats(count, stats);
            if (this.m_Right != null)
                count = this.m_Right.collectStats(count, stats);

            StringBuffer text = new StringBuffer();
            if (nodes)
                text.append("\n  Number of nodes in the tree " + count + " \n");
            if (leaves)
                text.append("  Number of leaves in the tree " + stats[0] + " \n");
            return text.toString();
        }

        /**
         * Returns statistics about the KDTree.
         * 
         * @param count
         *            number of nodes so far
         * @param stats
         *            array with stats info
         * @return the number of nodes
         */
        public int collectStats(int count, int[] stats) {

            count++;
            if (this.m_Left != null)
                count = this.m_Left.collectStats(count, stats);
            if (this.m_Right != null)
                count = this.m_Right.collectStats(count, stats);
            else
                // is a leaf
                stats[0]++;
            return count;
        }

        /**
         * Returns the KDTree node and its underlying branches as string.
         * 
         * @param leaves
         *            adds the instances of the leaves
         * @return a string representing the node
         */
        public String nodeToString(boolean leaves) {
            StringBuffer text = new StringBuffer();
            text.append("NODE-Nr:          " + m_NodeNumber + "\n");
            int len = m_End - m_Start + 1;
            text.append("Num of instances: " + len + "\n");
            text.append("start " + m_Start + " == end " + m_End + "\n");
            if (!isALeaf()) {
                text.append("attribute: " + this.m_SplitDim);
                text.append("split at: " + this.m_SplitValue + "\n");
            } else {
                text.append("is a leaf\n");
                if (leaves) {
                    for (int i = m_Start; i <= m_End; i++) {
                        int instIndex = m_InstList[i];
                        text.append(instIndex + ": ");
                        text.append(data.getInstance(instIndex).toString() + "\n");
                    }
                }
            }
            text.append("------------------\n");
            if (this.m_Left != null)
                text.append(this.m_Left.nodeToString(leaves));
            if (this.m_Right != null)
                text.append(this.m_Right.nodeToString(leaves));
            return text.toString();
        }

        /**
         * Assigns instances to the current centers called candidates.
         * 
         * @param centers
         *            all the current centers
         * @param candidates
         *            the current centers the method works on
         * @param assignments
         *            the center index for each instance
         * @param pc
         *            the threshold value for pruning
         * @throws Exception
         *             if something goes wrong
         */
        private void determineAssignments(Instance[] centers, int[] candidates, int[] assignments, double pc)
                throws Exception {

            // reduce number of owners for current hyper rectangle
            int[] owners = refineOwners(centers, candidates);

            // only one owner
            if (owners.length == 1) {
                // all instances of this node are owned by one center
                for (int i = m_Start; i <= m_End; i++) {
                    assignments[m_InstList[i]] // the assignment of this
                                                // instance
                    = owners[0]; // is the current owner
                }
            } else if (!this.isALeaf()) {
                // more than one owner and it is not a leaf
                m_Left.determineAssignments(centers, owners, assignments, pc);
                m_Right.determineAssignments(centers, owners, assignments, pc);
            } else {
                // this is a leaf and there are more than 1 owner
                // XMeans.
                assignSubToCenters(m_NodeRanges, centers, owners, assignments);
            }
        }

        /**
         * Refines the ownerlist.
         * 
         * @param centers
         *            all centers
         * @param candidates
         *            the indexes of those centers that are candidates
         * @return list of owners
         * @throws Exception
         *             if something goes wrong
         */
        private int[] refineOwners(Instance[] centers, int[] candidates) throws Exception {

            int[] owners = new int[candidates.length];
            double minDistance = Double.MAX_VALUE;
            int ownerIndex = -1;
            Instance owner;
            int numCand = candidates.length;
            double[] distance = new double[numCand];
            boolean[] inside = new boolean[numCand];
            for (int i = 0; i < numCand; i++) {
                distance[i] = distanceToHrect(centers[candidates[i]]);
                inside[i] = (distance[i] == 0.0);
                if (distance[i] < minDistance) {
                    minDistance = distance[i];
                    ownerIndex = i;
                }
            }
            owner = new SimpleInstance(centers[candidates[ownerIndex]]);

            // are there other owners
            // loop also goes over already found owner, keeps order
            // in owner list
            int index = 0;
            for (int i = 0; i < numCand; i++) {
                // 1. all centers that are points within rectangle are owners
                if ((inside[i])

                // 2. take all points with same distance to the rect. as the
                // owner
                        || (distance[i] == distance[ownerIndex])) {

                    // add competitor to owners list
                    owners[index++] = candidates[i];
                } else {

                    Instance competitor = new SimpleInstance(centers[candidates[i]]);
                    if

                    // 3. point has larger distance to rectangle but still can
                    // compete
                    // with owner for some points in the rectangle
                    (!candidateIsFullOwner(owner, competitor))

                    {
                        // also add competitor to owners list
                        owners[index++] = candidates[i];
                    }
                }
            }
            int[] result = new int[index];
            for (int i = 0; i < index; i++)
                result[i] = owners[i];
            return result;
        }

        /**
         * Returns true if candidate is a full owner in respect to a competitor.
         * <p>
         * 
         * The candidate has been the closer point to the current rectangle or
         * even has been a point within the rectangle. The competitor is
         * competing with the candidate for a few points out of the rectangle
         * although it is a point further away from the rectangle then the
         * candidate. The extrem point is the corner of the rectangle that is
         * furthest away from the candidate towards the direction of the
         * competitor.
         * 
         * If the distance candidate to this extreme point is smaller then the
         * distance competitor to this extreme point, then it is proven that
         * none of the points in the rectangle can be owned be the competitor
         * and the candidate is full owner of the rectangle in respect to this
         * competitor. See also D. Pelleg and A. Moore's paper 'Accelerating
         * exact k-means Algorithms with Geometric Reasoning'.
         * <p>
         * 
         * @param candidate
         *            instance that is candidate to be owner
         * @param competitor
         *            instance that competes against the candidate
         * @return true if candidate is full owner
         * @throws Exception
         *             if something goes wrong
         */
        private boolean candidateIsFullOwner(Instance candidate, Instance competitor) throws Exception {

            // get extreme point

            float[] extremeValues = new float[candidate.size()];
            for (int i = 0; i < candidate.size(); i++) {
                if ((competitor.getValue(i) - candidate.getValue(i)) > 0) {
                    extremeValues[i] = (float) m_NodeRanges[i][R_MAX];
                } else {
                    extremeValues[i] = (float) m_NodeRanges[i][R_MIN];
                }
            }
            Instance extreme = new SimpleInstance(extremeValues, candidate.getWeight(), candidate.isClassSet(),
                    candidate.getClassValue());
            ;
            boolean isFullOwner = dm.calculateDistance(extreme, candidate) < dm.calculateDistance(extreme, competitor);

            return isFullOwner;
        }

        /**
         * Returns the distance between a point and an hyperrectangle.
         * 
         * @param x
         *            the point
         * @return the distance
         * @throws Exception
         *             if something goes wrong
         */
        private double distanceToHrect(Instance x) throws Exception {
            double distance = 0.0;

           
            float[] closestPointValues=new float[x.size()];
            boolean inside = true;
            for (int i = 0; i < x.size(); i++) {
                if (x.getValue(i) < m_NodeRanges[i][R_MIN]) {
                    closestPointValues[i]= (float)m_NodeRanges[i][R_MIN];
                    inside = false;
                } else if (x.getValue(i) > m_NodeRanges[i][R_MAX]) {
                    closestPointValues[i]= (float) m_NodeRanges[i][R_MAX];
                    inside = false;
                }
            }
            
            Instance closestPoint = new SimpleInstance(closestPointValues,x.getWeight(),x.isClassSet(),x.getClassValue());
        
            if (!inside)
                distance = dm.calculateDistance(closestPoint, x);
            return distance;
        }

        

        /**
         * Assigns instances of this node to center. Center to be assign to is
         * decided by the distance function.
         * 
         * @param ranges
         *            min's and max's of attributes
         * @param centers
         *            all the input centers
         * @param centList
         *            the list of centers to work with
         * @param assignments
         *            index list of last assignments
         * @throws Exception
         *             if something goes wrong
         */
        public void assignSubToCenters(double[][] ranges, Instance[] centers, int[] centList, int[] assignments)
                throws Exception {

            // todo: undecided situations

            // WARNING: assignments is "input/output-parameter"
            // should not be null and the following should not happen
            if (assignments == null) {
                assignments = new int[data.size()];
                for (int i = 0; i < assignments.length; i++) {
                    assignments[i] = -1;
                }
            }

            // set assignments for all instances of this node
            for (int i = m_Start; i <= m_End; i++) {
                int instIndex = m_InstList[i];
                Instance inst = data.getInstance(instIndex);
                // if (instList[i] == 664) System.out.println("664***");
                int newC = closestPoint(inst, centers, centList);
                // int newC = clusterProcessedInstance(inst, centers);
                assignments[instIndex] = newC;
            }
        }

        // WEKA euclidean distance method
        private int closestPoint(Instance inst, Instance[] centers, int[] centList) {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * Find k nearest neighbours to target by simply searching through all
         * instances in the leaf. No check on missing class.
         * 
         * @param target
         *            the instance to find nearest neighbour for
         * @return the minimal distance found
         * @throws Exception
         *             if something goes wrong
         */
        public double simpleKNearestNeighbour(Instance target) throws Exception {

            double dist = 0;
            int currIndex;
            // sets and uses:
            // double m_MinDist
            // double m_MaxMinDist
            // int m_FurthestNear
            int i = m_NearestListLength;
            int index = m_Start;

            // if no instances, return max value as distance
            if (m_End < m_Start)
                return Double.MAX_VALUE;

            if (m_NearestListLength < m_kNN) {
                for (; (index <= m_End) && (i < m_kNN);) {
                    currIndex = m_InstList[index];
                    Instance trainInstance = data.getInstance(m_InstList[index]);

                    if (target != trainInstance) { // for hold-one-out
                        // cross-validation
                        // if(print==true)
                        // OOPS("K: "+i);
                        dist = distance(target, trainInstance, Double.MAX_VALUE, print);
                        m_NearestList[i] = currIndex;
                        m_DistanceList[i] = dist;
                        i++;
                    }
                    index++;
                }
                m_NearestListLength = i;
            }

            // set the new furthest nearest
            m_FurthestNear = checkFurthestNear(); // FURTHEST IN m_kNN NEAREST
            // NEIGHBOURS
            m_MaxMinDist = m_DistanceList[m_FurthestNear];

            int firstFurthestIndex = -1;
            double firstFurthestDistance = -1;
            int oldNearestListLength = -1;
            // check all or rest of instances if nearer
            for (; index <= m_End; index++) {

                currIndex = m_InstList[index];
                Instance trainInstance = data.getInstance(currIndex);
                if (target != trainInstance) { // for hold-one-out
                                                // cross-validation

                    dist = distance(target, trainInstance, m_MaxMinDist, print);

                    // is instance one of the nearest?
                    if (dist < m_MaxMinDist) {

                        // set instance as one of the nearest,
                        // replacing the last furthest nearest
                        firstFurthestIndex = m_NearestList[m_FurthestNear];
                        firstFurthestDistance = m_DistanceList[m_FurthestNear];
                        m_NearestList[m_FurthestNear] = currIndex;
                        m_DistanceList[m_FurthestNear] = dist;

                        // set the new furthest nearest
                        m_FurthestNear = checkFurthestNear();
                        m_MaxMinDist = m_DistanceList[m_FurthestNear];

                        if (m_MultipleFurthest) {
                            // remove multiple entries of old furthest nearest
                            oldNearestListLength = m_NearestListLength;
                            m_NearestListLength = m_kNN;
                            m_MultipleFurthest = false;
                        }

                        // the instance just replaced is at same distance as
                        // furthest nearest
                        // therefore there are multiple furthest nearest
                        if (firstFurthestDistance == m_MaxMinDist) {
                            m_MultipleFurthest = true;
                            if (oldNearestListLength != -1)
                                m_NearestListLength = oldNearestListLength;
                            m_NearestList[m_NearestListLength] = firstFurthestIndex;
                            m_DistanceList[m_NearestListLength] = firstFurthestDistance;
                            m_NearestListLength++;
                        }

                        // get rid of the old list length as it is no longer
                        // needed
                        // and can create problems.
                        oldNearestListLength = m_NearestListLength;
                    } else {
                        if (dist == m_MaxMinDist) {
                            // instance is at same distance as furthest nearest
                            m_MultipleFurthest = true;
                            m_NearestList[m_NearestListLength] = currIndex;
                            m_DistanceList[m_NearestListLength] = dist;
                            m_NearestListLength++;
                        }
                    }
                }
            }

            return m_MaxMinDist;
        }

        // WEKA euclidean distance method
        private double distance(Instance target, Instance trainInstance, double max_value, boolean print) {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * Finds the nearest neighbour to target, this method is called
         * recursively.
         * 
         * @param target
         *            the instance to find nearest neighbour for
         * @return the minimal distance found
         * @throws Exception
         *             if something goes wrong
         */
        private double kNearestNeighbour(Instance target) throws Exception {
            double maxDist;
            KDTreeNode nearer, further;

            // if is a leaf then the instance is in this hyperrectangle
            if (this.isALeaf()) {
                // return distance to kthnearest (and index of all
                // all k nearest in m_NearestList)
                return this.simpleKNearestNeighbour(target);
            }
            boolean targetInLeft = valueIsSmallerEqual(target, m_SplitDim, m_SplitValue);

            if (targetInLeft) {
                nearer = m_Left;
                further = m_Right;
            } else {
                nearer = m_Right;
                further = m_Left;
            }
            // look for nearer neighbours in nearer half
            maxDist = nearer.kNearestNeighbour(target);

            // ... now look in further half if maxDist reaches into it
            //Instance splitPoint = new SimpleInstance(target);
            //splitPoint.setValue(m_SplitDim, m_SplitValue);
            float[]splitValues=target.toArray();
            splitValues[m_SplitDim]=(float)m_SplitValue;
            Instance splitPoint=new SimpleInstance(splitValues,target.getWeight(),target.isClassSet(),target.getClassValue());
            double distanceToSplit = distance(target, splitPoint, Double.MAX_VALUE);
            boolean lookInSecondHalf = maxDist >= distanceToSplit;

            if (lookInSecondHalf) {
                // System.out.println("Searching into the 2nd half of the
                // tree.");
                // look for nearer neighbours in further half
                maxDist = further.kNearestNeighbour(target);
            }
            return maxDist;
        }

        // WEKA method Euclidean distance
        private double distance(Instance target, Instance splitPoint, double max_value) {
            // TODO Auto-generated method stub
            return 0;
        }

    }
}
