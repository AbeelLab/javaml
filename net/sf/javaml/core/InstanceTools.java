/**
 * InstanceTools.java, 26-feb-07
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
package net.sf.javaml.core;

public class InstanceTools {

    
    public static Instance normalizeMidrange(double normalMiddle,double normalRange,Instance min,Instance max,Instance instance){
        double[] out=new double[instance.size()];
        for(int i=0;i<out.length;i++){
            double range=Math.abs(max.getValue(i)-min.getValue(i));
            double middle=Math.abs(max.getValue(i)+min.getValue(i))/2;
            out[i]=((instance.getValue(i)-middle)/range)*normalRange+normalMiddle;
        }
        return new SimpleInstance(out,instance.getWeight(),instance.isClassSet(),instance.getClassValue());
    }
}
