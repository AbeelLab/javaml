/**
 * DatasetGenerator.java, 27-okt-2006
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

import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
@Deprecated
public class DatasetGenerator {

    private static Random rg = new Random(System.currentTimeMillis());
    
    public static Dataset createClusterDataset(int clusters, int itemsInCluster, int spaceWidth, int clusterWidth) {
        Dataset out = new SimpleDataset();
        for (int i = 0; i < clusters; i++) {
            int x = rg.nextInt(spaceWidth);
            int y = rg.nextInt(spaceWidth);
            for (int j = 0; j < itemsInCluster; j++) {
                double[] vec = {  rg.nextDouble() * clusterWidth + x,
                        rg.nextDouble() * clusterWidth + y };
                Instance instance = new SimpleInstance(vec);
                out.addInstance(instance);
            }
        }

        return out;
    }

    public static Dataset createClusterSquareDataset(float space, float clusterSpread){
        return createClusterSquareDataset(space,clusterSpread,20);
    }
    /**
     * Create a dataset with four clusters in the first quadrant with the max X
     * value = space
     * 
     * @param space
     * @param clusterSpread
     * @return
     */
    public static Dataset createClusterSquareDataset(float space, float clusterSpread,int itemsPerCluster) {
        Dataset out = new SimpleDataset();
       // System.out.println("Centers for space="+space+":");
        float small=1.0f/4.0f;
        float large=3.0f/4.0f;
//        System.out.println(space * small+"\t"+space * small);
//        System.out.println(space * small+"\t"+space * large);
//        System.out.println(space * large+"\t"+space * small);
//        System.out.println(space * large+"\t"+space * large);
        
        
        for (int i = 0; i < itemsPerCluster; i++) {

            // lower left
            double[] vec1 = { (float) ((rg.nextGaussian() * clusterSpread)  + space * small),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * small) };
            out.addInstance(new SimpleInstance(vec1));
//            // upper left
            double[] vec2 = { (float) ((rg.nextGaussian() * clusterSpread)  + space * small),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * large) };
            out.addInstance(new SimpleInstance(vec2));
//            // lower righ
            double[] vec3 = { (float) ((rg.nextGaussian() * clusterSpread)  + space * large),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * small) };
            out.addInstance(new SimpleInstance(vec3));
//            // upper right
            double[] vec4= { (float) ((rg.nextGaussian() * clusterSpread)  + space * large),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * large) };
            out.addInstance(new SimpleInstance(vec4));
      }
        return out;
    }

    public static Dataset createClusterSquareDataset3D(float space, float clusterSpread,int itemsPerCluster) {
        Dataset out = new SimpleDataset();
       // System.out.println("Centers for space="+space+":");
        float small=1.0f/4.0f;
        float large=3.0f/4.0f;
//        System.out.println(space * small+"\t"+space * small);
//        System.out.println(space * small+"\t"+space * large);
//        System.out.println(space * large+"\t"+space * small);
//        System.out.println(space * large+"\t"+space * large);
        
        for (int i = 0; i < itemsPerCluster; i++) {

            // lower left
            double[] vec1 = {0.5f, (float) ((rg.nextGaussian() * clusterSpread)  + space * small),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * small) };
            out.addInstance(new SimpleInstance(vec1));
//            // upper left
            double[] vec2 = {0.0f, (float) ((rg.nextGaussian() * clusterSpread)  + space * small),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * large) };
            out.addInstance(new SimpleInstance(vec2));
//            // lower righ
            double[] vec3 = {0.5f,(float) ((rg.nextGaussian() * clusterSpread)  + space * large),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * small) };
            out.addInstance(new SimpleInstance(vec3));
//            // upper right
            double[] vec4= {0.0f, (float) ((rg.nextGaussian() * clusterSpread)  + space * large),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * large) };
            out.addInstance(new SimpleInstance(vec4));
      }
//        System.out.print("[");
//        for(int i=0;i<out.size();i++){
//            System.out.print(out.getInstance(i).getValue(0)+","+out.getInstance(i).getValue(1)+","+out.getInstance(i).getValue(2));
//            if(i<out.size()-1){
//                System.out.print(";");
//            }
//        }
//        System.out.println("]");
        return out;
    }
}
