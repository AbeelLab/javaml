/**
 * PrincipalComponentsAnalysis.java
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
 * Copyright (c) 2000, Mark Hall
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.filter;

import gov.nist.math.jama.EigenvalueDecomposition;
import gov.nist.math.jama.Matrix;


import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import nz.ac.waikato.cs.weka.Utils;

/**
 * Performs a principal components analysis and transformation of the data. Use
 * in conjunction with a Ranker search. Dimensionality reduction is accomplished
 * by choosing enough eigenvectors to account for some percentage of the
 * variance in the original data---default 0.95 (95%). Attribute noise can be
 * filtered by transforming to the PC space, eliminating some of the worst
 * eigenvectors, and then transforming back to the original space. <p/>
 * 
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author Gabi Schmidberger (gabi@cs.waikato.ac.nz)
 * @author Thomas Abeel
 * 
 */
public class PrincipalComponentsAnalysis implements Filter {

    /** Number of attributes */
    private int m_numAttribs;

    /** Number of instances */
    private int m_numInstances;

    /** Correlation matrix for the original data */
    private double[][] m_correlation;

    /**
     * Will hold the unordered linear transformations of the (normalized)
     * original data
     */
    private double[][] m_eigenvectors;

    /** Eigenvalues for the corresponding eigenvectors */
    private double[] m_eigenvalues = null;

    /** Sorted eigenvalues */
    private int[] m_sortedEigens;

    /** sum of the eigenvalues */
    private double m_sumOfEigenValues = 0.0;

    /**
     * The amount of variance to cover in the original data when retaining the
     * best principal components.
     */
    private double m_coverVariance = 1.0;

    /**
     * Holds the transposed eigenvectors for converting back to the original
     * space
     */
    private double[][] m_eTranspose;

    public PrincipalComponentsAnalysis() {
        this(0.95);
    }

    public PrincipalComponentsAnalysis(double m_cover) {
        this(m_cover, false);

    }

    private boolean converBackToOriginalSpace = false;

    private int maxComponents = -1;

    public PrincipalComponentsAnalysis(double m_cover, boolean convertBack,int maxComponents) {
        m_coverVariance = m_cover;
        converBackToOriginalSpace = convertBack;
        this.maxComponents = maxComponents;

    }

    public PrincipalComponentsAnalysis(double m_cover, boolean convertBack) {
       this(m_cover,convertBack,-1);
    }

    private RemoveAttributes remAtt;

