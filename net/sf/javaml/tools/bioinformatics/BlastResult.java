/**
 * BlastResult.java
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
package net.sf.javaml.tools.bioinformatics;



import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AbstractDistance;

public class BlastResult extends AbstractDistance implements Dataset {
    /**
     * The mapping of gene names to indices in the mapping
     */
     private HashMap<String, Integer> mapping;

    /**
     * The collection of gene instances, they have the same index as in the
     * mapping
     */
    private Vector<GeneInstance> genes;

    /**
     * The mapping of two gene indices to a distance. This distance is a
     * derivative of the BLAST e-value.
     */
     private HashMap<Point, Double> distances;

    
    /**
     * XXX DOC
     * 
     * @param blastResult
     *            the result of a blast.
     * @param distanceExponent
     */
    public BlastResult(File blastResult) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(blastResult));
            String line = in.readLine();
            int index = 0;
            mapping = new HashMap<String, Integer>();
            genes = new Vector<GeneInstance>();
            distances=new HashMap<Point,Double>();
            while (line != null) {

                String[] arr = line.split("\t");
                try {
                    Integer.parseInt(arr[2]);
                    if (!mapping.containsKey(arr[0])) {
                        genes.add(new GeneInstance(arr[0]));
                        mapping.put(arr[0], index++);
                    }
                    if (!mapping.containsKey(arr[1])) {
                        genes.add(new GeneInstance(arr[1]));
                        mapping.put(arr[1], index++);
                    }
                    int x = mapping.get(arr[0]);
                    int y = mapping.get(arr[1]);
                    double dist = calculateDist(arr[10]);
                    if (dist > maxDistance)
                        maxDistance = dist;
                    if (dist < minDistance)
                        minDistance = dist;
                    if(!distances.containsKey(new Point(x,y))&&!distances.containsKey(new Point(y,x))){
                        distances.put(new Point(x, y), dist);
                    }
                } catch (RuntimeException e) {
                    // column with index 2 is not an int, this is probably a
                    // header line, ignore
                }
                line = in.readLine();
            }
            in.close();
            

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Construction of the BlastResult dataset failed!");
        }

    }

   private double calculateDist(String string) {
        double value = Double.parseDouble(string);
        return value;
        // value = 1 / Math.pow(Math.abs(Math.log10(value)), 1.0 /
        // distanceExponent);
        // return (float) value;
    }

    public boolean addInstance(Instance i) {
        // TODO it should be possible to add Gene instances, in all other cases
        // this should throw an exception
        return false;
    }

    public int getIndex(Instance inst) {
        // TODO implement
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Instance getInstance(int index) {
        return genes.get(index);
    }

    public Instance getMaximumInstance() {
        return null;
    }

    public Instance getMinimumInstance() {
        return null;
    }

    public int size() {
        return mapping.size();
    }

    private double maxDistance = 0;

    private double minDistance = 1;

    

    public double getMaximumDistance(Dataset data) {
        return maxDistance;
    }

    public double getMinimumDistance(Dataset data) {
        return minDistance;
    }

    public double calculateDistance(Instance i, Instance j) {
        int x = mapping.get(i);
        int y = mapping.get(j);
        Point a = new Point(x, y);
        Point b = new Point(y, x);
        if (distances.containsKey(a))
            return distances.get(a);
        else if (distances.containsKey(b))
            return distances.get(b);
        else
            return 1;
    }

    public int getNumClasses() {
        return 1;
    }

}
