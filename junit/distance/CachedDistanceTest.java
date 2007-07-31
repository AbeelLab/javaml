/**
 * CachedDistanceTest.java
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
package junit.distance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.InstanceTools;
import net.sf.javaml.distance.CachedDistance;
import net.sf.javaml.distance.DynamicTimeWarpingSimilarity;

import org.junit.Assert;
import org.junit.Test;


public class CachedDistanceTest {

    @Test
    public void testCD(){
        Instance x=InstanceTools.randomInstance(250);
        Instance y=InstanceTools.randomInstance(250);
        DynamicTimeWarpingSimilarity dm=new DynamicTimeWarpingSimilarity();
        CachedDistance cd=new CachedDistance(new DynamicTimeWarpingSimilarity());
        double dist=dm.calculateDistance(x, y);
        double cached=cd.calculateDistance(x, y);
        Assert.assertEquals(dist,cached,0.000001);
        
        long time=System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            dm.calculateDistance(x, y);
            
        }
        System.out.println("Time uncached: "+(System.currentTimeMillis()-time));
        time=System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            cd.calculateDistance(x, y);
            
        }
        System.out.println("Time cached: "+(System.currentTimeMillis()-time));
        
    }
}
