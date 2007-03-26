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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;


import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * <!-- globalinfo-start --> Simple EM (expectation maximisation) class.<br/>
 * <br/> EM assigns a probability distribution to each instance which indicates
 * the probability of it belonging to each of the clusters. EM can decide how
 * many clusters to create by cross validation, or you may specify apriori how
 * many clusters to generate.<br/> <br/> The cross validation performed to
 * determine the number of clusters is done in the following steps:<br/> 1. the
 * number of clusters is set to 1<br/> 2. the training set is split randomly
 * into 10 folds.<br/> 3. EM is performed 10 times using the 10 folds the usual
 * CV way.<br/> 4. the loglikelihood is averaged over all 10 results.<br/> 5.
 * if loglikelihood has increased the number of clusters is increased by 1 and
 * the program continues at step 2. <br/> <br/> The number of folds is fixed to
 * 10, as long as the number of instances in the training set is not smaller 10.
 * If this is the case the number of folds is set equal to the number of
 * instances. <p/>
 * 
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Thomas Abeel
 */
public class EMClustering implements Clusterer {
//    /**
//     * Class to support a building process of an estimator.
//     */
//    private static class Builder {
//
//        /** instances of the builder */
//        Dataset m_instances = null;
//
//        /** attribute index of the builder */
//        int m_attrIndex = -1;
//
//        /** class index of the builder, only relevant if class value index is set */
//        int m_classIndex = -1;
//
//        /** class value index of the builder */
//        int m_classValueIndex = -1;
//    }

    private class Estimator {

        /**
         * The class value index is > -1 if subset is taken with specific class
         * value only
         */
        private double m_classValueIndex = -1.0;

        /** set if class is not important */
        private boolean m_noClass = true;

        /**
         * Initialize the estimator with a new dataset. Finds min and max first.
         * 
         * @param data
         *            the dataset used to build this estimator
         * @param attrIndex
         *            attribute the estimator is for
         * @exception Exception
         *                if building of estimator goes wrong
         */
        private void addValues(Dataset data, int attrIndex) throws Exception {
            // can estimator handle the data?
            // getCapabilities().testWithFail(data);

            double[] minMax = new double[2];

            try {
                EstimatorUtils.getMinMax(data, attrIndex, minMax);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
            }

            double min = minMax[0];
            double max = minMax[1];

            // factor is 1.0, data set has not been reduced
            addValues(data, attrIndex, min, max, 1.0);
        }

        /**
         * Initialize the estimator with all values of one attribute of a
         * dataset. Some estimator might ignore the min and max values.
         * 
         * @param data
         *            the dataset used to build this estimator
         * @param attrIndex
         *            attribute the estimator is for
         * @param min
         *            minimal border of range
         * @param max
         *            maximal border of range
         * @param factor
         *            number of instances has been reduced to that factor
         * @exception Exception
         *                if building of estimator goes wrong
         */
        private void addValues(Dataset data, int attrIndex, double min, double max, double factor) throws Exception {
            // no handling of factor, would have to be overridden

            // no handling of min and max, would have to be overridden

            int numInst = data.size();
            for (int i = 1; i < numInst; i++) {
                addValue(data.getInstance(i).getValue(attrIndex), 1.0);
            }
        }

        // /**
        // * Initialize the estimator using only the instance of one class. It
        // is
        // * using the values of one attribute only.
        // *
        // * @param data
        // * the dataset used to build this estimator
        // * @param attrIndex
        // * attribute the estimator is for
        // * @param classIndex
        // * index of the class attribute
        // * @param classValue
        // * the class value
        // * @exception Exception
        // * if building of estimator goes wrong
        // */
        // public void addValues(Dataset data, int attrIndex, int classIndex,
        // int classValue) throws Exception {
        // // can estimator handle the data?
        // m_noClass = false;
        //
        // // find the minimal and the maximal value
        // double[] minMax = new double[2];
        //
        // try {
        // EstimatorUtils.getMinMax(data, attrIndex, minMax);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // System.out.println(ex.getMessage());
        // }
        //
        // double min = minMax[0];
        // double max = minMax[1];
        //
        // // extract the instances with the given class value
        // Instances workData = new Instances(data, 0);
        // double factor = getInstancesFromClass(data, attrIndex, classIndex,
        // (double) classValue, workData);
        //
        // // if no data return
        // if (workData.numInstances() == 0)
        // return;
        //
        // addValues(data, attrIndex, min, max, factor);
        // }

        // /**
        // * Initialize the estimator using only the instance of one class. It
        // is
        // * using the values of one attribute only.
        // *
        // * @param data
        // * the dataset used to build this estimator
        // * @param attrIndex
        // * attribute the estimator is for
        // * @param classIndex
        // * index of the class attribute
        // * @param classValue
        // * the class value
        // * @param min
        // * minimal value of this attribute
        // * @param max
        // * maximal value of this attribute
        // * @exception Exception
        // * if building of estimator goes wrong
        // */
        // public void addValues(Dataset data, int attrIndex, int classIndex,
        // int classValue, double min, double max)
        // throws Exception {
        //
        // // extract the instances with the given class value
        // Instances workData = new Instances(data, 0);
        // double factor = getInstancesFromClass(data, attrIndex, classIndex,
        // (double) classValue, workData);
        //
        // // if no data return
        // if (workData.numInstances() == 0)
        // return;
        //
        // addValues(data, attrIndex, min, max, factor);
        // }

        // /**
        // * Returns a dataset that contains all instances of a certain class
        // * value.
        // *
        // * @param data
        // * dataset to select the instances from
        // * @param attrIndex
        // * index of the relevant attribute
        // * @param classIndex
        // * index of the class attribute
        // * @param classValue
        // * the relevant class value
        // * @return a dataset with only
        // */
        // private double getInstancesFromClass(Dataset data, int attrIndex, int
        // classIndex, double classValue,
        // Instances workData) {
        // // DBO.pln("getInstancesFromClass classValue"+classValue+"
        // // workData"+data.numInstances());
        //
        // int num = 0;
        // int numClassValue = 0;
        // for (int i = 0; i < data.numInstances(); i++) {
        // if (!data.instance(i).isMissing(attrIndex)) {
        // num++;
        // if (data.instance(i).value(classIndex) == classValue) {
        // workData.add(data.instance(i));
        // numClassValue++;
        // }
        // }
        // }
        //
        // Double alphaFactor = new Double((double) numClassValue / (double)
        // num);
        // return alphaFactor;
        // }

