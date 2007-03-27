/**
 * VisualTestAQBC.java, 28-feb-2007
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
 * Copyright (c) 2007, Thomas Abeel, Andreas De Rijcke
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.javaml.clustering.AdaptiveQualityBasedClustering;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetGenerator;
import net.sf.javaml.data.DatasetLoader;

public class VisualTestAQBC  extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 7732700118221667424L;

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("AQBC test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new VisualTestAQBC());
        window.pack();
        window.setVisible(true);

    }

    public VisualTestAQBC() {
        this.setLayout(new GridLayout(0, 3));
        int space = 300;
        //Dataset data = DatasetGenerator.createClusterSquareDataset3D(space, 20,1000);
        Dataset data;
		try {
			data = DatasetLoader.loadDataset(new File("clustervolume25.data"));
		
        this.add(createLabel(data, Color.BLACK, space, space, null, null, 0));
        AdaptiveQualityBasedClustering km = new AdaptiveQualityBasedClustering();
        Dataset[] clusters = km.executeClustering(data);
        
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA };
        for (int i = 0; i < clusters.length; i++) {
        	System.out.println("Visual test: "+clusters[i].size());
            this.add(createLabel(clusters[i], colors[i], space, space, km, colors, i));
        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //CIndex cindex=new CIndex(new EuclideanDistance());
        //System.out.println("C-index score: "+cindex.score(clusters));
        

    }

    private JLabel createLabel(Dataset data, Color color, int width, int height, AdaptiveQualityBasedClustering km, Color[] colors, int i) {
        return new ClusterLabel(data, color, width, height, km, colors, i);
    }

    class ClusterLabel extends JLabel {

        public void setData(Dataset x) {
            this.data = x;
        }

        private static final long serialVersionUID = -5642750374875570050L;

        private Dataset data;

        private Color color;

     
       
        public ClusterLabel(Dataset data, Color color, int width, int height, AdaptiveQualityBasedClustering km, Color[] colors, int i) {
            this.setPreferredSize(new Dimension(width, height));
            this.data = data;
            this.color = color;
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            System.out.println("Visual PC: "+data.size());
            for (int i = 0; i < data.size(); i++) {
                Instance in = data.getInstance(i);
                //System.out.println("Datapoint: "+in.getValue(1)+","+in.getValue(2));
                
                g.fillOval((int) in.getValue(1) - 1, (int) in.getValue(2) - 1, 2, 2);

            }
            /*if (km != null) {
                Instance[] centroids = km.getCentroids();
                for (int i = 0; i < centroids.length; i++) {
                    g.setColor(Color.GRAY);
                    g.fillRect((int) centroids[i].getValue(0) - 4, (int) centroids[i].getValue(1) - 4, 8, 8);

                }
                g.setColor(Color.BLACK);
                g.fillRect((int) centroids[tmpI].getValue(0) - 4, (int) centroids[tmpI].getValue(1) - 4, 8, 8);

            }
*/
        }

    }

}