/**
 * SOM.java
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
 * Copyright (c) 2001-2006  Tomi Suuronen
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;

/**
 * An implementation of the Self Organizing Maps algorithm as proposed by
 * Kohonen.
 * 
 * This implementation is derived from the Bachelor thesis of Tomi Suuronen
 * titled "Java2 implementation of Self-Organizing Maps based on Neural
 * Networkds utilizing XML based Application Languages for Information Exchange
 * and Visualization.". (http://javasom.sourceforge.net/)
 * 
 * @author Tomi Suuronen
 * @author Thomas Abeel
 * 
 */
public class SOM implements Clusterer {
    public class JSomMath {

        private double[] cacheVector; // cache vector for temporary storage.

        private int sizeVector; // size of the cache vector.

        private double distCache; // distance cache.

        private double gaussianCache; // double cache for gaussian

        // neighbourhood function operations.

        private int distCacheSize; // cache for the length of the two vectors.

        /**
         * Constructor.
         * 
         * @param int
         *            vectorSize - Size of a weight/input vector.
         */
        public JSomMath(int vectorSize) {
            cacheVector = new double[vectorSize];
            for (int i = 0; i < vectorSize; i++) {
                cacheVector[i] = 0.0;
            }
            sizeVector = cacheVector.length;
        }

        /**
         * Calculates the Euclidean distance between two vectors.
         * 
         * @param double[]
         *            x - 1st vector.
         * @param double[]
         *            y - 2nd vector.
         * @return double - returns the distance between two vectors, x and y
         */
        public double getDistance(double[] x, double[] y) {
            return Math.sqrt(getSquareDistance(x, y));
        }

        /**
         * Calculates the square of Euclidean distance between two vectors. It
         * is faster to calculate the square of Euclidean distance than the
         * distance itself.
         * 
         * @param double[]
         *            x - 1st vector.
         * @param double[]
         *            y - 2nd vector.
         * @return double - returns the square of distance between x and y
         *         vectors.
         */
        public double getSquareDistance(double[] x, double[] y) {
            distCache = 0.0;
            distCacheSize = x.length;
            for (int i = 0; i < distCacheSize; i++) {
                distCache += (x[i] - y[i]) * (x[i] - y[i]);
            }
            return distCache;
        }

        /**
         * Calculates the exponential learning-rate parameter value.
         * 
         * @param int
         *            n - current step (time).
         * @param double
         *            a - initial value for learning-rate parameter (should be
         *            close to 0.1).
         * @param int
         *            A - time constant (usually the number of iterations in the
         *            learning process).
         * @return double - exponential learning-rate parameter value.
         */
        public double expLRP(int n, double a, int A) {
            return (a * Math.exp(-1.0 * ((double) n) / ((double) A)));
        }

        /**
         * Calculates the linear learning-rate parameter value.
         * 
         * @param int
         *            n - current step (time).
         * @param double
         *            a - initial value for learning-rate parameter (should be
         *            close to 0.1).
         * @param int
         *            A - another constant (usually the number of iterations in
         *            the learning process).
         * @return double - linear learning-rate parameter value.
         */
        public double linLRP(int n, double a, int A) {
            return (a * (1 - ((double) n) / ((double) A)));
        }

        /**
         * Calculates the inverse time learning-rate parameter value.
         * 
         * @param int
         *            n - current step (time).
         * @param double
         *            a - initial value for learning-rate parameter (should be
         *            close to 0.1).
         * @param double
         *            A - another constant.
         * @param double
         *            B - another constant.
         * @return double - inverse time learning-rate parameter value.
         */
        public double invLRP(int n, double a, double A, double B) {
            return (a * (A / (B + n)));
        }

        /**
         * Calculates the gaussian neighbourhood width value.
         * 
         * @param double
         *            g - initial width value of the neighbourhood.
         * @param int
         *            n - current step (time).
         * @param int
         *            t - time constant (usually the number of iterations in the
         *            learning process).
         * @return double - adapted gaussian neighbourhood function value.
         */
        public double gaussianWidth(double g, int n, int t) {
            return (g * Math.exp(-1.0 * ((double) n) / ((double) t)));
        }

