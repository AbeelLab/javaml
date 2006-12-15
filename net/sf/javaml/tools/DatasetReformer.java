/**
 * DatasetLoader.java, 27-nov-2006
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
 * Copyright (c) 2006, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DatasetReformer {
	/**
	 * This method will load the data stored in the file given as parameter, and
	 * returns this data in the format necessary for DataLoader usage.
	 * 
	 * The format of the file should be as follows: One entry on each line, all
	 * values of an entry on a single line, no blank lines, values should be
	 * seperated by a given character(s) and all entries should have the same
	 * number of values.
	 * 
	 * @param f
	 */

	public static void main(String[] args) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader("spectf-train.txt"));
		String line = in.readLine();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				"out.data")));
		while (line != null) {
			//
			String[] arr = line.split(",");
			int[] values = new int[arr.length];
			//double[] values = new double[arr.length];
			for (int i = 0; i < arr.length; i++) {
				try {
					values[i] = Integer.parseInt(arr[i]);
					//values[i] = Double.parseDouble(arr[i]);
				} catch (NumberFormatException e) {
					values[i] = 666;
				}
			}
			String tmp = "";
			for (int i = 1; i < arr.length; i++) {
				tmp += String.valueOf(values[i]) + "\t";
			}
			System.out.println(tmp);
			out.write(tmp);
			out.write("\n");
			line = in.readLine();
		}
		in.close();
		out.close();
	}
}
