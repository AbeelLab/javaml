package net.sf.javaml.test;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.filter.NormalizeMean;

public class TestNormalizeMean {

	
	    public static void main(String[] args) {
	        Dataset data=new SimpleDataset();
	        Instance i=new SimpleInstance(new float[]{0.0f});
	        data.addInstance(i);
	        i=new SimpleInstance(new float[]{1.0f});
	        data.addInstance(i);
	        i=new SimpleInstance(new float[]{-1.0f});
	        data.addInstance(i);
	        i=new SimpleInstance(new float[]{2.0f});
	        data.addInstance(i);
	        i=new SimpleInstance(new float[]{-2.0f});
	        data.addInstance(i);
	        
	        Filter norm=new NormalizeMean();
	        System.out.println(norm.filterDataset(data));
	        
	        
	         }

	

}
