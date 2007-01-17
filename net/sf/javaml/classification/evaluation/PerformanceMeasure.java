/**
 * PerformanceMeasure.java, 17-jan-2007
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
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.classification.evaluation;

/**
 * Class implementing several performance measures commonly used for
 * classification algorithms. Each of these measure can be calculated from four
 * basic values from the classifier. The number of true positives, the number of
 * true negatives, the number of false positives and the number of false
 * negatives.
 * 
 * For detailed information and formulas we refer to <a
 * href="http://www.abeel.be/java-ml/index.php/Performance_measures">Java-ML
 * wiki</a>
 * 
 * @author Thomas Abeel
 * 
 */
public class PerformanceMeasure {
    /**
     * The number of true positives.
     * <p>
     * 
     */
    public double truePositives;

    /**
     * The number of false positives.
     * <p>
     * Type I error, also known as an "error of the first kind", an ? error, or
     * a "false positive": the error of rejecting a null hypothesis when it is
     * actually true. In other words, this is the error of accepting an
     * alternative hypothesis (the real hypothesis of interest) when the results
     * can be attributed to chance. Plainly speaking, it occurs when we are
     * observing a difference when in truth there is none (or more specifically -
     * no statistically significant difference).
     */
    public double falsePositives;

    /**
     * The number of true negatives.
     * <p>
     */
    public double trueNegatives;

    /**
     * The number of false negatives.
     * <p>
     * Type II error, also known as an "error of the second kind", a ? error, or
     * a "false negative": the error of not rejecting a null hypothesis when the
     * alternative hypothesis is the true state of nature. In other words, this
     * is the error of failing to accept an alternative hypothesis when you
     * don't have adequate power. Plainly speaking, it occurs when we are
     * failing to observe a difference when in truth there is one.
     */
    public double falseNegatives;

    public double getCorrelationCoefficient() {

        return (truePositives * trueNegatives - falsePositives * falseNegatives)
                / Math.sqrt((truePositives + falsePositives) * (truePositives + falseNegatives)
                        * (trueNegatives + falsePositives) * (trueNegatives + falseNegatives));
    }

    public double getCost() {
        return falsePositives / truePositives;
    }

    public PerformanceMeasure(double tp, double tn, double fp, double fn) {
        this.truePositives = tp;
        this.trueNegatives = tn;
        this.falsePositives = fp;
        this.falseNegatives = fn;

    }

    /**
     * Default constructor for a new performance measure, all values (TP,TN,FP
     * and FN) will be set zero
     */
    public PerformanceMeasure() {
        this(0, 0, 0, 0);
    }

    public double getTPRate() {
        return this.truePositives / (this.truePositives + this.falseNegatives);
    }

    public double getTNRate() {
        return this.trueNegatives / (this.trueNegatives + this.falsePositives);
    }

    public double getFNRate() {
        return this.falseNegatives / (this.truePositives + this.falseNegatives);
    }

    public double getFPRate() {
        return this.falsePositives / (this.falsePositives + this.trueNegatives);
    }

    public double getErrorRate() {
        return (this.falsePositives + this.falseNegatives) / this.getTotal();
    }

    public double getAccuracy() {
        return (this.truePositives + this.trueNegatives) / this.getTotal();
    }

    public double getRecall() {
        return this.truePositives / (this.truePositives + this.falseNegatives);
    }

    public double getPrecision() {
        return this.truePositives / (this.truePositives + this.falsePositives);
    }

    public double getCorrelation() {
        return (this.truePositives * this.trueNegatives + this.falsePositives * this.falseNegatives)
                / Math.sqrt((this.trueNegatives + this.falseNegatives) * (this.truePositives + this.falsePositives)
                        * (this.trueNegatives + this.falsePositives) * (this.falseNegatives + this.truePositives));
    }

    public double getFMeasure(int beta) {
        return ((beta * beta + 1) * this.getPrecision() * this.getRecall())
                / (beta * beta * this.getPrecision() + this.getRecall());
    }

    public double getQ9() {
        if (this.truePositives + this.falseNegatives == 0) {
            return (this.trueNegatives - this.falsePositives) / (this.trueNegatives + this.falsePositives);
        } else if (this.trueNegatives + this.falsePositives == 0) {
            return (this.truePositives - this.falseNegatives) / (this.truePositives + this.falseNegatives);
        } else
            return 1
                    - Math.sqrt(2)
                    * Math.sqrt(Math.pow(this.falseNegatives / (this.truePositives + this.falseNegatives), 2)
                            + Math.pow(this.falsePositives / (this.trueNegatives + this.falsePositives), 2));

    }

    @Override
    public String toString() {
        return "[TP=" + this.truePositives + ", FP=" + this.falsePositives + ", TN=" + this.trueNegatives + ", FN="
                + this.falseNegatives + "]";
    }

    public double getTotal() {
        return falseNegatives + falsePositives + trueNegatives + truePositives;
    }

}
