/**
 * RemoveTest.java
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
package junit.filter;

import junit.framework.Assert;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.Filter;

import org.junit.Test;

public class RemoveTest {
    @Test
    public void testRemove(){
        float[]vals={1,2,3,4,5,6};
        Instance inst=new SimpleInstance(vals);
        Dataset data=new SimpleDataset();
        for(int i=0;i<10;i++){
            data.addInstance(inst);
            
        }
        int[]indices={0,1,2};
        Filter rem=new net.sf.javaml.filter.RemoveAttributes(indices);
        Dataset out=rem.filterDataset(data);
        //System.out.println(out);
        Assert.assertTrue(out.getInstance(0).getValue(0)==4);
        Assert.assertTrue(out.getInstance(0).getValue(1)==5);
        Assert.assertTrue(out.getInstance(0).getValue(2)==6);
    }

}
