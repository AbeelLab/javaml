/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.weka;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 * Provides utility methods to convert data from the WEKA format to the Java-ML
 * format.
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class FromWekaUtils {
	private Dataset data;
	private Instances wData;

	public FromWekaUtils(Instances wData) {
		data=new DefaultDataset();
		this.wData=wData;
		for (int i = 0; i < wData.numInstances(); i++) {
			Instance wInst = wData.instance(i);
			data.add(instanceFromWeka(wInst));
		}
	}

	public net.sf.javaml.core.Instance instanceFromWeka(Instance inst) {
		net.sf.javaml.core.Instance out;
		if (inst instanceof SparseInstance) {
			out = new net.sf.javaml.core.SparseInstance();
			SparseInstance tmp = (SparseInstance) inst;
			for (int i = 0; i < tmp.numValues(); i++) {
				int index = inst.index(i);
				double value = inst.value(index);
				out.put(index, value);
			}

		}

		else {
			double[] vals;
			if (inst.classIsMissing())
				vals = inst.toDoubleArray();
			else {
				vals = new double[inst.numAttributes() - 1];
				double[] tmp = inst.toDoubleArray();
				System.arraycopy(tmp, 0, vals, 0, inst.classIndex());
				System.arraycopy(tmp, inst.classIndex() + 1, vals, inst.classIndex(), vals.length - inst.classIndex());

			}
			out = new DenseInstance(vals);

		}
		
		if (!inst.classIsMissing()) {
			out.setClassValue(wData.classAttribute().value((int)inst.classValue()));
		}
		return out;
	}

	public Dataset getDataset() {
		return data;
	}

}
