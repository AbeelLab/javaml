/**
 * VisualTestSimpleKMeans.java, 25-okt-2006
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.XMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.tools.DatasetGenerator;

public class VisualTestXMeans extends JPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 7732700118221667424L;

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame window=new JFrame("X-means test");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new VisualTestXMeans());
        window.pack();
        window.setVisible(true);

    }
    
    public VisualTestXMeans(){
        this.setLayout(new GridLayout(0,3));
        int space=300;
        Dataset data =DatasetGenerator.createClusterDataset(4,100,space/2,5);
        
        this.add(createLabel(data,Color.BLACK,space,space));
        Clusterer km=new XMeans(3,4);
        km.buildClusterer(data);
        
        Dataset[]datas=new Dataset[5*2];
        for(int i=0;i<5*2;i++){
            datas[i]=new SimpleDataset();
        }
        for(int i=0;i<data.size();i++){
            Instance in=data.getInstance(i);
            datas[km.predictCluster(in)].addInstance(in);
        }
        Color[]colors={Color.RED,Color.GREEN,Color.BLUE,Color.CYAN,Color.MAGENTA};
        for(int i=0;i<5*2;i++){
            this.add(createLabel(datas[i],colors[i%5],space,space));
        }
        
    }
    
    private JLabel createLabel(Dataset data,Color color,int width,int height){
        return new ClusterLabel(data,color,width,height);
    }
    class ClusterLabel extends JLabel{
        
        public void setData(Dataset x){
            this.data=x;
        }
        private static final long serialVersionUID = -5642750374875570050L;

        private Dataset data;
        private Color color;
        private int width,height;
        public ClusterLabel(Dataset data, Color color, int width, int height) {
            this.setPreferredSize(new Dimension(width,height));
             this.data = data;
            this.color = color;
            this.width = width;
            this.height = height;
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(color);
            for(int i=0;i<data.size();i++){
                Instance in=data.getInstance(i);
                g.fillOval((int)in.getValue(0)-1,(int)in.getValue(1)-1,2,2);
                
            }
            
            
            
        }
        
        
       
        
    }

}