    /**
     * Initializes principal components and performs the analysis
     * 
     * @param data
     *            the instances to analyse/transform
     * @throws Exception
     *             if analysis fails
     */
    public Dataset filterDataset(Dataset data) {
        // can evaluator handle data?
        // getCapabilities().testWithFail(data);

        m_eigenvalues = null;
        // m_outputNumAtts = -1;
        m_sumOfEigenValues = 0.0;

        // delete any attributes with only one distinct value or are all missing
        Vector<Integer> deleteCols = new Vector<Integer>();
        for (int i = 0; i < data.getInstance(0).size(); i++) {
            if (numDistinctValues(i, data) <= 1) {
                deleteCols.addElement(i);
            }
        }
        // remove columns from the data if necessary
        Dataset m_trainInstances = data;
        if (deleteCols.size() > 0) {

            int[] todelete = new int[deleteCols.size()];
            for (int i = 0; i < deleteCols.size(); i++) {
                todelete[i] = deleteCols.get(i);
            }
            remAtt = new RemoveAttributes(todelete);
            m_trainInstances = remAtt.filterDataset(data);
        }

        m_numInstances = m_trainInstances.size();
        m_numAttribs = m_trainInstances.getInstance(0).size();

        fillCorrelation(m_trainInstances);

        double[] d = new double[m_numAttribs];
        double[][] V = new double[m_numAttribs][m_numAttribs];

        Matrix corr = new Matrix(m_correlation);
        // perform eigenvalue decomposition
        EigenvalueDecomposition eig = new EigenvalueDecomposition(corr);
        Matrix v = eig.getV();
        double[] d2 = eig.getRealEigenvalues();
        // transfer data
        int nr = v.getRowDimension();
        int nc = v.getColumnDimension();
        for (int i = 0; i < nr; i++)
            for (int j = 0; j < nc; j++)
                V[i][j] = v.get(i, j);

        for (int i = 0; i < d2.length; i++)
            d[i] = d2[i];

        m_eigenvectors = (double[][]) V.clone();
        m_eigenvalues = (double[]) d.clone();

        // any eigenvalues less than 0 are not worth anything --- change to 0
        for (int i = 0; i < m_eigenvalues.length; i++) {
            if (m_eigenvalues[i] < 0) {
                m_eigenvalues[i] = 0.0;
            }
        }

        m_sortedEigens = Utils.sort(m_eigenvalues);
        m_sumOfEigenValues = Utils.sum(m_eigenvalues);

        double[][] orderedVectors = new double[m_eigenvectors.length][m_eigenvectors[0].length];

        // sort eigenvector according to their eigenvalues
        for (int i = m_numAttribs; i > (m_numAttribs - m_eigenvectors[0].length); i--) {
            for (int j = 0; j < m_numAttribs; j++) {
                orderedVectors[j][m_numAttribs - i] = m_eigenvectors[j][m_sortedEigens[i - 1]];
            }
        }

        // transpose the matrix for unfiltering of instances.
        nr = orderedVectors.length;
        nc = orderedVectors[0].length;
        m_eTranspose = new double[nc][nr];
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nc; j++) {
                m_eTranspose[i][j] = orderedVectors[j][i];
            }
        }

        Dataset out = new SimpleDataset();
        for (int k = 0; k < data.size(); k++) {
            // we need to use the original data as the call to filterInstance
            // will strip the unneeded attributes as defined in remAtt
            Instance tempInst = data.getInstance(k);
            if (!converBackToOriginalSpace) {
                out.addInstance(filterInstance(tempInst));
            } else {
               out.addInstance(unfilterInstance(filterInstance(tempInst)));
            }
        }
        return out;
    }

    private int numDistinctValues(int index, Dataset data) {
        Set<Float> set = new HashSet<Float>();
        for (int i = 0; i < data.size(); i++) {
            set.add(data.getInstance(i).getValue(index));
        }
        return set.size();
    }

    /**
     * Fill the correlation matrix
     */
    private void fillCorrelation(Dataset m_trainInstances) {
        m_correlation = new double[m_numAttribs][m_numAttribs];
        double[] att1 = new double[m_numInstances];
        double[] att2 = new double[m_numInstances];
        double corr;

        for (int i = 0; i < m_numAttribs; i++) {
            for (int j = 0; j < m_numAttribs; j++) {
                if (i == j) {
                    m_correlation[i][j] = 1.0;
                } else {
                    for (int k = 0; k < m_numInstances; k++) {
                        att1[k] = m_trainInstances.getInstance(k).getValue(i);
                        att2[k] = m_trainInstances.getInstance(k).getValue(j);
                    }
                    corr = Utils.correlation(att1, att2, m_numInstances);
                    m_correlation[i][j] = corr;
                    m_correlation[j][i] = corr;
                }
            }
        }
    }

    public Instance filterInstance(Instance instance) {
        int numComponents = m_numAttribs;
        if (maxComponents > 0) {
            numComponents = maxComponents;
        }
        if (remAtt != null)
            instance = remAtt.filterInstance(instance);
        float[] newVals;

        newVals = new float[numComponents];

        double cumulative = 0;
        for (int i = numComponents - 1; i >= 0; i--) {
            double tempval = 0.0;
            for (int j = 0; j < numComponents; j++) {
                tempval += (m_eigenvectors[j][m_sortedEigens[i]] * instance.getValue(j));
            }
            newVals[numComponents - i - 1] = (float) tempval;
            cumulative += m_eigenvalues[m_sortedEigens[i]];
            if ((cumulative / m_sumOfEigenValues) > m_coverVariance) {
                break;
            }
        }
        return new SimpleInstance(newVals, instance.getWeight(), instance.isClassSet(), instance.getClassValue());
    }

    public Instance unfilterInstance(Instance instance) {
        float[] newVals = new float[m_numAttribs];
        for (int i = 0; i < m_eTranspose[0].length; i++) {
            float tempval = 0;
            for (int j = 0; j < m_eTranspose.length &&j<instance.size(); j++) {
                tempval += (m_eTranspose[j][i] * instance.getValue(j));
            }
            newVals[i] = tempval;
        }
        return new SimpleInstance(newVals, instance.getWeight(), instance.isClassSet(), instance.getClassValue());
    }

    public double[] getEigenValues() {
        return m_eigenvalues;
    }
    public double[][]getEigenVectors(){
        return m_eigenvectors;
    }
}
