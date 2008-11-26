/**
 * %SVN.HEADER%
 */
package external.libsvm;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class LibSVM implements Classifier {

    private static final long serialVersionUID = 1096163861836292687L;

    private svm_model model;

    private Dataset data;

    private svm_parameter param = new svm_parameter();

    public LibSVM() {
        param.C = 1;
    }

    public void buildClassifier(Dataset data) {
        this.data = data;
        // svm_parameter param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.degree = 1;
        param.gamma = 0; // 1/k
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];

        svm_problem p = new svm_problem();
        p.l = data.size();
        p.y = new double[data.size()];
        p.x = new svm_node[data.size()][];
        int tmpIndex = 0;
        int noAttributes = data.noAttributes();
        for (int j = 0; j < data.size(); j++) {
            Instance tmp = data.instance(j);
            p.y[tmpIndex] = data.classIndex(tmp.classValue());
            p.x[tmpIndex] = new svm_node[tmp.keySet().size()];
            int i = 0;
            SortedSet<Integer> tmpSet = tmp.keySet();
            for (int index : tmpSet) {
                p.x[tmpIndex][i] = new svm_node();
                p.x[tmpIndex][i].index = index;
                p.x[tmpIndex][i].value = tmp.value(index);
                i++;
            }
            tmpIndex++;
        }

        model = svm.svm_train(p, param);

        double[][] coef = model.sv_coef;

        double[][] prob = new double[model.SV.length][noAttributes];
        for (int i = 0; i < model.SV.length; i++) {
            for (int j = 0; j < model.SV[i].length; j++) {
                prob[i][j] = model.SV[i][j].value;
            }
        }
        double w_list[][][] = new double[model.nr_class][model.nr_class - 1][model.SV[0].length];

        for (int i = 0; i < model.SV[0].length; ++i) {
            for (int j = 0; j < model.nr_class - 1; ++j) {
                int index = 0;
                int end = 0;
                double acc;
                for (int k = 0; k < model.nr_class; ++k) {
                    acc = 0.0;
                    index += (k == 0) ? 0 : model.nSV[k - 1];
                    end = index + model.nSV[k];
                    for (int m = index; m < end; ++m) {
                        acc += coef[j][m] * prob[m][i];
                    }
                    w_list[k][j][i] = acc;
                }
            }
        }

        weights = new double[model.SV[0].length];
        for (int i = 0; i < model.nr_class - 1; ++i) {
            for (int j = i + 1, k = i; j < model.nr_class; ++j, ++k) {
                for (int m = 0; m < model.SV[0].length; ++m) {
                    weights[m] = (w_list[i][k][m] + w_list[j][i][m]);

                }
            }
        }
    }

    private double[] weights;

    public double[] getWeights() {
        return weights;
    }

    public Object classify(Instance instance) {
        svm_node[] x = new svm_node[instance.noAttributes()];
        // TODO implement sparseness
        for (int i = 0; i < instance.noAttributes(); i++) {
            x[i] = new svm_node();
            x[i].index = i;
            x[i].value = instance.value(i);
        }
        double d = svm.svm_predict(model, x);

        return data.classValue((int) d);
    }

    public void setC(double c) {
        param.C = c;

    }

    @Override
    public Map<Object, Double> classDistribution(Instance instance) {
        HashMap<Object, Double> out = new HashMap<Object, Double>();
        for (Object o : data.classes()) {
            out.put(o, 0.0);
        }
        out.put(classify(instance), 1.0);
        return out;

    }

}