        /**
         * Calculates the Gaussian neighbourhood value.
         * 
         * @param double[]
         *            i - winning neuron location in the lattice.
         * @param double[]
         *            j - excited neuron location in the lattice.
         * @param double
         *            width - width value of the neighbourhood.
         * @return double - Gaussian neighbourhood value.
         */
        private double gaussianNF(double[] i, double[] j, double width) {
            gaussianCache = getDistance(i, j);
            return (Math.exp(-1.0 * gaussianCache * gaussianCache / (2.0 * width * width)));
        }

        /**
         * Calculates whether the excited neuron is in the Bubble neighbourhood
         * set.
         * 
         * @param double[]
         *            i - winning neuron location in the lattice.
         * @param double[]
         *            j - excited neuron location in the lattice.
         * @param double
         *            g - width value of the neighbourhood.
         * @return boolean - true if located in the Bubble neighbourhood set.
         */
        private boolean bubbleNF(double[] i, double[] j, double g) {
            if (getDistance(i, j) <= g) {
                return true;
            }
            return false;
        }

        /**
         * Calculates the new adapted values for a weight vector, based on
         * Bubble neighbourhood.
         * 
         * @param double[]
         *            x - input vector.
         * @param double[]
         *            w - weight vector.
         * @param double[]
         *            i - winning neuron location in the lattice.
         * @param double[]
         *            j - excited neuron location in the lattice.
         * @param double
         *            g - adapted width value of the neighbourhood.
         * @param double
         *            lrp - adapted learning-rate parameter value.
         * @return double[] - Returns the adapted neuron values.
         */
        public double[] bubbleAdaptation(double[] x, double[] w, double[] i, double[] j, double g, double lrp) {
            if (bubbleNF(i, j, g)) {
                for (int k = 0; k < sizeVector; k++) {
                    cacheVector[k] = w[k] + lrp * (x[k] - w[k]);
                }
            } else {
                return w;
            }
            return cacheVector;
        }

        /**
         * Calculates the new adapted values for a weight vector, based on
         * Gaussian neighbourhood.
         * 
         * @param double[]
         *            x - input vector.
         * @param double[]
         *            w - weight vector.
         * @param double[]
         *            i - winning neuron location in the lattice.
         * @param double[]
         *            j - excited neuron location in the lattice.
         * @param double
         *            width - adapted width value of the neighbourhood.
         * @param double
         *            lrp - adapted learning-rate parameter value.
         * @return double[] - Returns the adapted neuron values.
         */
        public double[] gaussianAdaptation(double[] x, double[] w, double[] i, double[] j, double width, double lrp) {
            gaussianCache = gaussianNF(i, j, width);
            for (int k = 0; k < sizeVector; k++) {
                cacheVector[k] = w[k] + lrp * gaussianCache * (x[k] - w[k]);
            }
            return cacheVector;
        }
    }

    public class InputVectors extends ArrayList<SomNode> {

        // private ArrayList input; //input vectors

        /**
         * 
         */
        private static final long serialVersionUID = 703966236164827750L;

        /**
         * Main constructor for this map. Used to contain all the input vectors.
         */
        public InputVectors() {
            super(1000);
        }

        /**
         * Main constructor for this map. Used to contain all the input vectors.
         * 
         * @param capacity
         *            Number of input vectors.
         */
        public InputVectors(int capacity) {
            super(capacity);
        }

        /**
         * Adds a new input vector.
         * 
         * @param node
         *            The SomNode object added.
         */
        public void addInputVector(SomNode node) {
            // input.add(node);
            add(node);
        }

        /**
         * Returns a input vector from the specified index.
         * 
         * @param index
         *            The index of SomNode.
         * @return SomNode - returns the SomNode object at the specified index.
         */
        public SomNode getSomNodeAt(int index) {
            // return ((SomNode) input.get(index));
            return ((SomNode) get(index));
        }

        /**
         * Returns a Node values of a specific input vector from the specified
         * index.
         * 
         * @param index
         *            The index of SomNode.
         * @return double[] - returns the Node values from the specified index.
         */
        public double[] getNodeValuesAt(int index) {
            // SomNode cache = (SomNode) input.get(index);
            SomNode cache = (SomNode) get(index);
            return (cache.getValues());
        }

