package net.sf.javaml.test;


import net.sf.javaml.clustering.SimpleKMeans;
import net.sf.javaml.clustering.evaluation.CIndex;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.Gamma;
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
		ClusterEvaluation ce=new Gamma();
		double score = ce.score(km,data);
		System.out.println("score: "+score);
	}
}
