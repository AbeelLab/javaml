/**
 * WekaUtils.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.utils;

import net.sf.javaml.core.Dataset;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaUtils {
    public static Instances datasetToWeka(Dataset data) {
        FastVector att = new FastVector();
        for (int i = 0; i < data.numAttributes(); i++) {
            att.addElement(new Attribute("att" + i));
        }

        FastVector fvNominalVal = new FastVector(3);
        fvNominalVal.addElement("0");
        fvNominalVal.addElement("1");
        // fvNominalVal.addElement(“black”);
        Attribute ca = new Attribute("classAtt", fvNominalVal);

        // Attribute ca = new Attribute("classAtt");
        // ca.addStringValue("0");
        // ca.addStringValue("1");
        att.addElement(ca);
        System.out.println(att.size());
        Instances wData = new Instances("Java_ML", att, data.size());
        System.out.println(wData.numAttributes());
        wData.setClass(ca);
        for (net.sf.javaml.core.Instance i : data) {
            double[] values = new double[i.size() + 1];
            System.arraycopy(i.toArray(), 0, values, 0, values.length - 1);
            values[values.length - 1] = i.classValue();
            Instance wI = new Instance(i.weight(), values);
            wI.setDataset(wData);
            if (i.classValue() == 0) {
                wI.setClassValue("0");
            } else {
                wI.setClassValue("1");
            }

            wData.add(wI);
        }
        return wData;

    }
}
