package net.sf.javaml.test;

import net.sf.javaml.clustering.AdaptiveQualityBasedClustering;
import net.sf.javaml.clustering.IterativeKMeans;
import net.sf.javaml.clustering.SimpleKMeans;
import net.sf.javaml.clustering.evaluation.CIndex;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.GPlus;
import net.sf.javaml.clustering.evaluation.Gamma;
import net.sf.javaml.clustering.evaluation.HybridCentroidSimilarity;
import net.sf.javaml.clustering.evaluation.HybridPairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.MinMaxCut;
import net.sf.javaml.clustering.evaluation.PointBiserial;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.clustering.evaluation.Tau;
import net.sf.javaml.clustering.evaluation.TraceScatterMatrix;
import net.sf.javaml.clustering.evaluation.WB;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetGenerator;

public class TestClustEvalScore {

	public static void main(String[] args) {
		int space = 200;
		//Dataset data = DatasetGenerator.createClusterSquareDataset(space, 8);
		Dataset data = DatasetGenerator.createClusterSquareDataset3D(space, 30,100);
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}
		//ClusterEvaluation ce = new CIndex(new EuclideanDistance());
		//ClusterEvaluation ce = new Gamma(new EuclideanDistance());
		//ClusterEvaluation ce = new GPlus(new EuclideanDistance());
		//ClusterEvaluation ce = new HybridCentroidSimilarity(new EuclideanDistance());
		//ClusterEvaluation ce = new HybridPairwiseSimilarities(new EuclideanDistance());
		//ClusterEvaluation ce = new MinMaxCut(new EuclideanDistance());
		//ClusterEvaluation ce = new PointBiserial(new EuclideanDistance());
		//ClusterEvaluation ce = new SumOfAveragePairwiseSimilarities(new EuclideanDistance());
		//ClusterEvaluation ce = new SumOfCentroidSimilarities(new EuclideanDistance());
		//ClusterEvaluation ce = new SumOfSquaredErrors(new EuclideanDistance());
		ClusterEvaluation ce = new Tau(new EuclideanDistance());
		//ClusterEvaluation ce = new TraceScatterMatrix(new EuclideanDistance());
		//ClusterEvaluation ce = new WB(new EuclideanDistance());
		
		for (int i = 0; i < 10; i++) {
			//SimpleKMeans km = new SimpleKMeans(4, 200);
			//IterativeKMeans km = new IterativeKMeans(2, 10, 500,new EuclideanDistance(), ce);
			//MultiKMeans km = new MultiKMeans(4, 500);
			//IterativeMultiKMeans km = new IterativeMultiKMeans(4, 500);
			AdaptiveQualityBasedClustering km = new AdaptiveQualityBasedClustering();
			//Ant km = new Ant(100, 10);
			Dataset[] clusters = km.executeClustering(data);
			System.out.println("CLUSTERS: "+clusters);
			for (int j = 0; j <  clusters.length; j++) {
				System.out.println("cluster: " + j + ": size: "
						+ clusters[j].size());
			}
			double score = ce.score(clusters);
			System.out.println("score: " + score);
		}
	}
}
