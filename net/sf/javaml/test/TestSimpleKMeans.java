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

import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.SimpleKMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;

public class TestSimpleKMeans extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TestSimpleKMeans.class);
    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans()'
     */
    public void testSimpleKMeans() {
        Clusterer k = new SimpleKMeans();
        Assert.assertTrue(k.getNumberOfClusters() == 2);

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans(int,
     * int)'
     */
    public void testSimpleKMeansIntInt() {
        Clusterer k = new SimpleKMeans(4, 100);
        Assert.assertTrue(k.getNumberOfClusters() == 4);
    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans(int,
     * int, Random)'
     */
    public void testSimpleKMeansIntIntRandom() {
        // TODO

    }

    /*
     * Test method for 'net.sf.javaml.clustering.SimpleKMeans.SimpleKMeans(int,
     * int, Random, DistanceMeasure)'
     */
    public void testSimpleKMeansIntIntRandomDistanceMeasure() {
        // TODO
    }

    /*
     * Test method for
     * 'net.sf.javaml.clustering.SimpleKMeans.buildClusterer(Dataset)'
     */
    public void testBuildClusterer() {
        Dataset data = new SimpleDataset();
        Random rg = new Random(1);

        for (int i = 0; i < 3; i++) {
            int x = rg.nextInt(100) - 50;
            int y = rg.nextInt(100) - 50;
            for (int j = 0; j < 10; j++) {
                float[] vec = {(float) rg.nextGaussian() * 10 + x,(float) rg.nextGaussian() * 10 + y };
                Instance instance = new SimpleInstance(vec);
                data.addInstance(instance);
            }
        }
        Clusterer km=new SimpleKMeans();
        km.buildClusterer(data);
    }

    /*
     * Test method for
     * 'net.sf.javaml.clustering.SimpleKMeans.getNumberOfClusters()'
     */
    public void testGetNumberOfClusters() {
        Clusterer k = new SimpleKMeans(4, 100);
        Assert.assertTrue(k.getNumberOfClusters() == 4);
        Clusterer l = new SimpleKMeans();
        Assert.assertTrue(l.getNumberOfClusters() == 2);
    }

    /*
     * Test method for
     * 'net.sf.javaml.clustering.SimpleKMeans.predictCluster(Instance)'
     */
    public void testPredictCluster() {
        // TODO
    }

    /*
     * Test method for
     * 'net.sf.javaml.clustering.SimpleKMeans.predictMembershipDistribution(Instance)'
     */
    public void testPredictMembershipDistribution() {
        // TODO
    }

}
