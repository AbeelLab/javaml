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
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

/**
 * A class to load datasets from file.
 * <p>
 * The format of the file should be as follows: One entry on each line, all
 * values of an entry on a single line, no blank lines, values should be
 * seperated by a tab character and all entries should have the same number of
 * values.
 * <p>
 * Only class values are allowed to be non float. If a dataset has no labels,
 * then all columns should be float values.
 * 
 * @author Thomas Abeel
 * 
 */
public class DatasetLoader {

    public static Dataset loadDataset(String f) throws IOException {
        return loadDataset(new File(f));
    }

    public static Dataset loadDataset(String f, int classIndex) throws IOException {
        return loadDataset(new File(f), classIndex);
    }

    /**
     * Load the data from a file without class labels.
     * <p>
     * All columns should only contain float values.
     * 
     * @param f
     *            the file to be loaded.
     */
    public static Dataset loadDataset(File f) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line = in.readLine();
        int lineCount = 0;
        while (line != null && line.length() == 0) {
            lineCount++;
            line = in.readLine();
        }
        Dataset out = new SimpleDataset();

        while (line != null) {

            String[] arr = line.split("\t");
            float[] values = new float[arr.length];

            for (int i = 0; i < arr.length; i++) {
                try {
                    values[i] = Float.parseFloat(arr[i]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid data on line " + lineCount + " column " + i);
                    throw new RuntimeException("Invalid data file");
                }
            }
            out.addInstance(new SimpleInstance(values));
            while (line != null && line.length() == 0) {
                lineCount++;
                line = in.readLine();
            }
        }

        in.close();
        return out;

    }

    /**
     * This method will load the data stored in a file..
     * <p>
     * Only the column with the class values is allowed to have different values
     * than floats.
     * 
     * @param f
     *            the file to be loaded.
     * @param classIndex
     *            the index of the column that contains the class labels. This
     *            index starts from zero for the first column and should not be
     *            negative.
     */
    public static Dataset loadDataset(File f, int classIndex) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line = in.readLine();
        int lineCount = 0;
        // get a non empty line
        while (line != null && line.length() == 0) {
            lineCount++;
            line = in.readLine();
            System.err.println("Reading empty X");
        }
        Dataset out = new SimpleDataset();

        if (line == null) {
            return out;
        }
        // check type of classIndex
        String[] tmp = line.split("\t");
        boolean isInteger = true;
        try {
            Integer.parseInt(tmp[classIndex]);
        } catch (NumberFormatException e) {
            isInteger = false;
        }
        HashMap<String, Integer> classMapping = new HashMap<String, Integer>();

        // load data
        while (line != null) {

            String[] arr = line.split("\t");
            float[] values = new float[arr.length - 1];
            int classValue = -1;
            for (int i = 0; i < arr.length; i++) {
                try {
                    if (i < classIndex) {
                        values[i] = Float.parseFloat(arr[i]);
                    } else if (i == classIndex) {
                        if (isInteger) {
                            classValue = Integer.parseInt(arr[i]);

                        } else {
                            if (!classMapping.containsKey(arr[i])) {
                                classMapping.put(arr[i], classMapping.size());
                            }
                            classValue = classMapping.get(arr[i]);
                        }

                    } else {
                        values[i - 1] = Float.parseFloat(arr[i]);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid data on line " + lineCount + " column " + i);
                    throw new RuntimeException("Invalid data file");
                }
            }

            out.addInstance(new SimpleInstance(values, 1.0f, true, classValue));
            lineCount++;
            line = in.readLine();
            while (line != null && line.length() == 0) {
                lineCount++;
                line = in.readLine();
                System.err.println("Reading empty Y");
            }

        }
        in.close();
        return out;

    }
}
