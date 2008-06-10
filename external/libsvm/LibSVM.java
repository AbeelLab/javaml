/**
 * LibSVM.java
 *
 * %SVN.HEADER%
 */
package external.libsvm;

import java.io.IOException;
import java.util.Map;

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

    public LibSVM(){
        param.C=1;
    }
    
    public void buildClassifier(Dataset data) {
        // System.out.println("Size: " + data.size());
        // System.out.println("att: " + data.noAttributes());
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
//        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        // cross_validation = 0;
        // svm.
        // svm s=new svm();
        svm_problem p = new svm_problem();
        p.l = data.size();
        p.y = new double[data.size()];
        p.x = new svm_node[data.size()][data.noAttributes()];
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.instance(i).classValue());
            p.y[i] = data.classIndex(data.instance(i).classValue());
            System.out.println(p.y[i]);
            for (int j = 0; j < data.noAttributes(); j++) {
                p.x[i][j] = new svm_node();
                p.x[i][j].index = j;
                p.x[i][j].value = data.instance(i).value(j);
            }

        }
        // p.x = new svm_node[data.noAttributes()][data.size()];
        // for (int i = 0; i < data.size(); i++) {
        // p.y[i] = data.instance(i).classValue();
        // for (int j = 0; j < data.noAttributes(); j++) {
        // p.x[j][i] = new svm_node();
        // p.x[j][i].index = j;
        // p.x[j][i].value = data.instance(i).value(j);
        // }
        //
        // }

        model = svm.svm_train(p, param);
        // try {
        // svm.svm_save_model("test", model);
        // } catch (IOException e) {
        // // TOO Auto-generated catch block
        // e.printStackTrace();
        // }
        // System.out.println(Arrays.toString(model.nSV));
        double[][] coef = model.sv_coef;
        // System.out.println();
        double[][] prob = new double[model.SV.length][model.SV[0].length];// model.SV
        for (int i = 0; i < model.SV.length; i++) {
            for (int j = 0; j < model.SV[0].length; j++) {
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
                        // System.out.println("m=" + m + " j=" + j + " i=" + i);
                        // System.out.println("coef=" + coef.length + "
                        // coef[x]=" + coef[0].length);
                        acc += coef[j][m] * prob[m][i];
                        // acc += coef[m][j] * prob[m][i];
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
                    // System.out.println(m + ":" + (w_list[i][k][m] +
                    // w_list[j][i][m]));
                }
                // printf("\n");
            }
        }
        // crap, brol weights.
        // System.out.println(Arrays.deepToString(model.SV));

        // System.out.println("SV coef");
        // System.out.println(Arrays.deepToString(model.sv_coef));
        // System.out.println("SV coef - weight?");
        // System.out.println(Arrays.toString(model.sv_coef[0]));
        // System.out.println("Rho coef");
        // System.out.println(Arrays.toString(model.rho));
        // svm_model model=svm.svm_train(p, param);

    }

    private double[] weights;

    public double[] getWeights() {
        return weights;
    }

    public Object classifyInstance(Instance instance) {
        svm_node[] x = new svm_node[instance.size()];
        //TODO implement sparseness
        for (int i = 0; i < instance.size(); i++) {
            x[i] = new svm_node();
            x[i].index = i;
            x[i].value = instance.value(i);
        }
        double d=svm.svm_predict(model, x);
        
        System.out.print("pp "+instance.classValue()+"\t"+(int)d);
        System.out.println("\t"+data.classValue((int)d));
        return data.classValue((int)d);
    }

//    public double[] distributionForInstance(Instance instance) {
//        return null;
//    }

    public void setC(double c) {
        param.C = c;

    }

    @Override
    public Map<Object, Double> distributionForInstance(Instance instance) {
        // TODO Auto-generated method stub
        return null;
    }

}
