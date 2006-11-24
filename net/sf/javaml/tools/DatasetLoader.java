package net.sf.javaml.tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

/*
 * DatasetLoader.java 
 * -----------------------
 * Copyright (C) 2005-2006  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 * Created on: 7-nov-2006 - 14:26:05
 */

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

        while (line != null) {
            String[] arr = line.split("\t");
            float[] values = new float[arr.length];
            for (int i = 0; i < arr.length; i++) {
                values[i] = Float.parseFloat(arr[i]);
            }
            out.addInstance(new SimpleInstance(values));
            line=in.readLine();
        }
        in.close();
        return out;

    }
}
