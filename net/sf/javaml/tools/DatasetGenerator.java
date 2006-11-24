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

public class DatasetGenerator {

    public static Dataset createClusterDataset(int clusters, int itemsInCluster, int spaceWidth, int clusterWidth) {
        Dataset out = new SimpleDataset();
        Random rg = new Random(System.currentTimeMillis());
        for (int i = 0; i < clusters; i++) {
            int x = rg.nextInt(spaceWidth);
            int y = rg.nextInt(spaceWidth);
            for (int j = 0; j < itemsInCluster; j++) {
                float[] vec = { (float) rg.nextDouble() * clusterWidth + x,
                        (float) rg.nextDouble() * clusterWidth + y };
                Instance instance = new SimpleInstance(vec);
                out.addInstance(instance);
            }
        }

        return out;
    }

    /**
     * Create a dataset with four clusters in the first quadrant with the max X
     * value = space
     * 
     * @param space
     * @param clusterSpread
     * @return
     */
    public static Dataset createClusterSquareDataset(float space, float clusterSpread) {
        Dataset out = new SimpleDataset();
        System.out.println("Centers for space="+space+":");
        float small=1.0f/4.0f;
        float large=3.0f/4.0f;
        System.out.println(space * small+"\t"+space * small);
        System.out.println(space * small+"\t"+space * large);
        System.out.println(space * large+"\t"+space * small);
        System.out.println(space * large+"\t"+space * large);
        
        Random rg = new Random(System.currentTimeMillis());
        for (int i = 0; i < 25; i++) {
            // lower left
            float[] vec1 = { (float) ((rg.nextGaussian() * clusterSpread)  + space * small),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * small) };
            out.addInstance(new SimpleInstance(vec1));
//            // upper left
            float[] vec2 = { (float) ((rg.nextGaussian() * clusterSpread)  + space * small),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * large) };
            out.addInstance(new SimpleInstance(vec2));
//            // lower righ
            float[] vec3 = { (float) ((rg.nextGaussian() * clusterSpread)  + space * large),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * small) };
            out.addInstance(new SimpleInstance(vec3));
//            // upper right
            float[] vec4= { (float) ((rg.nextGaussian() * clusterSpread)  + space * large),
                    (float) ((rg.nextGaussian() * clusterSpread)  + space * large) };
            out.addInstance(new SimpleInstance(vec4));
      }
        return out;
    }

}
