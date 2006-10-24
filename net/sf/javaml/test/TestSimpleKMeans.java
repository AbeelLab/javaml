/**
 * TestSimpleKMeans.java, 24-okt-2006
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
package net.sf.javaml.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.SimpleKMeans;

public class TestSimpleKMeans extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestSimpleKMeans.class);
    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans()'
     */
    public void testSimpleKMeans() {
        Clusterer k=new SimpleKMeans();
        Assert.assertTrue(k.getNumberOfClusters()==2);
        
    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans(int, int)'
     */
    public void testSimpleKMeansIntInt() {

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans(int, int, Random)'
     */
    public void testSimpleKMeansIntIntRandom() {

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans(int, int, Random, DistanceMeasure)'
     */
    public void testSimpleKMeansIntIntRandomDistanceMeasure() {

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.buildClusterer(Dataset)'
     */
    public void testBuildClusterer() {

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.getNumberOfClusters()'
     */
    public void testGetNumberOfClusters() {

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.predictCluster(Instance)'
     */
    public void testPredictCluster() {

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.predictMembershipDistribution(Instance)'
     */
    public void testPredictMembershipDistribution() {

    }

}