        /**
         * Sets the node values at a specific node.
         * 
         * @param index
         *            Index of the SomNode
         * @param values
         *            Values of the SomNode
         */
        public void setNodeValuesAt(int index, double[] values) {
            // SomNode cache = (SomNode) input.get(index);
            SomNode cache = (SomNode) get(index);
            cache.setValues(values);
            // input.set(index, cache);
            set(index, cache);
        }

        /**
         * Returns a Node label of a specific input vector from the specified
         * index.
         * 
         * @param index
         *            The index of SomNode.
         * @return String - returns the Node label from the specified index.
         */
        public String getNodeLabelAt(int index) {
            // SomNode cache = (SomNode) input.get(index);
            SomNode cache = (SomNode) get(index);
            return (cache.getLabel());
        }

        /**
         * Returns the number of input vectors.
         * 
         * @return int - returns the number of input vectors.
         */
        public int getCount() {
            // return input.size();
            return size();
        }
    }

    public class WeightVectors extends ArrayList<SomNode> {

        /**
         * 
         */
        private static final long serialVersionUID = -8922053499602333314L;

        private double[] values;

        private double[] location;

        private String lattice; // topology of the map

        private Random generator;

        private int xDim;

        private int yDim;

        private int dimension; // dimensionality of a node

        private final double YVALUE = 0.866;

