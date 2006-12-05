package net.sf.javaml.test;

import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.javaml.core.Instance;

public class VisualizeClustering extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6890541400222963617L;

	public static void visual(Vector<Vector<Instance>>data){
		JFrame window=new JFrame("Visualizing data - "+data.size()+" clusters" );
		window.setContentPane(new JScrollPane(new VisualizeClustering(data)));
		window.pack();
		window.setVisible(true);
	}
	
	public VisualizeClustering (Vector<Vector<Instance>>data){
		
		this.setLayout(new GridLayout(0, 5));
		for(int i=0;i<data.size();i++){
			this.add(new ClusterLabel(data.get(i),200,"Cluster: "+i+" Size: "+data.get(i).size()));
			
		}
		
		
	}
}
