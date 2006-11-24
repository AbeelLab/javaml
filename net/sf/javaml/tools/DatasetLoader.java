/**
 * DatasetLoader.java, 7-nov-2006
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
package net.sf.javaml.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

public class DatasetLoader {
    /**
     * This method will load the data stored in the file given as parameter.
     * 
     * The format of the file should be as follows: One entry on each line, all
     * values of an entry on a single line, no blank lines, values should be
     * seperated by a tab character and all entries should have the same number
     * of values.
     * 
     * @param f
     */
    public static Dataset loadDataset(File f) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line = in.readLine();
        Dataset out = new SimpleDataset();

        Set<Integer> nonFloat = new HashSet<Integer>();
        while (line != null) {
            String[] arr = line.split("\t");
            float[] values = new float[arr.length];

            for (int i = 0; i < arr.length; i++) {
                try {
                    values[i] = Float.parseFloat(arr[i]);
                } catch (NumberFormatException e) {
                    nonFloat.add(i);
                }
            }
            out.addInstance(new SimpleInstance(values));
            line = in.readLine();
        }
        if (nonFloat.size() != 0) {
            System.err.println("Null columns in the loaded data");
        }
        in.close();
        return out;

    }
}
