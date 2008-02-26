/**
 * WekaUtils.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.weka;

import net.sf.javaml.core.Dataset;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Provides utility methods to convert data to and from the WEKA format.
 * 
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class WekaUtils {
	private Instances wData;
	
	private boolean classSet;

	public WekaUtils(Dataset data) {
		
		FastVector att = new FastVector();
		for (int i = 0; i < data.numAttributes(); i++) {
			att.addElement(new Attribute("att" + i));
		}

		FastVector fvNominalVal = new FastVector(3);
		fvNominalVal.addElement("0");
		fvNominalVal.addElement("1");
		// fvNominalVal.addElement("black");
		Attribute ca = new Attribute("classAtt", fvNominalVal);

		// Attribute ca = new Attribute("classAtt");
		// ca.addStringValue("0");
		// ca.addStringValue("1");
		att.addElement(ca);
		// System.out.println(att.size());
		wData = new Instances("Java_ML", att, data.size());
		// System.out.println(wData.numAttributes());
		classSet = data.instance(0).isClassSet();
		if (classSet)
			wData.setClass(ca);

		for (net.sf.javaml.core.Instance i : data) {

			wData.add(instanceToWeka(i));
		}

	}

	public Instances getDataset() {
		return wData;
	}

	public Instance instanceToWeka(net.sf.javaml.core.Instance i) {
		double[] values = new double[classSet ? i.size() + 1 : i.size()];
		System.arraycopy(i.toArray(), 0, values, 0,
				classSet ? values.length - 1 : values.length);
		if (classSet)
			values[values.length - 1] = i.classValue();
		Instance wI = new Instance(i.weight(), values);
		wI.setDataset(wData);
		if (i.isClassSet()) {
			if (i.classValue() == 0) {
				wI.setClassValue("0");
			} else {
				wI.setClassValue("1");
			}
		}
		return wI;
	}
}
