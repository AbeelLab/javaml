/**
 * NormalizeMean.java, 4-dec-2006
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

/**
 * This filter will normalize the dataset with mean 0 and standard deviation 1
 * 
 * @linkplain http://www.faqs.org/faqs/ai-faq/neural-nets/part2/section-16.html
 * 
 * @author Thomas Abeel
 * 
 */
public class NormalizeMean implements Filter {

	private float[] mean=null;

	private float[] std= null;

	public Dataset filterDataset(Dataset data) {
		if (data.size() == 0)
			return data;
		int instanceLength = data.getInstance(0).size();
		mean = new float[instanceLength];
		std = new float[instanceLength];
		for (int i = 0; i < instanceLength; i++) {
			for (int j = 0; j < data.size(); j++) {
				mean[i] += data.getInstance(j).getValue(i) / data.size();
			}
		}
		for (int i = 0; i < instanceLength; i++) {
			for (int j = 0; j < data.size(); j++) {
				std[i] += (data.getInstance(j).getValue(i) - mean[i])
						* (data.getInstance(j).getValue(i) - mean[i]);
			}
		}
		for (int i = 0; i < instanceLength; i++) {
			std[i] = (float) Math.sqrt(std[i] / (data.size() - 1));
		}
		Dataset out=new SimpleDataset();
		for(int i=0;i<data.size();i++){
			out.addInstance(filterInstance(data.getInstance(i)));
		}
		return out;
	}

	
	
	
	public Instance filterInstance(Instance instance) {
		if(mean==null||std==null)
			throw new RuntimeException("You should first call filterDataset for this filter, some parameters are not yet set.");
        double[] out=new double [instance.size()];
		for(int i=0;i<out.length;i++){
			out[i]=(instance.getValue(i)-mean[i])/std[i];
		}
		return new SimpleInstance(out, instance.getWeight(), instance.isClassSet(), instance
                .getClassValue());
		
	}




    public Instance unfilterInstance(Instance instance) {
        if(mean==null||std==null)
            throw new RuntimeException("You should first call filterDataset for this filter, some parameters are not yet set.");
        double[] out=new double[instance.size()];
        for(int i=0;i<out.length;i++){
            //out[i]=(instance.getValue(i)-mean[i])/std[i];
            out[i]=(instance.getValue(i)*std[i])+mean[i];
        }
        return new SimpleInstance(out, instance.getWeight(), instance.isClassSet(), instance
                .getClassValue());
    }

}