        /**
         * Main constructor. Used to contain the synaptic weight vectors during
         * the learning phase.
         * 
         * @param xDim
         *            X-dimension of the map constructed.
         * @param yDim
         *            Y-dimension of the map constructed.
         * @param dimension
         *            dimensionality of a node. This is the dimension of an
         *            instance
         * @param type
         *            Lattice type of the map constructed (hexa or rect)
         */
        public WeightVectors(int xDim, int yDim, int dimension, String type) {
            super(xDim * yDim);
            int size = xDim * yDim;
            this.xDim = xDim;
            this.yDim = yDim;
            this.dimension = dimension;
            values = new double[dimension];
            location = new double[2];
            generator = new Random();
            lattice = type;
            int yCounter = 0;
            int xCounter = 0;
            double xValue = 0;
            double yValue = 0;
            boolean evenRow = false; // for hexagonal lattice, cheking if the
            // current row number is even or odd
            if (lattice.equals("rect")) { // rectangular lattice
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < dimension; j++) {
                        values[j] = generator.nextDouble();
                    }
                    if (xCounter < xDim) {
                        location[0] = xCounter;
                        location[1] = yCounter;
                        xCounter++;
                    } else {
                        xCounter = 0;
                        yCounter++;
                        location[0] = xCounter;
                        location[1] = yCounter;
                        xCounter++;
                    }
                    add(new SomNode(values, location));
                }
            } else { // hexagonal lattice
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < dimension; j++) {
                        values[j] = generator.nextDouble();
                    }
                    if (xCounter < xDim) {
                        location[0] = xValue;
                        location[1] = yValue;
                        xValue += 1.0;
                        xCounter++;
                    } else {
                        xCounter = 0;
                        yValue += YVALUE;
                        if (evenRow) {
                            xValue = 0.0;
                            evenRow = false;
                        } else {
                            xValue = 0.5;
                            evenRow = true;
                        }
                        location[0] = xValue;
                        location[1] = yValue;
                        xValue += 1.0;
                        xCounter++;
                    }
                    add(new SomNode(values, location));
                }
            }
        }

        /**
         * Returns the x-dimension of this map.
         * 
         * @return int - X-dimensionality of the map.
         */
        public int getXDimension() {
            return xDim;
        }

        /**
         * Returns the y-dimension of this map.
         * 
         * @return int - Yy-dimensionality of the map.
         */
        public int getYDimension() {
            return yDim;
        }

        /**
         * Returns the node values at a specific node.
         * 
         * @param index
         *            Index of the SomNode
         * @return double[] - Returns the Node values from the specified index.
         */
        public double[] getNodeValuesAt(int index) {
            SomNode cache = (SomNode) get(index);
            return (cache.getValues());
        }

        /**
         * Sets the node values at a specific node.
         * 
         * @param index
         *            Index of the SomNode
         * @param values
         *            Values of the SomNode
         */
        public void setNodeValuesAt(int index, double[] values) {
            SomNode cache = (SomNode) get(index);
            cache.setValues(values);
            set(index, cache);
        }

        /**
         * Returns the node values at a specific node.
         * 
         * @param index
         *            Index of the SomNode
         * @return double[] - Returns the Node location from the specified
         *         index.
         */
        public double[] getNodeLocationAt(int index) {
            SomNode cache = (SomNode) get(index);
            return (cache.getLocation());
        }

        /**
         * Returns the dimensionality of a node (it is the same for all of
         * them).
         * 
         * @return int - Dimensionality of nodes.
         */
        public int getDimensionalityOfNodes() {
            return dimension;
        }

        /**
         * Returns the number of weight vectors.
         * 
         * @return int - Returns the number of weight vectors.
         */
        public int getCount() {
            return size();
        }

        /**
         * Sets the label of a specific weight vector at the specified index.
         * 
         * @param index
         *            The index of SomNode.
         * @param label
         *            The new label for this SomNode.
         * @return String - Returns the Node label from the specified index.
         */
        public void setNodeLabelAt(int index, String label) {
            // SomNode cache = (SomNode) weight.get(index);
            SomNode cache = (SomNode) get(index);
            if (cache.isLabeled()) {
                cache.addLabel(label);
            } else {
                cache.setLabel(label);
            }
            set(index, cache);
        }

        /**
         * Returns a Node label of a specific weight vector from the specified
         * index.
         * 
         * @param index
         *            The index of SomNode.
         * @return String - Returns the Node label from the specified index.
         */
        public String getNodeLabelAt(int index) {
            SomNode cache = (SomNode) get(index);
            return (cache.getLabel());
        }

        /**
         * Returns the lattice type used in initializing node locations.
         * 
         * @return String - Lattice :: hexa | rect.
         */
        public String getLatticeType() {
            return lattice;
        }
    }

    public class SomNode {

        public String toString() {
            String out = "";
            out += "\tVAL: " + Arrays.toString(values);
            out += "\n\tPOS: " + Arrays.toString(location);
            out += "\n\tLAB: " + label;
            out += "\n";
            return out;
        }

        private String label;

        private double[] values;

        private double[] location;

        /**
         * Main constructor.
         */
        public SomNode() {
            label = "";
            values = new double[1];
            location = new double[1];
        }

        /**
         * Main constructor (for input vectors).
         * 
         * @param String
         *            label - Name of this node.
         * @param double[]
         *            values - All the values of this node.
         */
        public SomNode(String label, double[] values) {
            this.label = label;
            this.values = (double[]) values.clone();
            location = new double[1];
        }

        /**
         * Main constructor (for weight vectors).
         * 
         * @param String
         *            label - Name of this node.
         * @param double[]
         *            values - All the values of this node.
         * @param double[]
         *            location - The location of this node.
         */
        public SomNode(double[] values, double[] location) {
            label = "";
            this.values = (double[]) values.clone();
            this.location = (double[]) location.clone();
        }

        /**
         * Sets values for every dimension in this node.
         * 
         * @param double[]
         *            values - Sets all the values for this node.
         */
        public void setValues(double[] values) {
            this.values = (double[]) values.clone();
        }

        /**
         * Returns all the values of this node.
         * 
         * @return double[] - Returns the numerical presentation of this node.
         */
        public double[] getValues() {
            return ((double[]) values.clone());
        }

        /**
         * Set the label name for this node.
         * 
         * @param String -
         *            Label of this node.
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * Set the secondary label(s) for this node.
         * 
         * @param String -
         *            Another label of this node.
         */
        public void addLabel(String label) {
            this.label += ", " + label;
        }

        /**
         * Returns the label of this node.
         * 
         * @return String - Returns the label of this node if any.
         */
        public String getLabel() {
            return label;
        }

        /**
         * Returns the location of this node.
         * 
         * @return double[] - Returns the location of this node if any.
         */
        public double[] getLocation() {
            return ((double[]) location.clone());
        }

        /**
         * Returns the information about wether labeling has been done.
         * 
         * @return boolean - Returns true if this node has been labeled
         *         otherwise false.
         */
        public boolean isLabeled() {
            return label.length() > 0;
        }
    }

    public class JSomTraining {

        private double length; // caching

        private double lcache; // caching

        private int index; // caching

        private JSomMath math;

        private WeightVectors wVector;

        private InputVectors iVector;

        private String neigh; // the neighborhood function type used ::

        // step(bubble) | gaussian

        private int steps; // running length (number of steps) in training

        private double lrate; // initial learning rate parameter value

        private String lrateType; // learning rate parameter type ::

        // exponential | linear | inverse

        private double width; // initial "width" of training area

        // private int xDim; // number of units in the x-direction
        //
        // private int yDim; // number of units in the y-direction

        private Random generator;

        private int wVectorSize; // the number of weight vectors

        private int iVectorSize; // the number of input vectors

        /**
         * Constructor.
         * 
         * @param WeightVectors
         *            wVector - weight vectors.
         * @param InputVectors
         *            iVector - input vectors.
         */
        public JSomTraining(WeightVectors wVector, InputVectors iVector) {
            this.wVector = wVector;
            this.iVector = iVector;
            math = new JSomMath(wVector.getDimensionalityOfNodes());
            // xDim = wVector.getXDimension();
            // yDim = wVector.getYDimension();
            generator = new Random();
        }

        /**
         * Sets the ordering instructions for the ordering process.
         * 
         * @param int
         *            steps - number of steps in this ordering phase.
         * @param double
         *            lrate - initial value for learning rate (usually near
         *            0.1).
         * @param int
         *            radius - initial radius of neighbors.
         * @param String
         *            lrateType - states which learning-rate parameter function
         *            is used :: exponential | linear | inverse
         * @param String
         *            neigh - the neighborhood function type used ::
         *            step(bubble) | gaussian
         */
        public void setTrainingInstructions(int steps, double lrate, int radius, String lrateType, String neigh) {
            this.steps = steps;
            this.lrate = lrate;
            this.lrateType = lrateType;
            this.neigh = neigh;
            width = radius;
        }

        /**
         * Does the training phase.
         * 
         * @return WeightVectors - Returns the trained weight vectors.
         */
        public WeightVectors doTraining() {
            // fireBatchStart("Training phase");
            iVectorSize = iVector.getCount();
            wVectorSize = wVector.getCount();
            if (lrateType.equals("exponential") && neigh.equals("step")) {
                doBubbleExpAdaptation();
            } else if (lrateType.equals("linear") && neigh.equals("step")) {
                doBubbleLinAdaptation();
            } else if (lrateType.equals("inverse") && neigh.equals("step")) {
                doBubbleInvAdaptation();
            } else if (lrateType.equals("exponential") && neigh.equals("gaussian")) {
                doGaussianExpAdaptation();
            } else if (lrateType.equals("linear") && neigh.equals("gaussian")) {
                doGaussianLinAdaptation();
            } else {
                // inverse and gaussian
                doGaussianInvAdaptation();
            }
            // fireBatchEnd("Training phase");
            return wVector;
        }

        /*
         * Does the Bubble Exponential Adaptation to the Weight Vectors.
         */
        private void doBubbleExpAdaptation() {
            double[] input;
            double[] wLocation; // location of a winner node
            double s = (double) steps;
            double wCache; // width cache
            double exp;
            for (int n = 0; n < steps; n++) {
                wCache = Math.ceil(width * (1 - (n / s))); // adapts the width
                // function as it is
                // a function of
                // time.
                exp = math.expLRP(n, lrate, steps);
                input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
                index = resolveIndexOfWinningNeuron(input);
                wLocation = wVector.getNodeLocationAt(index);
                for (int h = 0; h < wVectorSize; h++) {
                    wVector.setNodeValuesAt(h, math.bubbleAdaptation(input, wVector.getNodeValuesAt(h), wLocation,
                            wVector.getNodeLocationAt(h), wCache, exp));
                }
                // fireBatchProgress(n, steps);
            }
        }

        /*
         * Does the Bubble Linear Adaptation to the Weight Vectors.
         */
        private void doBubbleLinAdaptation() {
            double[] input;
            double[] wLocation; // location of a winner node
            double s = (double) steps;
            double wCache; // width cache
            double lin;
            for (int n = 0; n < steps; n++) {
                wCache = Math.ceil(width * (1 - (n / s))); // adapts the width
                // function as it is
                // a function of
                // time.
                lin = math.linLRP(n, lrate, steps);
                input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
                index = resolveIndexOfWinningNeuron(input);
                wLocation = wVector.getNodeLocationAt(index);
                for (int h = 0; h < wVectorSize; h++) {
                    wVector.setNodeValuesAt(h, math.bubbleAdaptation(input, wVector.getNodeValuesAt(h), wLocation,
                            wVector.getNodeLocationAt(h), wCache, lin));
                }
                // fireBatchProgress(n, steps);
            }
        }

        /*
         * Does the Bubble Inverse-time Adaptation to the Weight Vectors.
         */
        private void doBubbleInvAdaptation() {
            double[] input;
            double[] wLocation; // location of a winner node
            double A; // constants A and B which are considered equal
            double s = (double) steps;
            double wCache; // width cache
            double inv;
            A = steps / 100.0;
            for (int n = 0; n < steps; n++) {
                wCache = Math.ceil(width * (1 - (n / s))); // adapts the width
                // function as it is
                // a function of
                // time.
                inv = math.invLRP(n, lrate, A, A);
                input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
                index = resolveIndexOfWinningNeuron(input);
                wLocation = wVector.getNodeLocationAt(index);
                for (int h = 0; h < wVectorSize; h++) {
                    wVector.setNodeValuesAt(h, math.bubbleAdaptation(input, wVector.getNodeValuesAt(h), wLocation,
                            wVector.getNodeLocationAt(h), wCache, inv));
                }
                // fireBatchProgress(n, steps);
            }
        }

        /*
         * Does the Gaussian Exponential Adaptation to the Weight Vectors.
         */
        private void doGaussianExpAdaptation() {
            double[] input;
            double[] wLocation; // location of a winner node
            double wCache; // width cache
            double exp;
            for (int n = 0; n < steps; n++) {
                wCache = math.gaussianWidth(width, n, steps);
                exp = math.expLRP(n, lrate, steps);
                input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
                index = resolveIndexOfWinningNeuron(input);
                wLocation = wVector.getNodeLocationAt(index);
                for (int h = 0; h < wVectorSize; h++) {
                    wVector.setNodeValuesAt(h, math.gaussianAdaptation(input, wVector.getNodeValuesAt(h), wLocation,
                            wVector.getNodeLocationAt(h), wCache, exp));
                }
                // fireBatchProgress(n, steps);
            }
        }

        /*
         * Does the Gaussian Linear Adaptation to the Weight Vectors.
         */
        private void doGaussianLinAdaptation() {
            double[] input;
            double[] wLocation; // location of a winner node
            double wCache; // width cache
            double lin;
            for (int n = 0; n < steps; n++) {
                wCache = math.gaussianWidth(width, n, steps);
                lin = math.linLRP(n, lrate, steps);
                input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
                index = resolveIndexOfWinningNeuron(input);
                wLocation = wVector.getNodeLocationAt(index);
                for (int h = 0; h < wVectorSize; h++) {
                    wVector.setNodeValuesAt(h, math.gaussianAdaptation(input, wVector.getNodeValuesAt(h), wLocation,
                            wVector.getNodeLocationAt(h), wCache, lin));
                }
                // fireBatchProgress(n, steps);
            }
        }

        /*
         * Does the Gaussian Inverse-time Adaptation to the Weight Vectors.
         */
        private void doGaussianInvAdaptation() {
            double[] input;
            double[] wLocation; // location of a winner node
            double A; // constants A and B which are considered equal
            double wCache; // width cache
            double inv;
            A = steps / 100.0;
            for (int n = 0; n < steps; n++) {
                wCache = math.gaussianWidth(width, n, steps);
                inv = math.invLRP(n, lrate, A, A);
                input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
                index = resolveIndexOfWinningNeuron(input);
                wLocation = wVector.getNodeLocationAt(index);
                for (int h = 0; h < wVectorSize; h++) {
                    wVector.setNodeValuesAt(h, math.gaussianAdaptation(input, wVector.getNodeValuesAt(h), wLocation,
                            wVector.getNodeLocationAt(h), wCache, inv));
                }
                // fireBatchProgress(n, steps);
            }
        }

        /*
         * Finds the winning neuron for this input vector.
         * 
         * @param double[] values - values of an input vector. @return int -
         * index of the winning neuron.
         */
        private int resolveIndexOfWinningNeuron(double[] values) {
            length = math.getSquareDistance(values, wVector.getNodeValuesAt(0));
            index = 0;
            for (int i = 1; i < wVectorSize; i++) {
                lcache = math.getSquareDistance(values, wVector.getNodeValuesAt(i));
                if (lcache < length) {
                    index = i;
                    length = lcache;
                }
            }
            return index;
        }
    }

    public Dataset[] executeClustering(Dataset data) {

        // hexa || rect
        WeightVectors wV = new WeightVectors(2, 2, data.getInstance(0).size(), "hexa");
        InputVectors iV = convertDataset(data);
        JSomTraining jst = new JSomTraining(wV, iV);
        // exponential || inverse || linear
        // gaussian || step
        jst.setTrainingInstructions(100000, 0.1, 1, "inverse", "gaussian");
        WeightVectors out = jst.doTraining();
        // System.out.println(out);
        // System.out.println(iV);
        JSomLabeling labels = new JSomLabeling(out, iV);
        out = labels.doLabeling();
        Dataset[] clusters = new Dataset[wV.size()];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new SimpleDataset();
        }
        for (int i = 0; i < data.size(); i++) {
            clusters[labels.resolveIndexOfWinningNeuron(iV.getNodeValuesAt(i))].addInstance(data.getInstance(i));
        }
        // System.out.println(out);
        return clusters;

    }

    public class JSomLabeling {

        private WeightVectors wVector;

        private InputVectors iVector;

        private double distCache;

        private double length;

        private double lcache;

        private int distCacheSize;

        private int iSize; // the number of input vectors

        private int wSize; // the number of weight vectors

        private int index;

        /**
         * Constructor.
         * 
         * @param WeightVectors
         *            wVector - weight vectors.
         * @param InputVectors
         *            iVector - input vectors.
         */
        public JSomLabeling(WeightVectors wVector, InputVectors iVector) {
            this.wVector = wVector;
            this.iVector = iVector;
            distCacheSize = wVector.getDimensionalityOfNodes();
            iSize = iVector.getCount();
            wSize = wVector.getCount();
        }

        /**
         * Does the labeling phase.
         * 
         * @return WeightVectors - Returns the labeled weight vectors.
         */
        public WeightVectors doLabeling() {
            for (int i = 0; i < iSize; i++) {
                wVector.setNodeLabelAt(resolveIndexOfWinningNeuron(iVector.getNodeValuesAt(i)), iVector
                        .getNodeLabelAt(i));
            }
            return wVector;
        }

        /*
         * Finds the winning neuron for this input vector. Determines the
         * winning neuron by calculating the square of Eclidean distance of two
         * vectors as it will give the same result as the Euclidean distance.
         * 
         * @param double[] values - values of an input vector. @return int -
         * index of the winning neuron.
         */
        private int resolveIndexOfWinningNeuron(double[] values) {
            length = getSquareDistance(values, wVector.getNodeValuesAt(0));
            index = 0;
            for (int i = 1; i < wSize; i++) {
                lcache = getSquareDistance(values, wVector.getNodeValuesAt(i));
                if (lcache < length) {
                    index = i;
                    length = lcache;
                }
            }
            return index;
        }

        /**
         * Calculates the square of Euclidean distance between two vectors. It
         * is faster to calculate the square of Euclidean distance than the
         * distance itself.
         * 
         * @param double[]
         *            x - 1st vector.
         * @param double[]
         *            y - 2nd vector.
         * @return double - returns the square of distance between x and y
         *         vectors.
         */
        private double getSquareDistance(double[] x, double[] y) {
            distCache = 0.0;
            for (int i = 0; i < distCacheSize; i++) {
                distCache += (x[i] - y[i]) * (x[i] - y[i]);
            }
            return distCache;
        }
    }

    private InputVectors convertDataset(Dataset data) {
        InputVectors iVS = new InputVectors();
        for (int i = 0; i < data.size(); i++) {
            double[] values = toDouble(data.getInstance(i).toArray());
            SomNode tmp = new SomNode("node_" + i, values);
            iVS.add(tmp);
        }
        return iVS;
    }

    private double[] toDouble(float[] fs) {
        double[] out = new double[fs.length];
        for (int i = 0; i < fs.length; i++)
            out[i] = fs[i];
        return out;
    }

}
