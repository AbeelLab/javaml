/**
 * VariableNumberofclusterDataset.java
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
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.data.clustering;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.filter.NormalizeMidrange;

/**
 * This class can generate datasets with variable number of clusters to test the
 * time complexity and performance of clustering algorithms on datasets that
 * contain different number of clusters.
 * 
 * All datasets contain X clusters, with 1000 instances each.
 * 
 * All instances have dimension 2
 * 
 * All datasets are normalized with the values in each dimension between 0 and
 * 1.
 * 
 * All instances have no class set.
 * 
 * The values in each dimension within a cluster are Gaussian distributed with a
 * standard deviation of 0.1.
 * 
 * The number of dataitems in each dataset is variable.
 * 
 * @author Andreas De Rijcke
 * 
 */

public class VariableNumberofclusterDataset {
	public static void main(String[] args) {
		for (int i = 2; i < 26; i++) {
			write(createNd(i), "clusternumber" + i + ".data");
		}
	}

	private static Dataset createNd(int n) {
		Dataset out=new SimpleDataset();
		int datasize = 1000;
		Random rg = new Random(System.currentTimeMillis());
		int dim=(int)Math.sqrt(n)+1;
		float clusterSpread = 0.1f;
		for(int i=0;i<n;i++){
			int rij=i/dim;
			int kolom=i%dim;
			for(int j=0;j<datasize;j++){
				double x=rg.nextGaussian() * clusterSpread+0.5+kolom;
				double y=rg.nextGaussian()*clusterSpread+0.5+rij;
				float[] vec1 = new float[2];
				vec1[0]=(float)x;
				vec1[1]=(float)y;
				out.addInstance(new SimpleInstance(vec1));
			}
		}	
		Filter filter=new NormalizeMidrange(0.5,1);
		return filter.filterDataset(out);
	}

	private static void write(Dataset data, String fileName) {
		try {
			PrintWriter out = new PrintWriter(fileName);
			for (int i = 0; i < data.size(); i++) {
				Instance tmp = data.getInstance(i);
				out.print(tmp.getValue(0));
				for (int j = 1; j < tmp.size(); j++)
					out.print("\t" + tmp.getValue(j));
				out.println();
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
