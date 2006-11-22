/**
 * ClusterEvaluationFactory.java, 16-nov-2006
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering.evaluation;

public class ClusterEvaluationFactory {

    public static ClusterEvaluation getSumOfSquaredErrors(){
        return new SumOfSquaredErrors();
    }
    public static ClusterEvaluation getSumOfAveragePairWiseSimilarities(){
        return new SumOfAveragePairwiseSimilarities();
    }
    public static ClusterEvaluation getSumOfCentroidSimilarities(){
        return new SumOfCentroidSimilarities();
    }
    public static ClusterEvaluation getHybridPairwiseSimilarities(){
        return new HybridPairwiseSimilarities();
    }
    public static ClusterEvaluation getHybridCentroidSimilarity(){
        return new HybridCentroidSimilarity();
    }
    public static ClusterEvaluation getTraceScatterMatrix(){
        return new TraceScatterMatrix();
    }
    public static ClusterEvaluation getMinMaxCut(){
        return new MinMaxCut();
    }
    public static ClusterEvaluation getBICScore(){
        return new BICScore();
    }
    public static ClusterEvaluation getAICScore(){
        return new AICScore();
    }
}
