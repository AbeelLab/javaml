/**
 * ARFF.java
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
package net.sf.javaml.tools.weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

public class ARFF {

    public static Dataset readARFF(String file)throws IOException{
        return readARFF(new File(file));
        
    }

    public static Dataset readARFF(File file)throws IOException {
        //      FIXME hacked together solution;
        //skip everything empty or with @ in front
        //read all data and make the last one the classvalue
        BufferedReader in =new BufferedReader(new FileReader(file));
        Dataset out=new SimpleDataset();
        String line=in.readLine();
        while(line!=null&&(line.length()==0||line.startsWith("@"))){
            line=in.readLine();
        }
        while(line!=null){
            String[]arr=line.split(",");
            double[]values=new double[arr.length-1];
            for(int i=0;i<arr.length-1;i++){
                values[i]=Double.parseDouble(arr[i]);
            }
            int classValue=Integer.parseInt(arr[arr.length-1]);
            out.add(new SimpleInstance(values,1,classValue));
            line=in.readLine();                        
        }
        return out;
    }
}