        // /**
        // * Get a probability estimate for a value.
        // *
        // * @param data
        // * the value to estimate the probability of
        // * @return the estimated probability of the supplied value
        // */
        // public abstract double getProbability(double data);

//        /**
//         * Build an estimator using the options. The data is given in the
//         * options.
//         * 
//         * @param est
//         *            the estimator used
//         * @param options
//         *            the list of options
//         * @param isIncremental
//         *            true if estimator is incremental
//         * @exception Exception
//         *                if something goes wrong or the user requests help on
//         *                command options
//         */
//        private void buildEstimator(Estimator est, String[] options, boolean isIncremental) {
//            // DBO.pln("buildEstimator");
//
//            boolean debug = false;
//            boolean helpRequest;
//
//            // read all options
//            Builder build = new Builder();
//            try {
//                setGeneralOptions(build, est, options);
//
//                if (est instanceof OptionHandler) {
//                    ((OptionHandler) est).setOptions(options);
//                }
//
//                Utils.checkForRemainingOptions(options);
//
//                buildEstimator(est, build.m_instances, build.m_attrIndex, build.m_classIndex, build.m_classValueIndex,
//                        isIncremental);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                System.out.println(ex.getMessage());
//                String specificOptions = "";
//                // Output the error and also the valid options
//                if (est instanceof OptionHandler) {
//                    specificOptions += "\nEstimator options:\n\n";
//                    Enumeration enumOptions = ((OptionHandler) est).listOptions();
//                    while (enumOptions.hasMoreElements()) {
//                        Option option = (Option) enumOptions.nextElement();
//                        specificOptions += option.synopsis() + '\n' + option.description() + "\n";
//                    }
//                }
//
//                String genericOptions = "\nGeneral options:\n\n" + "-h\n" + "\tGet help on available options.\n"
//                        + "-i <file>\n" + "\tThe name of the file containing input instances.\n"
//                        + "\tIf not supplied then instances will be read from stdin.\n" + "-a <attribute index>\n"
//                        + "\tThe number of the attribute the probability distribution\n"
//                        + "\testimation is done for.\n" + "\t\"first\" and \"last\" are also valid entries.\n"
//                        + "\tIf not supplied then no class is assigned.\n" + "-c <class index>\n"
//                        + "\tIf class value index is set, this attribute is taken as class.\n"
//                        + "\t\"first\" and \"last\" are also valid entries.\n"
//                        + "\tIf not supplied then last is default.\n" + "-v <class value index>\n"
//                        + "\tIf value is different to -1, select instances of this class value.\n"
//                        + "\t\"first\" and \"last\" are also valid entries.\n"
//                        + "\tIf not supplied then all instances are taken.\n";
//
//                throw new Exception('\n' + ex.getMessage() + specificOptions + genericOptions);
//            }
//        }
//
//        private static void buildEstimator(Estimator est, Instances instances, int attrIndex, int classIndex,
//                int classValueIndex, boolean isIncremental) throws Exception {
//
//            // DBO.pln("buildEstimator 2 " + classValueIndex);
//
//            // non-incremental estimator add all instances at once
//            if (!isIncremental) {
//
//                if (classValueIndex == -1) {
//                    // DBO.pln("before addValues -- Estimator");
//                    est.addValues(Dataset, attrIndex);
//                } else {
//                    // DBO.pln("before addValues with classvalue -- Estimator");
//                    est.addValues(Dataset, attrIndex, classIndex, classValueIndex);
//                }
//            } else {
//                // incremental estimator, read one value at a time
//                Enumeration enumInsts = (Dataset).enumerateInstances();
//                while (enumInsts.hasMoreElements()) {
//                    Instance instance = (Instance) enumInsts.nextElement();
//                    ((IncrementalEstimator) est).addValue(instance.value(attrIndex), instance.weight());
//                }
//            }
//        }

//        /**
//         * Parses and sets the general options
//         * 
//         * @param build
//         *            contains the data used
//         * @param est
//         *            the estimator used
//         * @param options
//         *            the options from the command line
//         */
//        private static void setGeneralOptions(Builder build, Estimator est, String[] options) throws Exception {
//            Reader input = null;
//
//            // help request option
//            boolean helpRequest = Utils.getFlag('h', options);
//            if (helpRequest) {
//                throw new Exception("Help requested.\n");
//            }
//
//            // instances used
//            String infileName = Utils.getOption('i', options);
//            if (infileName.length() != 0) {
//                input = new BufferedReader(new FileReader(infileName));
//            } else {
//                input = new BufferedReader(new InputStreamReader(System.in));
//            }
//
//            build.m_instances = new Instances(input);
//
//            // attribute index
//            String attrIndex = Utils.getOption('a', options);
//
//            if (attrIndex.length() != 0) {
//                if (attrIndex.equals("first")) {
//                    build.m_attrIndex = 0;
//                } else if (attrIndex.equals("last")) {
//                    build.m_attrIndex = build.m_instances.numAttributes() - 1;
//                } else {
//                    int index = Integer.parseInt(attrIndex) - 1;
//                    if ((index < 0) || (index >= build.m_instances.numAttributes())) {
//                        throw new IllegalArgumentException("Option a: attribute index out of range.");
//                    }
//                    build.m_attrIndex = index;
//
//                }
//            } else {
//                // default is the first attribute
//                build.m_attrIndex = 0;
//            }
//
//            // class index, if not given is set to last attribute
//            String classIndex = Utils.getOption('c', options);
//            if (classIndex.length() == 0)
//                classIndex = "last";
//
//            if (classIndex.length() != 0) {
//                if (classIndex.equals("first")) {
//                    build.m_classIndex = 0;
//                } else if (classIndex.equals("last")) {
//                    build.m_classIndex = build.m_instances.numAttributes() - 1;
//                } else {
//                    int cl = Integer.parseInt(classIndex);
//                    if (cl == -1) {
//                        build.m_classIndex = build.m_instances.numAttributes() - 1;
//                    } else {
//                        build.m_classIndex = cl - 1;
//                    }
//                }
//            }
//
//            // class value index, if not given is set to -1
//            String classValueIndex = Utils.getOption('v', options);
//            if (classValueIndex.length() != 0) {
//                if (classValueIndex.equals("first")) {
//                    build.m_classValueIndex = 0;
//                } else if (classValueIndex.equals("last")) {
//                    build.m_classValueIndex = build.m_instances.numAttributes() - 1;
//                } else {
//                    int cl = Integer.parseInt(classValueIndex);
//                    if (cl == -1) {
//                        build.m_classValueIndex = -1;
//                    } else {
//                        build.m_classValueIndex = cl - 1;
//                    }
//                }
//            }
//
//            build.m_instances.setClassIndex(build.m_classIndex);
//        }
//
//        /**
//         * Creates a deep copy of the given estimator using serialization.
//         * 
//         * @param model
//         *            the estimator to copy
//         * @return a deep copy of the estimator
//         * @exception Exception
//         *                if an error occurs
//         */
//        public static Estimator clone(Estimator model) throws Exception {
//
//            return makeCopy(model);
//        }
//
//        /**
//         * Creates a deep copy of the given estimator using serialization.
//         * 
//         * @param model
//         *            the estimator to copy
//         * @return a deep copy of the estimator
//         * @exception Exception
//         *                if an error occurs
//         */
//        public static Estimator makeCopy(Estimator model) throws Exception {
//
//            return (Estimator) new SerializedObject(model).getObject();
//        }
//
//        /**
//         * Creates a given number of deep copies of the given estimator using
//         * serialization.
//         * 
//         * @param model
//         *            the estimator to copy
//         * @param num
//         *            the number of estimator copies to create.
//         * @return an array of estimators.
//         * @exception Exception
//         *                if an error occurs
//         */
//        public static Estimator[] makeCopies(Estimator model, int num) throws Exception {
//
//            if (model == null) {
//                throw new Exception("No model estimator set");
//            }
//            Estimator[] estimators = new Estimator[num];
//            SerializedObject so = new SerializedObject(model);
//            for (int i = 0; i < estimators.length; i++) {
//                estimators[i] = (Estimator) so.getObject();
//            }
//            return estimators;
//        }

        /**
         * Tests whether the current estimation object is equal to another
         * estimation object
         * 
         * @param obj
         *            the object to compare against
         * @return true if the two objects are equal
         */
        public boolean equals(Object obj) {

            if ((obj == null) || !(obj.getClass().equals(this.getClass()))) {
                return false;
            }
            Estimator cmp = (Estimator) obj;
//            if (m_Debug != cmp.m_Debug)
//                return false;
            if (m_classValueIndex != cmp.m_classValueIndex)
                return false;
            if (m_noClass != cmp.m_noClass)
                return false;

            return true;
        }

