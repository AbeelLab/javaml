package net.sf.javaml.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JLabel;

import net.sf.javaml.core.Instance;

public class ClusterLabel extends JLabel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3146348959085477532L;
	private static Color[]colors= { Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA,Color.CYAN };
	private static int colorIndex=0;
	private Color color;
	private Vector<Instance>data;
	private int dimension;
	private String msg;
	public ClusterLabel(Vector<Instance>data,int dimension,String msg){
		this.color=colors[colorIndex];
		this.data=data;
		colorIndex++;
		colorIndex%=5;
		this.dimension=dimension;
		super.setPreferredSize(new Dimension(dimension,dimension));
		this.msg=msg;
		
	}
	@Override
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        for (int i = 0; i < data.size(); i++) {
            Instance in = data.get(i);
            g.fillOval((int) in.getValue(0) - 1, (int) in.getValue(1) - 1, 2, 2);

        }
        g.setColor(Color.black);
        g.drawString(msg, 5, 12);
        g.drawRect(0,0,dimension-1, dimension-1);
        

    }
}
