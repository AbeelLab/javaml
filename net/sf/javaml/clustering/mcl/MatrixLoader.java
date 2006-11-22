/**
 * MatrixLoader.java, 22-nov-2006
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
 * based on work from Gregor Heinrich, Arbylon (http://www.arbylon.net/projects/).
 * Copyright (c) 2006 Gregor Heinrich. All rights reserved.
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.mcl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * MatrixLoader loads matrices in simple sparse and dense formats.
 *
 * @author gregor :: arbylon . net
 */
public class MatrixLoader {

    public static void main(String[] args) {
        double[][] a = loadDense("m.txt");
        System.out.println(Vectors.print(a));
    }

    /**
     * read a graph with line format labelfrom labelto weight
     *
     * @param string
     */
    public static SparseMatrix loadSparse(String file) {
        SparseMatrix matrix = new SparseMatrix();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length < 2) {
                    System.out.println("Warning: wrong line format (1)");
                    continue;
                }
                int from = Integer.parseInt(parts[0].trim());
                int to = Integer.parseInt(parts[1].trim());
                double weight = 1.;
                if (parts.length > 2) {
                    weight = Double.parseDouble(parts[2].trim());
                }
                matrix.add(from, to, weight);

            }
            br.close();
            return matrix;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * read a graph with the elements of the adjacency matrix in each line.
     *
     * @param file
     * @return
     */
    public static double[][] loadDense(String file) {
        ArrayList<double[]> matrix = new ArrayList<double[]>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("[ ,] *");
                double[] vec = new double[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    vec[i] = Double.parseDouble(parts[i].trim());
                }
                matrix.add(vec);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix.toArray(new double[0][]);
    }

}