        // /**
        // * Returns an enumeration describing the available options.
        // *
        // * @return an enumeration of all the available options.
        // */
        // public Enumeration listOptions() {
        //
        // Vector newVector = new Vector(1);
        //
        // newVector.addElement(new Option(
        // "\tIf set, estimator is run in debug mode and\n"
        // + "\tmay output additional info to the console",
        // "D", 0, "-D"));
        // return newVector.elements();
        // }
        //
        // /**
        // * Parses a given list of options. Valid options are:<p>
        // *
        // * -D <br>
        // * If set, estimator is run in debug mode and
        // * may output additional info to the console.<p>
        // *
        // * @param options the list of options as an array of strings
        // * @exception Exception if an option is not supported
        // */
        // public void setOptions(String[] options) throws Exception {
        //
        // setDebug(Utils.getFlag('D', options));
        // }
        //
        // /**
        // * Gets the current settings of the Estimator.
        // *
        // * @return an array of strings suitable for passing to setOptions
        // */
        // public String [] getOptions() {
        //
        // String [] options;
        // if (getDebug()) {
        // options = new String[1];
        // options[0] = "-D";
        // } else {
        // options = new String[0];
        // }
        // return options;
        // }

        // /**
        // * Creates a new instance of a estimatorr given it's class name and
        // * (optional) arguments to pass to it's setOptions method. If the
        // * classifier implements OptionHandler and the options parameter is
        // * non-null, the classifier will have it's options set.
        // *
        // * @param name the fully qualified class name of the estimatorr
        // * @param options an array of options suitable for passing to
        // setOptions. May
        // * be null.
        // * @return the newly created classifier, ready for use.
        // * @exception Exception if the classifier name is invalid, or the
        // options
        // * supplied are not acceptable to the classifier
        // */
        // public static Estimator forName(String name,
        // String [] options) throws Exception {
        //      
        // return (Estimator)Utils.forName(Estimator.class,
        // name,
        // options);
        // }

        // /**
        // * Set debugging mode.
        // *
        // * @param debug true if debug output should be printed
        // */
        // public void setDebug(boolean debug) {
        //
        // m_Debug = debug;
        // }

        // /**
        // * Get whether debugging is turned on.
        // *
        // * @return true if debugging output is on
        // */
        // public boolean getDebug() {
        //
        // return m_Debug;
        // }

        // /**
        // * Returns the tip text for this property
        // * @return tip text for this property suitable for
        // * displaying in the explorer/experimenter gui
        // */
        // public String debugTipText() {
        // return "If set to true, estimator may output additional info to " +
        // "the console.";
        // }

        // /**
        // * Returns the Capabilities of this Estimator. Derived estimators have
        // to
        // * override this method to enable capabilities.
        // *
        // * @return the capabilities of this object
        // * @see Capabilities
        // */
        // public Capabilities getCapabilities() {
        // Capabilities result = new Capabilities(this);
        //      
        // // class
        // if (!m_noClass) {
        // result.enable(Capability.NOMINAL_CLASS);
        // result.enable(Capability.MISSING_CLASS_VALUES);
        // } else {
        // result.enable(Capability.NO_CLASS);
        // }
        //         
        // return result;
        // }

        /** Hold the counts */
        private double[] m_Counts;

        /** Hold the sum of counts */
        private double m_SumOfCounts;

        /**
         * Constructor
         * 
         * @param numSymbols
         *            the number of possible symbols (remember to include 0)
         * @param laplace
         *            if true, counts will be initialised to 1
         */
        private Estimator(int numSymbols, boolean laplace) {

            m_Counts = new double[numSymbols];
            m_SumOfCounts = 0;
            if (laplace) {
                for (int i = 0; i < numSymbols; i++) {
                    m_Counts[i] = 1;
                }
                m_SumOfCounts = (double) numSymbols;
            }
        }

        /**
         * Constructor
         * 
         * @param nSymbols
         *            the number of possible symbols (remember to include 0)
         * @param fPrior
         *            value with which counts will be initialised
         */
        private Estimator(int nSymbols, double fPrior) {

            m_Counts = new double[nSymbols];
            for (int iSymbol = 0; iSymbol < nSymbols; iSymbol++) {
                m_Counts[iSymbol] = fPrior;
            }
            m_SumOfCounts = fPrior * (double) nSymbols;
        }

        /**
         * Add a new data value to the current estimator.
         * 
         * @param data
         *            the new data value
         * @param weight
         *            the weight assigned to the data value
         */
        private void addValue(double data, double weight) {

            m_Counts[(int) data] += weight;
            m_SumOfCounts += weight;
        }

        /**
         * Get a probability estimate for a value
         * 
         * @param data
         *            the value to estimate the probability of
         * @return the estimated probability of the supplied value
         */
        private double getProbability(double data) {

            if (m_SumOfCounts == 0) {
                return 0;
            }
            return (double) m_Counts[(int) data] / m_SumOfCounts;
        }

        /**
         * Gets the number of symbols this estimator operates with
         * 
         * @return the number of estimator symbols
         */
        private int getNumSymbols() {

            return (m_Counts == null) ? 0 : m_Counts.length;
        }

        /**
         * Get the count for a value
         * 
         * @param data
         *            the value to get the count of
         * @return the count of the supplied value
         */
        private double getCount(double data) {

            if (m_SumOfCounts == 0) {
                return 0;
            }
            return m_Counts[(int) data];
        }

        /**
         * Get the sum of all the counts
         * 
         * @return the total sum of counts
         */
        private double getSumOfCounts() {

            return m_SumOfCounts;
        }

        //    
        // /**
        // * Display a representation of this estimator
        // */
        // public String toString() {
        //      
        // StringBuffer result = new StringBuffer("Discrete Estimator. Counts =
        // ");
        // if (m_SumOfCounts > 1) {
        // for(int i = 0; i < m_Counts.length; i++) {
        // result.append(" ").append(Utils.doubleToString(m_Counts[i], 2));
        // }
        // result.append(" (Total = "
        // ).append(Utils.doubleToString(m_SumOfCounts, 2));
        // result.append(")\n");
        // } else {
        // for(int i = 0; i < m_Counts.length; i++) {
        // result.append(" ").append(m_Counts[i]);
        // }
        // result.append(" (Total = ").append(m_SumOfCounts).append(")\n");
        // }
        // return result.toString();
        // }

        // /**
        // * Returns default capabilities of the classifier.
        // *
        // * @return the capabilities of this classifier
        // */
        // public Capabilities getCapabilities() {
        // Capabilities result = super.getCapabilities();
        //      
        // // attributes
        // result.enable(Capability.NUMERIC_ATTRIBUTES);
        // return result;
        // }
        //    
        // /**
        // * Main method for testing this class.
        // *
        // * @param argv should contain a sequence of integers which
        // * will be treated as symbolic.
        // */
        // public static void main(String [] argv) {
        //      
        // try {
        // if (argv.length == 0) {
        // System.out.println("Please specify a set of instances.");
        // return;
        // }
        // int current = Integer.parseInt(argv[0]);
        // int max = current;
        // for(int i = 1; i < argv.length; i++) {
        // current = Integer.parseInt(argv[i]);
        // if (current > max) {
        // max = current;
        // }
        // }
        // DiscreteEstimator newEst = new DiscreteEstimator(max + 1, true);
        // for(int i = 0; i < argv.length; i++) {
        // current = Integer.parseInt(argv[i]);
        // System.out.println(newEst);
        // System.out.println("Prediction for " + current
        // + " = " + newEst.getProbability(current));
        // newEst.addValue(current, 1);
        // }
        // } catch (Exception e) {
        // System.out.println(e.getMessage());
        // }
        // }
    }

    /** hold the discrete estimators for each cluster */
    private Estimator m_model[][];

    /** hold the normal estimators for each cluster */
    private double m_modelNormal[][][];

    /** default minimum standard deviation */
    private double m_minStdDev = 1e-6;

    private double[] m_minStdDevPerAtt;

