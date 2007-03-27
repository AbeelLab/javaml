/**
 * SubclusterDataset.java
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
 * This class can generates datasets with 4 clusters, who contain each 4
 * subclusters with variable inter subcluster distance to test the time
 * complexity and performance of clustering algorithms on datasets with
 * subclusters.
 * 
 * All subclusters contain 100 instances. All instances have dimension 2
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

public class VariableIntersubclusterdistanceDataset {
	public static void main(String[] args) {
		for (int i = 1; i < 11; i++) {
			write(createNd(i), "clusterSubSpace" + i + ".data");
		}

	}

	private static Dataset createNd(int space) {
		double spacer = 0.01 * space;
		Dataset out = new SimpleDataset();
		int datasize = 100;
		Random rg = new Random(System.currentTimeMillis());
		double clusterSpread = 0.014;
		for (int i = 0; i < 2; i++) {
			double rij = 0.25 + 0.5 * i;
			for (int j = 0; j < 2; j++) {
				double kolom = 0.25 + 0.5 * j;
				for (int k = 0; k < datasize; k++) {
					double x1 = rg.nextGaussian() * clusterSpread + kolom
							+ spacer;
					double y1 = rg.nextGaussian() * clusterSpread + rij
							+ spacer;
					float[] vec1 = new float[2];
					vec1[0] = (float) x1;
					vec1[1] = (float) y1;
					out.addInstance(new SimpleInstance(vec1));
					double x2 = rg.nextGaussian() * clusterSpread + kolom
							+ spacer;
					double y2 = rg.nextGaussian() * clusterSpread + rij
							- spacer;
					float[] vec2 = new float[2];
					vec2[0] = (float) x2;
					vec2[1] = (float) y2;
					out.addInstance(new SimpleInstance(vec2));
					double x3 = rg.nextGaussian() * clusterSpread + kolom
							- spacer;
					double y3 = rg.nextGaussian() * clusterSpread + rij
							+ spacer;
					float[] vec3 = new float[2];
					vec3[0] = (float) x3;
					vec3[1] = (float) y3;
					out.addInstance(new SimpleInstance(vec3));
					double x4 = rg.nextGaussian() * clusterSpread + kolom
							- spacer;
					double y4 = rg.nextGaussian() * clusterSpread + rij
							- spacer;
					float[] vec4 = new float[2];
					vec4[0] = (float) x4;
					vec4[1] = (float) y4;
					out.addInstance(new SimpleInstance(vec4));
				}
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
