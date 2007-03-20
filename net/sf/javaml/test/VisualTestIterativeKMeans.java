/** VisualTestMultiKMeans.java
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
 * Copyright (c) 2006-2007, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.javaml.clustering.IterativeKMeans;
import net.sf.javaml.clustering.evaluation.*;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetGenerator;

public class VisualTestIterativeKMeans extends JPanel {

     private static final long serialVersionUID = 5608683822417234316L;

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Iterative K-means test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new VisualTestIterativeKMeans());
        window.pack();
        window.setVisible(true);

    }

    public VisualTestIterativeKMeans() {
        this.setLayout(new GridLayout(0, 5));
        int space = 200;
        Dataset data = DatasetGenerator.createClusterSquareDataset(200, 8,200);

        this.add(createLabel(data, Color.BLACK, space, space, null, null, 0));
        DistanceMeasure dm=new EuclideanDistance();
        //IterativeKMeans km = new IterativeKMeans(2, 20,100,dm,new Tau(dm));
        IterativeKMeans km = new IterativeKMeans(2, 20,100,dm,new SumOfSquaredErrors(dm));
        

        Dataset[] datas=km.executeClustering(data);
        
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA,Color.CYAN };
        for (int i = 0; i < datas.length; i++) {
            this.add(createLabel(datas[i], colors[i%5], space, space, km, colors, i));
        }

    }

    private JLabel createLabel(Dataset data, Color color, int width, int height, IterativeKMeans km, Color[] colors, int i) {
        return new ClusterLabel(data, color, width, height, km, colors, i);
    }

    class ClusterLabel extends JLabel {

        public void setData(Dataset x) {
            this.data = x;
        }

        private static final long serialVersionUID = -5642750374875570050L;

        private Dataset data;

        private Color color;

    
        public ClusterLabel(Dataset data, Color color, int width, int height, IterativeKMeans km2, Color[] colors, int i) {
            this.setPreferredSize(new Dimension(width, height));
            this.data = data;
            this.color = color;
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            for (int i = 0; i < data.size(); i++) {
                Instance in = data.getInstance(i);
                g.fillOval((int) in.getValue(0) - 1, (int) in.getValue(1) - 1, 2, 2);

            }
//            if (km != null) {
//                Instance[] centroids = km.getCentroids();
//                for (int i = 0; i < centroids.length; i++) {
//                    g.setColor(Color.GRAY);
//                    g.fillRect((int) centroids[i].getValue(0) - 4, (int) centroids[i].getValue(1) - 4, 8, 8);
//
//                }
//                try{
//                g.setColor(Color.BLACK);
//                g.fillRect((int) centroids[tmpI].getValue(0) - 4, (int) centroids[tmpI].getValue(1) - 4, 8, 8);
//                }catch(Exception e){
//                    System.err.println("Error with tmpI: "+tmpI);
//                }
//
//            }

        }

    }
}
