/**
 * KMeansTest.java
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
package junit.clustering;

import junit.framework.Assert;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

import org.junit.Test;


public class KMeansTest {

    
    /**
     * Test to detect zero size clusters
     */
    @Test
    public void testZeroClusters(){
        Dataset data=new SimpleDataset();
        add(data,2,2);
        add(data,2,3);
        add(data,3,2);
        add(data,3,3);
        
        add(data,2,13);
        add(data,2,14);
        add(data,3,13);
        add(data,3,14);
        
        add(data,13,2);
        add(data,14,3);
        add(data,13,2);
        add(data,14,3);
        
        add(data,13,13);
        add(data,14,14);
        add(data,13,14);
        add(data,14,13);
        
        KMeans km=new KMeans();
        int count=0;
        for(int i=0;i<100;i++){
            Dataset[]clusters=km.executeClustering(data);
            for(int j=0;j<clusters.length;j++){
                if(clusters[j].size()==0)
                    count++;
            }
        }
        
        Assert.assertEquals(0, count);
        
    }
    
    private void add(Dataset data,float x,float y){
        float[] values={x,y};
        SimpleInstance in=new SimpleInstance(values);
        data.addInstance(in);
    }
}
