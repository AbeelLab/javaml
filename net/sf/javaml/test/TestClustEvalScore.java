package net.sf.javaml.test;


import net.sf.javaml.clustering.SimpleKMeans;
import net.sf.javaml.clustering.evaluation.CIndex;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.GPlus;
import net.sf.javaml.clustering.evaluation.Gamma;
import net.sf.javaml.clustering.evaluation.PointBiserial;
import net.sf.javaml.clustering.evaluation.Tau;
import net.sf.javaml.clustering.evaluation.WB;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.tools.DatasetGenerator;


public class TestClustEvalScore {
	
	public static void main(String[] args){
		int space = 200;
		Dataset data = new SimpleDataset();
		data = DatasetGenerator.createClusterSquareDataset(space, 8);
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}
		SimpleKMeans km = new SimpleKMeans(4, 500);
        km.buildClusterer(data);
		ClusterEvaluation ce=new GPlus();
		double score = ce.score(km,data);
		System.out.println("score: "+score);
	}
}