    /** hold the weights of each instance for each cluster */
    private double m_weights[][];

    /** the prior probabilities for clusters */
    private double m_priors[];

    /** the loglikelihood of the data */
    private double m_loglikely;

    /** training instances */
    private Dataset m_theInstances = null;

    /** number of clusters selected by the user or cross validation */
    private int m_num_clusters;

    /**
     * the initial number of clusters requested by the user--- -1 if xval is to
     * be used to find the number of clusters
     */
    private int m_initialNumClusters;

    /** number of attributes */
    private int m_num_attribs;

    /** number of training instances */
    private int m_num_instances;

    /** maximum iterations to perform */
    private int m_max_iterations;

    // /** attribute min values */
    // private double [] m_minValues;
    //
    // /** attribute max values */
    // private double [] m_maxValues;

    // /** random number generator */
    // private Random m_rr;

    // /** Verbose? */
    // private boolean m_verbose;

    // /** globally replace missing values */
    // private ReplaceMissingValues m_replaceMissing;

    // /**
    // * Returns a string describing this clusterer
    // * @return a description of the evaluator suitable for
    // * displaying in the explorer/experimenter gui
    // */
    // public String globalInfo() {
    // return
    // "Simple EM (expectation maximisation) class.\n\n"
    // + "EM assigns a probability distribution to each instance which "
    // + "indicates the probability of it belonging to each of the clusters. "
    // + "EM can decide how many clusters to create by cross validation, or you
    // "
    // + "may specify apriori how many clusters to generate.\n\n"
    // + "The cross validation performed to determine the number of clusters "
    // + "is done in the following steps:\n"
    // + "1. the number of clusters is set to 1\n"
    // + "2. the training set is split randomly into 10 folds.\n"
    // + "3. EM is performed 10 times using the 10 folds the usual CV way.\n"
    // + "4. the loglikelihood is averaged over all 10 results.\n"
    // + "5. if loglikelihood has increased the number of clusters is increased
    // "
    // + "by 1 and the program continues at step 2. \n\n"
    // + "The number of folds is fixed to 10, as long as the number of "
    // + "instances in the training set is not smaller 10. If this is the case "
    // + "the number of folds is set equal to the number of instances.";
    // }

    // /**
    // * Returns an enumeration describing the available options.
    // *
    // * @return an enumeration of all the available options.
    // */
    // public Enumeration listOptions () {
    // Vector result = new Vector();
    //    
    // result.addElement(new Option(
    // "\tnumber of clusters. If omitted or -1 specified, then \n"
    // + "\tcross validation is used to select the number of clusters.",
    // "N", 1, "-N <num>"));
    //
    // result.addElement(new Option(
    // "\tmax iterations."
    // + "\n(default 100)",
    // "I", 1, "-I <num>"));
    //    
    // result.addElement(new Option(
    // "\tverbose.",
    // "V", 0, "-V"));
    //    
    // result.addElement(new Option(
    // "\tminimum allowable standard deviation for normal density\n"
    // + "\tcomputation\n"
    // + "\t(default 1e-6)",
    // "M",1,"-M <num>"));
    //
    // Enumeration en = super.listOptions();
    // while (en.hasMoreElements())
    // result.addElement(en.nextElement());
    //    
    // return result.elements();
    // }

    // /**
    // * Parses a given list of options. <p/>
    // *
    // <!-- options-start -->
    // * Valid options are: <p/>
    // *
    // * <pre> -N &lt;num&gt;
    // * number of clusters. If omitted or -1 specified, then
    // * cross validation is used to select the number of clusters.</pre>
    // *
    // * <pre> -I &lt;num&gt;
    // * max iterations.
    // * (default 100)</pre>
    // *
    // * <pre> -V
    // * verbose.</pre>
    // *
    // * <pre> -M &lt;num&gt;
    // * minimum allowable standard deviation for normal density
    // * computation
    // * (default 1e-6)</pre>
    // *
    // * <pre> -S &lt;num&gt;
    // * Random number seed.
    // * (default 1)</pre>
    // *
    // <!-- options-end -->
    // *
    // * @param options the list of options as an array of strings
    // * @throws Exception if an option is not supported
    // */
    // public void setOptions (String[] options)
    // throws Exception {
    // resetOptions();
    // setDebug(Utils.getFlag('V', options));
    // String optionString = Utils.getOption('I', options);
    //
    // if (optionString.length() != 0) {
    // setMaxIterations(Integer.parseInt(optionString));
    // }
    //
    // optionString = Utils.getOption('N', options);
    // if (optionString.length() != 0) {
    // setNumClusters(Integer.parseInt(optionString));
    // }
    //
    // optionString = Utils.getOption('M', options);
    // if (optionString.length() != 0) {
    // setMinStdDev((new Double(optionString)).doubleValue());
    // }
    //    
    // super.setOptions(options);
    // }

    // /**
    // * Returns the tip text for this property
    // * @return tip text for this property suitable for
    // * displaying in the explorer/experimenter gui
    // */
    // public String minStdDevTipText() {
    // return "set minimum allowable standard deviation";
    // }

    // /**
    // * Set the minimum value for standard deviation when calculating
    // * normal density. Reducing this value can help prevent arithmetic
    // * overflow resulting from multiplying large densities (arising from small
    // * standard deviations) when there are many singleton or near singleton
    // * values.
    // * @param m minimum value for standard deviation
    // */
    // public void setMinStdDev(double m) {
    // m_minStdDev = m;
    // }
    //
    // public void setMinStdDevPerAtt(double [] m) {
    // m_minStdDevPerAtt = m;
    // }
    //
    // /**
    // * Get the minimum allowable standard deviation.
    // * @return the minumum allowable standard deviation
    // */
    // public double getMinStdDev() {
    // return m_minStdDev;
    // }

    // /**
    // * Returns the tip text for this property
    // * @return tip text for this property suitable for
    // * displaying in the explorer/experimenter gui
    // */
    // public String numClustersTipText() {
    // return "set number of clusters. -1 to select number of clusters "
    // +"automatically by cross validation.";
    // }

    // /**
    // * Set the number of clusters (-1 to select by CV).
    // *
    // * @param n the number of clusters
    // * @throws Exception if n is 0
    // */
    // public void setNumClusters (int n)
    // throws Exception {
    //    
    // if (n == 0) {
    // throw new Exception("Number of clusters must be > 0. (or -1 to "
    // + "select by cross validation).");
    // }
    //
    // if (n < 0) {
    // m_num_clusters = -1;
    // m_initialNumClusters = -1;
    // }
    // else {
    // m_num_clusters = n;
    // m_initialNumClusters = n;
    // }
    // }

    // /**
    // * Get the number of clusters
    // *
    // * @return the number of clusters.
    // */
    // public int getNumClusters () {
    // return m_initialNumClusters;
    // }
    //
    // /**
    // * Returns the tip text for this property
    // * @return tip text for this property suitable for
    // * displaying in the explorer/experimenter gui
    // */
    // public String maxIterationsTipText() {
    // return "maximum number of iterations";
    // }

    // /**
    // * Set the maximum number of iterations to perform
    // *
    // * @param i the number of iterations
    // * @throws Exception if i is less than 1
    // */
    // public void setMaxIterations (int i)
    // throws Exception {
    // if (i < 1) {
    // throw new Exception("Maximum number of iterations must be > 0!");
    // }
    //
    // m_max_iterations = i;
    // }

    // /**
    // * Get the maximum number of iterations
    // *
    // * @return the number of iterations
    // */
    // public int getMaxIterations () {
    // return m_max_iterations;
    // }
    //
    //
    // /**
    // * Set debug mode - verbose output
    // *
    // * @param v true for verbose output
    // */
    // public void setDebug (boolean v) {
    // m_verbose = v;
    // }

