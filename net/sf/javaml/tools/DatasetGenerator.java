/**
 * DatasetGenerator.java, 27-okt-2006
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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

    public static Dataset createClusterDataset(int clusters,int itemsInCluster,int spaceWidth, int clusterWidth){
        Dataset out=new SimpleDataset();
        Random rg = new Random(System.currentTimeMillis());
        for (int i = 0; i < clusters; i++) {
            int x = rg.nextInt(spaceWidth) - (spaceWidth/2);
            int y = rg.nextInt(spaceWidth) - (spaceWidth/2);
            for (int j = 0; j < itemsInCluster; j++) {
                double[] vec = { rg.nextGaussian() * clusterWidth + x, rg.nextGaussian() * clusterWidth + y };
                Instance instance = new SimpleInstance(vec);
                out.addInstance(instance);
            }
        }
        
        return out;
    }

}
