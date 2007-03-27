/**
 * Specialclustershape2Dataset.java
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

/**
 * This class can generate a dataset with non-spherical shaped clusters to test
 * the time complexity and performance of clustering algorithms on datasets that
 * contain non-spherical clusters .
 * 
 * Dataset contains 4 clusters, with respectively 100, 200, 400 and 1300
 * instances each, 2000 over-all.
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
 * @author Andreas De Rijcke
 * 
 */

public class Specialclustershape2Dataset {
	public static void main(String[] args) {
		write(createNd(), "special_shape2.data");
	}

	
	// TODO complete code
	private static float[] createVec(double x, double y, double clusterSpread) {
		float[] vec = new float[2];
		Random rg = new Random(System.currentTimeMillis());
		double x1 = rg.nextGaussian() * clusterSpread + x;
		double y1 = rg.nextGaussian() * clusterSpread + y;
		vec[0] = (float) x1;
		vec[1] = (float) y1;
		return vec;
	}

	private static Dataset createNd() {
		Dataset out = new SimpleDataset();
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.15, 0.45, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.135, 0.36, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.18, 0.18, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.27, 0.15, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.4, 0.18, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.45, 0.27, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.5, 0.45, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.45, 0.615, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.35, 0.73, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.38, 0.8, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.45, 0.875, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.6, 0.85, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		for (int i = 0; i < 10; i++) {
			float[] vec = createVec(0.7, 0.7, 0.03);
			out.addInstance(new SimpleInstance(vec));
		}
		return out;
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
