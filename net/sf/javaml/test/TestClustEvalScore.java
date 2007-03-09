package net.sf.javaml.test;

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
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetGenerator;

public class TestClustEvalScore {

	public static void main(String[] args) {
		int space = 200;
		Dataset data = new SimpleDataset();
		data = DatasetGenerator.createClusterSquareDataset(space, 8);
		if (data.size() == 0) {
			throw new RuntimeException("The dataset should not be empty");
		}
		for (int i = 0; i < 10; i++) {
			SimpleKMeans km = new SimpleKMeans(4, 500);
			Dataset[] clusters = km.executeClustering(data);
			for (int j = 0; j <  clusters.length; j++) {
				System.out.println("cluster: " + j + ": size: "
						+ clusters[j].size());
			}
			ClusterEvaluation ce = new CIndex(new EuclideanDistance());
			double score = ce.score(clusters);
			System.out.println("score: " + score);
		}
	}
}
