package net.sf.javaml.test;
import java.io.File;

import net.sf.javaml.clustering.XMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.data.DatasetLoader;

/*
 * TestDatasetLoader.java 
 * -----------------------
 * Copyright (C) 2005-2006  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 * Created on: 17-jan-2007 - 11:39:12
 */

public class TestDatasetLoader {

    public static void main(String[]args)throws Exception{
        System.out.println("Loading data...");
        Dataset data=DatasetLoader.loadDataset(new File("dim16.data"));
        System.out.println("Dataset size: "+data.size());
        XMeans xm=new XMeans();
        System.out.println("Executing clustering...");
        Dataset[] clusters=xm.executeClustering(data);
        System.out.println(clusters.length);
    }
}