    // /**
    // * Get debug mode
    // *
    // * @return true if debug mode is set
    // */
    // public boolean getDebug () {
    // return m_verbose;
    // }
    //
    //
    // /**
    // * Gets the current settings of EM.
    // *
    // * @return an array of strings suitable for passing to setOptions()
    // */
    // public String[] getOptions () {
    // int i;
    // Vector result;
    // String[] options;
    //
    // result = new Vector();
    //
    // result.add("-I");
    // result.add("" + m_max_iterations);
    // result.add("-N");
    // result.add("" + getNumClusters());
    // result.add("-M");
    // result.add("" + getMinStdDev());
    //
    // options = super.getOptions();
    // for (i = 0; i < options.length; i++)
    // result.add(options[i]);
    //
    // return (String[]) result.toArray(new String[result.size()]);
    // }

    /**
     * Initialise estimators and storage.
     * 
     * @param inst
     *            the instances
     * @throws Exception
     *             if initialization fails
     */
    private void EM_Init(Dataset data)  {
        int j;

        // run k means 10 times and choose best solution
        Dataset[] best = null;
        Instance datasetSTD=DatasetTools.getStandardDeviation(data);
        double bestSqE = Double.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            SimpleKMeans sk = new SimpleKMeans(m_num_clusters, 100);
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
        m_model = new Estimator[m_num_clusters][m_num_attribs];
        m_modelNormal = new double[m_num_clusters][m_num_attribs][3];
        m_priors = new double[m_num_clusters];

        Instance[] centers = new Instance[m_num_clusters];
        int[] clusterSizes = new int[m_num_clusters];
        Instance[] stdD=new Instance[m_num_clusters];
        for (int i = 0; i < best.length; i++) {
            centers[i] = DatasetTools.getCentroid(best[i], new EuclideanDistance());
            clusterSizes[i] = best[i].size();
            DatasetTools.getStandardDeviation(best[i]);
        }
        // Instances centers = bestK.getClusterCentroids();
        
       // Instances stdD = DatasetTools.getStandardDeviation(best)bestK.getClusterStandardDevs();
        // int[][][] nominalCounts = bestK.getClusterNominalCounts();
        // int[] clusterSizes = bestK.getClusterSizes();

        for (int i = 0; i < m_num_clusters; i++) {
            Instance center = centers[i];
            for (j = 0; j < m_num_attribs; j++) {
                // if (inst.attribute(j).isNominal()) {
                // m_model[i][j] = new
                // DiscreteEstimator(m_theInstances.attribute(j).numValues(),
                // true);
                // for (k = 0; k < inst.attribute(j).numValues(); k++) {
                // m_model[i][j].addValue(k, nominalCounts[i][j][k]);
                // }
                // } else {
                double minStdD = (m_minStdDevPerAtt != null) ? m_minStdDevPerAtt[j] : m_minStdDev;
                double mean = center.getValue(j);
                m_modelNormal[i][j][0] = mean;
                // double stdv = (stdD.instance(i).isMissing(j)) ?
                // ((m_maxValues[j] - m_minValues[j]) / (2 * m_num_clusters))
                // : stdD.instance(i).value(j);
                double stdv = stdD[i].getValue(j);
                if (stdv < minStdD) {
                    stdv = datasetSTD.getValue(j);//data.attributeStats(j).numericStats.stdDev;
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

        for (j = 0; j < m_num_clusters; j++) {
            // m_priors[j] += 1.0;
            m_priors[j] = clusterSizes[j];
        }
        normalize(m_priors);
    }
    /**
     * Normalizes the doubles in the array by their sum.
     *
     * @param doubles the array of double
     * @exception IllegalArgumentException if sum is Zero or NaN
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
     * @param doubles the array of double
     * @param sum the value by which the doubles are to be normalized
     * @exception IllegalArgumentException if sum is zero or NaN
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
        // System.err.println("x: "+x+" mean: "+mean+" diff: "+diff+" stdv:
        // "+stdDev);
        // System.err.println("diff*diff/(2*stdv*stdv): "+ (diff * diff / (2 *
        // stdDev * stdDev)));

        return -(diff * diff / (2 * stdDev * stdDev)) - m_normConst - Math.log(stdDev);
    }

    /**
     * New probability estimators for an iteration
     */
    private void new_estimators() {
        for (int i = 0; i < m_num_clusters; i++) {
            for (int j = 0; j < m_num_attribs; j++) {
//                if (m_theInstances.attribute(j).isNominal()) {
//                    m_model[i][j] = new DiscreteEstimator(m_theInstances.attribute(j).numValues(), true);
//                } else {
                    m_modelNormal[i][j][0] = m_modelNormal[i][j][1] = m_modelNormal[i][j][2] = 0.0;
               // }
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
        Instance stdD=DatasetTools.getStandardDeviation(inst);
        for (i = 0; i < m_num_clusters; i++) {
            for (j = 0; j < m_num_attribs; j++) {
                for (l = 0; l < inst.size(); l++) {
                    Instance in = inst.getInstance(l);
                   // if (!in.isMissing(j)) {
//                        if (inst.attribute(j).isNominal()) {
//                            m_model[i][j].addValue(in.value(j), in.weight() * m_weights[l][i]);
//                        } else {
                            m_modelNormal[i][j][0] += (in.getValue(j) * in.getWeight() * m_weights[l][i]);
                            m_modelNormal[i][j][2] += in.getWeight() * m_weights[l][i];
                            m_modelNormal[i][j][1] += (in.getValue(j) * in.getValue(j) * in.getWeight() * m_weights[l][i]);
                        //}
                   // }
                }
            }
        }

        // calcualte mean and std deviation for numeric attributes
        for (j = 0; j < m_num_attribs; j++) {
            //if (!inst.attribute(j).isNominal()) {
                for (i = 0; i < m_num_clusters; i++) {
                    if (m_modelNormal[i][j][2] <= 0) {
                        m_modelNormal[i][j][1] = Double.MAX_VALUE;
                        // m_modelNormal[i][j][0] = 0;
                        m_modelNormal[i][j][0] = m_minStdDev;
                    } else {

                        // variance
                        m_modelNormal[i][j][1] = (m_modelNormal[i][j][1] - (m_modelNormal[i][j][0]
                                * m_modelNormal[i][j][0] / m_modelNormal[i][j][2]))
                                / (m_modelNormal[i][j][2]);

                        if (m_modelNormal[i][j][1] < 0) {
                            m_modelNormal[i][j][1] = 0;
                        }

                        // std dev
                        double minStdD = (m_minStdDevPerAtt != null) ? m_minStdDevPerAtt[j] : m_minStdDev;

                        m_modelNormal[i][j][1] = Math.sqrt(m_modelNormal[i][j][1]);

                        if ((m_modelNormal[i][j][1] <= minStdD)) {
                            m_modelNormal[i][j][1] = stdD.getValue(j);//inst.attributeStats(j).numericStats.stdDev;
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
            //}
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
     * @param instance the instance to compute the density for
     * @return the density.
     * @exception Exception if the density could not be computed successfully
     */
    private double logDensityForInstance(Instance instance) {

      double[] a = logJointDensitiesForInstance(instance);
      double max = a[maxIndex(a)];
      double sum = 0.0;

      for(int i = 0; i < a.length; i++) {
        sum += Math.exp(a[i] - max);
      }

      return max + Math.log(sum);
    }
    /**
     * Returns the cluster probability distribution for an instance.
     *
     * @param instance the instance to be clustered
     * @return the probability distribution
     * @throws Exception if computation fails
     */  
    private double[] distributionForInstance(Instance instance) {
      
      return logs2probs(logJointDensitiesForInstance(instance));
    }
    
    /**
     * Returns index of maximum element in a given
     * array of doubles. First maximum is returned.
     *
     * @param doubles the array of doubles
     * @return the index of the maximum element
     */
    private static /*@pure@*/ int maxIndex(double [] doubles) {

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
     * Converts an array containing the natural logarithms of
     * probabilities stored in a vector back into probabilities.
     * The probabilities are assumed to sum to one.
     *
     * @param a an array holding the natural logarithms of the probabilities
     * @return the converted array 
     */
    private static double[] logs2probs(double[] a) {

      double max = a[maxIndex(a)];
      double sum = 0.0;

      double[] result = new double[a.length];
      for(int i = 0; i < a.length; i++) {
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
     * @param inst the instance 
     * @return the array of values
     * @exception Exception if values could not be computed
     */
    private double[] logJointDensitiesForInstance(Instance inst)
      {

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
//    /**
//     * Constructor.
//     * 
//     */
//    public EM() {
//        super();
//
//        m_SeedDefault = 100;
//        resetOptions();
//    }

    // /**
    // * Reset to default options
    // */
    // protected void resetOptions () {
    // m_minStdDev = 1e-6;
    // m_max_iterations = 100;
    // m_Seed = m_SeedDefault;
    // m_num_clusters = -1;
    // m_initialNumClusters = -1;
    // m_verbose = false;
    // }

    // /**
    // * Return the normal distributions for the cluster models
    // *
    // * @return a <code>double[][][]</code> value
    // */
    // public double [][][] getClusterModelsNumericAtts() {
    // return m_modelNormal;
    // }
    //
    // /**
    // * Return the priors for the clusters
    // *
    // * @return a <code>double[]</code> value
    // */
    // public double [] getClusterPriors() {
    // return m_priors;
    // }
    //
    // /**
    // * Outputs the generated clusters into a string.
    // *
    // * @return the clusterer in string representation
    // */
    // public String toString () {
    // if (m_priors == null) {
    // return "No clusterer built yet!";
    // }
    // StringBuffer text = new StringBuffer();
    // text.append("\nEM\n==\n");
    // if (m_initialNumClusters == -1) {
    // text.append("\nNumber of clusters selected by cross validation: "
    // +m_num_clusters+"\n");
    // } else {
    // text.append("\nNumber of clusters: " + m_num_clusters + "\n");
    // }
    //
    // for (int j = 0; j < m_num_clusters; j++) {
    // text.append("\nCluster: " + j + " Prior probability: "
    // + Utils.doubleToString(m_priors[j], 4) + "\n\n");
    //
    // for (int i = 0; i < m_num_attribs; i++) {
    // text.append("Attribute: " + m_theInstances.attribute(i).name() + "\n");
    //
    // if (m_theInstances.attribute(i).isNominal()) {
    // if (m_model[j][i] != null) {
    // text.append(m_model[j][i].toString());
    // }
    // }
    // else {
    // text.append("Normal Distribution. Mean = "
    // + Utils.doubleToString(m_modelNormal[j][i][0], 4)
    // + " StdDev = "
    // + Utils.doubleToString(m_modelNormal[j][i][1], 4)
    // + "\n");
    // }
    // }
    // }
    //
    // return text.toString();
    // }

    // /**
    // * verbose output for debugging
    // * @param inst the training instances
    // */
    // private void EM_Report (Dataset inst) {
    // int i, j, l, m;
    // System.out.println("======================================");
    //
    // for (j = 0; j < m_num_clusters; j++) {
    // for (i = 0; i < m_num_attribs; i++) {
    // System.out.println("Clust: " + j + " att: " + i + "\n");
    //
    // if (m_theInstances.attribute(i).isNominal()) {
    // if (m_model[j][i] != null) {
    // System.out.println(m_model[j][i].toString());
    // }
    // }
    // else {
    // System.out.println("Normal Distribution. Mean = "
    // + Utils.doubleToString(m_modelNormal[j][i][0]
    // , 8, 4)
    // + " StandardDev = "
    // + Utils.doubleToString(m_modelNormal[j][i][1]
    // , 8, 4)
    // + " WeightSum = "
    // + Utils.doubleToString(m_modelNormal[j][i][2]
    // , 8, 4));
    // }
    // }
    // }
    //    
    // for (l = 0; l < inst.numInstances(); l++) {
    // m = Utils.maxIndex(m_weights[l]);
    // System.out.print("Inst " + Utils.doubleToString((double)l, 5, 0)
    // + " Class " + m + "\t");
    // for (j = 0; j < m_num_clusters; j++) {
    // System.out.print(Utils.doubleToString(m_weights[l][j], 7, 5) + " ");
    // }
    // System.out.println();
    // }
    // }

//    /**
//     * estimate the number of clusters by cross validation on the training data.
//     * 
//     * @throws Exception
//     *             if something goes wrong
//     */
//    private void CVClusters()  {
//        double CVLogLikely = -Double.MAX_VALUE;
//        double templl, tll;
//        boolean CVincreased = true;
//        m_num_clusters = 1;
//        int num_clusters = m_num_clusters;
//        int i;
//        Random cvr;
//        Dataset trainCopy;
//        int numFolds = (m_theInstances.size() < 10) ? m_theInstances.size() : 10;
//
//        boolean ok = true;
//        //int seed = getSeed();
//        int restartCount = 0;
//        CLUSTER_SEARCH: while (CVincreased) {
//            // theInstances.stratify(10);
//
//            CVincreased = false;
//           // cvr = new Random(getSeed());
//            trainCopy = new SimpleDataset(m_theInstances);
//           // trainCopy.randomize(cvr);
//            templl = 0.0;
//            for (i = 0; i < numFolds; i++) {
//                Dataset cvTrain = trainCopy.trainCV(numFolds, i, cvr);
//                if (num_clusters > cvTrain.numInstances()) {
//                    break CLUSTER_SEARCH;
//                }
//                Dataset cvTest = trainCopy.testCV(numFolds, i);
////                m_rr = new Random(seed);
////                for (int z = 0; z < 10; z++)
////                    m_rr.nextDouble();
//                m_num_clusters = num_clusters;
//                EM_Init(cvTrain);
//                try {
//                    iterate(cvTrain, false);
//                } catch (Exception ex) {
//                    // catch any problems - i.e. empty clusters occuring
//                    ex.printStackTrace();
//                    // System.err.println("Restarting after CV training failure
//                    // ("+num_clusters+" clusters");
//                    seed++;
//                    restartCount++;
//                    ok = false;
//                    if (restartCount > 5) {
//                        break CLUSTER_SEARCH;
//                    }
//                    break;
//                }
//                try {
//                    tll = E(cvTest, false);
//                } catch (Exception ex) {
//                    // catch any problems - i.e. empty clusters occuring
//                    // ex.printStackTrace();
//                    ex.printStackTrace();
//                    // System.err.println("Restarting after CV testing failure
//                    // ("+num_clusters+" clusters");
//                    // throw new Exception(ex);
//                    seed++;
//                    restartCount++;
//                    ok = false;
//                    if (restartCount > 5) {
//                        break CLUSTER_SEARCH;
//                    }
//                    break;
//                }
//
////                if (m_verbose) {
////                    System.out.println("# clust: " + num_clusters + " Fold: " + i + " Loglikely: " + tll);
////                }
//                templl += tll;
//            }
//
//            if (ok) {
//                restartCount = 0;
//                //seed = getSeed();
//                templl /= (double) numFolds;
//
////                if (m_verbose) {
////                    System.out.println("===================================" + "==============\n# clust: "
////                            + num_clusters + " Mean Loglikely: " + templl + "\n================================"
////                            + "=================");
////                }
//
//                if (templl > CVLogLikely) {
//                    CVLogLikely = templl;
//                    CVincreased = true;
//                    num_clusters++;
//                }
//            }
//        }
//
//        if (m_verbose) {
//            System.out.println("Number of clusters: " + (num_clusters - 1));
//        }
//
//        m_num_clusters = num_clusters - 1;
//    }

    // /**
    // * Returns the number of clusters.
    // *
    // * @return the number of clusters generated for a training dataset.
    // * @throws Exception if number of clusters could not be returned
    // * successfully
    // */
    // public int numberOfClusters ()
    // throws Exception {
    // if (m_num_clusters == -1) {
    // throw new Exception("Haven't generated any clusters!");
    // }
    //
    // return m_num_clusters;
    // }

//    /**
//     * Updates the minimum and maximum values for all the attributes based on a
//     * new instance.
//     * 
//     * @param instance
//     *            the new instance
//     */
//    private void updateMinMax(Instance instance) {
//
//        for (int j = 0; j < m_theInstances.numAttributes(); j++) {
//            if (!instance.isMissing(j)) {
//                if (Double.isNaN(m_minValues[j])) {
//                    m_minValues[j] = instance.value(j);
//                    m_maxValues[j] = instance.value(j);
//                } else {
//                    if (instance.value(j) < m_minValues[j]) {
//                        m_minValues[j] = instance.value(j);
//                    } else {
//                        if (instance.value(j) > m_maxValues[j]) {
//                            m_maxValues[j] = instance.value(j);
//                        }
//                    }
//                }
//            }
//        }
//    }

    // /**
    // * Returns default capabilities of the clusterer (i.e., the ones of
    // * SimpleKMeans).
    // *
    // * @return the capabilities of this clusterer
    // */
    // public Capabilities getCapabilities() {
    // Capabilities result = new SimpleKMeans().getCapabilities();
    // result.setOwner(this);
    // return result;
    // }

    // /**
    // * Generates a clusterer. Has to initialize all fields of the clusterer
    // * that are not being set via options.
    // *
    // * @param data set of instances serving as training data
    // * @throws Exception if the clusterer has not been
    // * generated successfully
    // */
    // public void buildClusterer (Dataset data)
    // throws Exception {
    //    
    //    
    // }

    // /**
    // * Returns the cluster priors.
    // *
    // * @return the cluster priors
    // */
    // private double[] clusterPriors() {
    //
    // double[] n = new double[m_priors.length];
    //  
    // System.arraycopy(m_priors, 0, n, 0, n.length);
    // return n;
    // }

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

//        m_replaceMissing.input(inst);
//        inst = m_replaceMissing.output();

        for (i = 0; i < m_num_clusters; i++) {
            // System.err.println("Cluster : "+i);
            logprob = 0.0;

            for (j = 0; j < m_num_attribs; j++) {
                //if (!inst.isMissing(j)) {
//                    if (inst.attribute(j).isNominal()) {
//                        logprob += Math.log(m_model[i][j].getProbability(inst.value(j)));
//                    } else { // numeric attribute
                        logprob += logNormalDens(inst.getValue(j), m_modelNormal[i][j][0], m_modelNormal[i][j][1]);
                        /*
                         * System.err.println(logNormalDens(inst.value(j),
                         * m_modelNormal[i][j][0], m_modelNormal[i][j][1]) + "
                         * ");
                         */
                    //}
               // }
            }
            // System.err.println("");

            wghts[i] = logprob;
        }
        return wghts;
    }

    /**
     * Perform the EM algorithm
     * 
     * @throws Exception
     *             if something goes wrong
     */
    private void doEM() {

        // if (m_verbose) {
        // System.out.println("Seed: " + getSeed());
        // }

      //  Random rg = new Random(System.currentTimeMillis());

        // // throw away numbers to avoid problem of similar initial numbers
        // // from a similar seed
        // for (int i = 0; i < 10; i++)
        // rg.nextDouble();

        m_num_instances = m_theInstances.size();// .numInstances();
        m_num_attribs = m_theInstances.getInstance(0).size();// .numAttributes();

        // if (m_verbose) {
        // System.out.println("Number of instances: " + m_num_instances +
        // "\nNumber of atts: " + m_num_attribs + "\n");
        // }

//        // setDefaultStdDevs(theInstances);
//        // cross validate to determine number of clusters?
//        if (m_initialNumClusters == -1) {
//            if (m_theInstances.size() > 9) {
//                CVClusters();
//                // m_rr = new Random(getSeed());
//
//            } else {
//                m_num_clusters = 1;
//            }
//        }

        // fit full training set
        EM_Init(m_theInstances);
        m_loglikely = iterate(m_theInstances);
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

        // if (report) {
        // EM_Report(inst);
        // }

        boolean ok = false;
        // int seed = getSeed();
        int restartCount = 0;
        while (!ok) {
            try {
                for (i = 0; i < m_max_iterations; i++) {
                    llkold = llk;
                    llk = E(inst, true);

                    // if (report) {
                    // System.out.println("Loglikely: " + llk);
                    // }

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
                // m_rr = new Random(seed);
                // for (int z = 0; z < 10; z++) {
                // m_rr.nextDouble();
                // m_rr.nextInt();
                // }
                // if (restartCount > 5) {
                // // System.err.println("Reducing the number of clusters");
                // m_num_clusters--;
                // restartCount = 0;
                // }
                EM_Init(m_theInstances);
            }
        }

        // if (report) {
        // EM_Report(inst);
        // }

        return llk;
    }

    // // ============
    // // Test method.
    // // ============
    // /**
    // * Main method for testing this class.
    // *
    // * @param argv should contain the following arguments: <p>
    // * -t training file [-T test file] [-N number of clusters] [-S random
    // seed]
    // */
    // public static void main (String[] argv) {
    // runClusterer(new EM(), argv);
    // }

    public Dataset[] executeClustering(Dataset data) {
        // can clusterer handle the data?
        m_theInstances = data;
this.m_num_clusters=4;
        // calculate min and max values for attributes
        // m_minValues = new double[m_theInstances.numAttributes()];
        // m_maxValues = new double[m_theInstances.numAttributes()];
        // for (int i = 0; i < m_theInstances.numAttributes(); i++) {
        // m_minValues[i] = m_maxValues[i] = Double.NaN;
        // }
        // for (int i = 0; i < m_theInstances.numInstances(); i++) {
        // updateMinMax(m_theInstances.instance(i));
        // }

        doEM();
        // TODO export clusters
        
        for(int i=0;i<data.size();i++){
            System.out.println("Prob: "+Arrays.toString(distributionForInstance(data.getInstance(i))));
        }
        return null;
    }

    /**
     * Contains static utility functions for Estimators.
     * <p>
     * 
     * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
     * @version $Revision: 1.2 $
     */
    private static class EstimatorUtils {

        // /**
        // * Find the minimum distance between values
        // *
        // * @param inst
        // * sorted instances, sorted
        // * @param attrIndex
        // * index of the attribute, they are sorted after
        // * @return the minimal distance
        // */
        // private static double findMinDistance(Dataset inst, int attrIndex) {
        // double min = Double.MAX_VALUE;
        // int numInst = inst.size().numInstances();
        // double diff;
        // if (numInst < 2)
        // return min;
        // int begin = -1;
        // Instance instance = null;
        // do {
        // begin++;
        // if (begin < numInst) {
        // instance = inst.instance(begin);
        // }
        // } while (begin < numInst && instance.isMissing(attrIndex));
        //
        // double secondValue = inst.instance(begin).value(attrIndex);
        // for (int i = begin; i < numInst &&
        // !inst.instance(i).isMissing(attrIndex); i++) {
        // double firstValue = secondValue;
        // secondValue = inst.instance(i).value(attrIndex);
        // if (secondValue != firstValue) {
        // diff = secondValue - firstValue;
        // if (diff < min && diff > 0.0) {
        // min = diff;
        // }
        // }
        // }
        // return min;
        // }

        /**
         * Find the minimum and the maximum of the attribute and return it in
         * the last parameter..
         * 
         * @param inst
         *            instances used to build the estimator
         * @param attrIndex
         *            index of the attribute
         * @param minMax
         *            the array to return minimum and maximum in
         * @return number of not missing values
         * @exception Exception
         *                if parameter minMax wasn't initialized properly
         */
        private static void getMinMax(Dataset data, int attrIndex, double[] minMax) {
           
            minMax[0] = data.getMinimumInstance().getValue(attrIndex);
            minMax[1] = data.getMaximumInstance().getValue(attrIndex);

            // Enumeration enumInst = inst.enumerateInstances();
            // if (enumInst.hasMoreElements()) {
            // do {
            // instance = (Instance) enumInst.nextElement();
            // } while (instance.isMissing(attrIndex) &&
            // (enumInst.hasMoreElements()));
            //      
            // // add values if not missing
            // if (!instance.isMissing(attrIndex)) {
            // numNotMissing++;
            // min = instance.value(attrIndex);
            // max = instance.value(attrIndex);
            // }
            // while (enumInst.hasMoreElements()) {
            // instance = (Instance) enumInst.nextElement();
            // if (!instance.isMissing(attrIndex)) {
            // numNotMissing++;
            // if (instance.value(attrIndex) < min) {
            // min = (instance.value(attrIndex));
            // } else {
            // if (instance.value(attrIndex) > max) {
            // max = (instance.value(attrIndex));
            // }
            // }
            // }
            // }
            // }
            // minMax[0] = min;
            // minMax[1] = max;
            // return numNotMissing;
        }

        // /**
        // * Returns a dataset that contains all instances of a certain class
        // * value.
        // *
        // * @param data
        // * dataset to select the instances from
        // * @param attrIndex
        // * index of the relevant attribute
        // * @param classIndex
        // * index of the class attribute
        // * @param classValue
        // * the relevant class value
        // * @return a dataset with only
        // */
        // private static Vector getInstancesFromClass(Instances data, int
        // attrIndex, int classIndex, double classValue,
        // Instances workData) {
        // // Oops.pln("getInstancesFromClass classValue"+classValue+"
        // // workData"+data.numInstances());
        // Vector dataPlusInfo = new Vector(0);
        // int num = 0;
        // int numClassValue = 0;
        // // workData = new Instances(data, 0);
        // for (int i = 0; i < data.numInstances(); i++) {
        // if (!data.instance(i).isMissing(attrIndex)) {
        // num++;
        // if (data.instance(i).value(classIndex) == classValue) {
        // workData.add(data.instance(i));
        // numClassValue++;
        // }
        // }
        // }
        //
        // Double alphaFactor = new Double((double) numClassValue / (double)
        // num);
        // dataPlusInfo.add(workData);
        // dataPlusInfo.add(alphaFactor);
        // return dataPlusInfo;
        // }

        // /**
        // * Returns a dataset that contains of all instances of a certain class
        // * value.
        // *
        // * @param data
        // * dataset to select the instances from
        // * @param classIndex
        // * index of the class attribute
        // * @param classValue
        // * the class value
        // * @return a dataset with only instances of one class value
        // */
        // private static Instances getInstancesFromClass(Instances data, int
        // classIndex, double classValue) {
        // Instances workData = new Instances(data, 0);
        // for (int i = 0; i < data.numInstances(); i++) {
        // if (data.instance(i).value(classIndex) == classValue) {
        // workData.add(data.instance(i));
        // }
        //
        // }
        // return workData;
        // }

        // /**
        // * Output of an n points of a density curve. Filename is parameter f +
        // * ".curv".
        // *
        // * @param f
        // * string to build filename
        // * @param est
        // * @param min
        // * @param max
        // * @param numPoints
        // * @throws Exception
        // * if something goes wrong
        // */
        // private static void writeCurve(String f, Estimator est, double min,
        // double max, int numPoints) throws Exception {
        //
        // PrintWriter output = null;
        // StringBuffer text = new StringBuffer("");
        //
        // if (f.length() != 0) {
        // // add attribute indexnumber to filename and extension .hist
        // String name = f + ".curv";
        // output = new PrintWriter(new FileOutputStream(name));
        // } else {
        // return;
        // }
        //
        // double diff = (max - min) / ((double) numPoints - 1.0);
        // try {
        // text.append("" + min + " " + est.getProbability(min) + " \n");
        //
        // for (double value = min + diff; value < max; value += diff) {
        // text.append("" + value + " " + est.getProbability(value) + " \n");
        // }
        // text.append("" + max + " " + est.getProbability(max) + " \n");
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // System.out.println(ex.getMessage());
        // }
        // output.println(text.toString());
        //
        // // close output
        // if (output != null) {
        // output.close();
        // }
        // }

        // /**
        // * Output of an n points of a density curve.
        // * Filename is parameter f + ".curv".
        // *
        // * @param f string to build filename
        // * @param est
        // * @param classEst
        // * @param classIndex
        // * @param min
        // * @param max
        // * @param numPoints
        // * @throws Exception if something goes wrong
        // */
        // private static void writeCurve(String f, Estimator est,
        // Estimator classEst,
        // double classIndex,
        // double min, double max,
        // int numPoints) throws Exception {
        //
        // PrintWriter output = null;
        // StringBuffer text = new StringBuffer("");
        //    
        // if (f.length() != 0) {
        // // add attribute indexnumber to filename and extension .hist
        // String name = f + ".curv";
        // output = new PrintWriter(new FileOutputStream(name));
        // } else {
        // return;
        // }
        //
        // double diff = (max - min) / ((double)numPoints - 1.0);
        // try {
        // text.append("" + min + " " +
        // est.getProbability(min) * classEst.getProbability(classIndex)
        // + " \n");
        //
        // for (double value = min + diff; value < max; value += diff) {
        // text.append("" + value + " " +
        // est.getProbability(value) * classEst.getProbability(classIndex)
        // + " \n");
        // }
        // text.append("" + max + " " +
        // est.getProbability(max) * classEst.getProbability(classIndex)
        // + " \n");
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // System.out.println(ex.getMessage());
        // }
        // output.println(text.toString());
        //
        // // close output
        // if (output != null) {
        // output.close();
        // }
        // }

        // /**
        // * Returns a dataset that contains of all instances of a certain value
        // * for the given attribute.
        // *
        // * @param data
        // * dataset to select the instances from
        // * @param index
        // * the index of the attribute
        // * @param v
        // * the value
        // * @return a subdataset with only instances of one value for the
        // * attribute
        // */
        // private static Instances getInstancesFromValue(Instances data, int
        // index, double v) {
        // Instances workData = new Instances(data, 0);
        // for (int i = 0; i < data.numInstances(); i++) {
        // if (data.instance(i).value(index) == v) {
        // workData.add(data.instance(i));
        // }
        // }
        // return workData;
        // }

        // /**
        // * Returns a string representing the cutpoints
        // */
        // public static String cutpointsToString(double [] cutPoints, boolean
        // [] cutAndLeft) {
        // StringBuffer text = new StringBuffer("");
        // if (cutPoints == null) {
        // text.append("\n# no cutpoints found - attribute \n");
        // } else {
        // text.append("\n#* "+cutPoints.length+" cutpoint(s) -\n");
        // for (int i = 0; i < cutPoints.length; i++) {
        // text.append("# "+cutPoints[i]+" ");
        // text.append(""+cutAndLeft[i]+"\n");
        // }
        // text.append("# end\n");
        // }
        // return text.toString();
        // }

    }
}
