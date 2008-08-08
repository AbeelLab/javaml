/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.NormDistance;

/**
 * Implementation of the K nearest neighbor (KNN) classification algorithm.
 * 
 * @author Thomas Abeel
 * 
 */
public class KNearestNeighbors extends AbstractClassifier{

    private static final long serialVersionUID = 1560149339188819924L;

    private Dataset training;

    private NormDistance euc = new NormDistance(2);

    private int k = 5;

    public KNearestNeighbors(int k) {
        this.k = k;
    }

    @Override
    public void buildClassifier(Dataset data) {
        this.training = data;

    }

    

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        Set<Instance> neighbors = training.kNearest(k, euc, instance);
//        System.out.println(neighbors);
        HashMap<Object, Double> out = new HashMap<Object, Double>();
//        System.out.println("classes: "+training.classes());
        for (Object o : training.classes())
            out.put(o, 0.0);
        // double[] output = new double[training.classes().size()];
       
        for (Instance i : neighbors) {
//            System.out.println("Class: "+i.classValue());
            out.put(i.classValue(), out.get(i.classValue()) + 1);
        }
//        System.out.println("real: "+instance.classValue());
//        System.out.println(out);
//        
        double min = k;
        double max = 0;
        for (Object key : out.keySet()) {
            double val = out.get(key);
            if (val > max)
                max = val;
            if (val < min)
                min = val;
        }
        for (Object key : out.keySet()) {
            out.put(key, (out.get(key) - min) / (max - min));
        }
       
        return out;
    }

    // public void buildClassifier(Dataset data) {
    //        
    //
    // }
    //
    // public Object classifyInstance(Instance<?> instance) {
    // return ArrayUtils.maxIndex(distributionForInstance(instance));
    // }
    //
    // public double[] distributionForInstance(Instance instance) {
    // List<Instance> neighbors = DatasetTools.getNearestK(training, euc,
    // instance, k);
    // double[] output = new double[training.numClasses()];
    // for (Instance i : neighbors) {
    // output[i.classValue()]++;
    // }
    // ArrayUtils.normalize(output);
    // return output;
    //
    // }

}
