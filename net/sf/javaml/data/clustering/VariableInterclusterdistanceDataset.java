/**
 * VariableInterclusterdistanceDataset.java
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
 * This class can generate datasets with variable inter clusters distance to
 * test the time complexity and performance of clustering algorithms on datasets
 * that contain clusters a certain distance .
 * 
 * All datasets contain 2 clusters, with 500 instances each.
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
public class VariableInterclusterdistanceDataset {

	public static void main(String[] args) {
		for (int i = 1; i < 8; i++) {
			write(createNd(i), "distance" + i + ".data");
		}

	}

	private static Dataset createNd(int n) {
		Dataset out = new SimpleDataset();
		int datasize = 1000;
		Random rg = new Random(System.currentTimeMillis());
		float clusterSpread = 0.05f;
		float height = 1.0f / 2.0f;
		float fixedHorizontal = 3.0f / 4.0f;
		float varHorizontal = (float) 0.1f / 4.0f + (0.1f * n);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < datasize; j++) {
				// right, fixed cluster
				float[] vec1 = new float[2];
				vec1[0] = (float) ((rg.nextGaussian() * clusterSpread) + fixedHorizontal);
				vec1[1] = (float) ((rg.nextGaussian() * clusterSpread) + height);
				out.addInstance(new SimpleInstance(vec1));

				// left, variable cluster
				float[] vec2 = new float[2];
				vec2[0] = (float) ((rg.nextGaussian() * clusterSpread) + varHorizontal);
				vec2[1] = (float) ((rg.nextGaussian() * clusterSpread) + height);
				out.addInstance(new SimpleInstance(vec2));
			}
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
