/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection.subset;

import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.featureselection.AttributeSubsetSelection;

/**
 * Provides an implementation of the forward greedy attribute subset selection.
 * 
 * @author Thomas Abeel
 * 
 */
public class GreedyForwardSelection implements AttributeSubsetSelection {
    /* Number of features that should be selected */
    private int n;

    private DistanceMeasure dm;

    /**
     * Creates an Instance from the class labels over all Instances in a data
     * set.
     * 
     * @param data
     * @param i
     * @return
     */
    // TODO this method probably belongs to DatasetTools
    private Instance createInstanceFromClass(Dataset data) {
        Instance out = new DenseInstance(data.size());
        int index = 0;
        for (Instance inst : data)
            out.put(index++, (double) data.classIndex(inst.classValue()));
        return out;
    }

    /**
     * Creates an Instance from the values of one particular attribute over all
     * Instances in a data set.
     * 
     * @param data
     * @param i
     * @return
     */
    // TODO this method probably belongs to DatasetTools
    private Instance createInstanceFromAttribute(Dataset data, int i) {
        Instance out = new DenseInstance(data.size());
        int index = 0;
        for (Instance inst : data)
            out.put(index++, inst.value(i));
        return out;
    }

    /**
     * Creates a new GreedyForwardSelection that will select the supplied number
     * of attributes.
     * 
     * @param n
     *            number of attributes to select in the subset
     */
    public GreedyForwardSelection(int n, DistanceMeasure dm) {
        this.n = n;
        this.dm = dm;
    }

    private Set<Integer> selectedAttributes = null;

    @Override
    public void build(Dataset data) {
        /*
         * When more attributes should be selected then there are, return all
         * attributes.
         */
        if (n > data.noAttributes()) {
            selectedAttributes = data.get(0).keySet();
            return;
        }
        /*
         * Regular procedure, add iteratively the best attribute till we have
         * enough attributes selected.
         */
        Instance classInstance = createInstanceFromClass(data);
        selectedAttributes = new HashSet<Integer>();
        while (selectedAttributes.size() < n) {
            selectNext(data, classInstance);
        }

    }

    private void selectNext(Dataset data, Instance classInstance) {
        int bestIndex = -1;
        double bestScore = Double.NaN;
        for (int i = 0; i < data.noAttributes(); i++) {
            if (!selectedAttributes.contains(i)) {
                Instance attributeInstance = createInstanceFromAttribute(data, i);
               
                double score = dm.measure(attributeInstance, classInstance);
              
                if (!Double.isNaN(score)&&bestIndex == -1) {
                    bestIndex = i;
                    bestScore = score;
                } else {
                    if (!Double.isNaN(score)&&dm.compare(score, bestScore)) {
                        bestIndex = i;
                        bestScore = score;

                    }
                }

            }
        }
        selectedAttributes.add(bestIndex);

    }

    @Override
    public Set<Integer> selectedAttributes() {
        return selectedAttributes;
    }

}
