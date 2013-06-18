package tutorials.featureselection;

/**
 * %SVN.HEADER%
 */
import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaAttributeSelection;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;

/**
 * Tutorial how to use the Bridge to WEKA AS Evaluation , AS Search and
 * Evaluator algorithms in Java-ML
 * 
 * 
 * @author Irwan Krisna
 */
public class TutorialWekaAttributeSelection {

	public static void main(String[] args) throws IOException {
		/* Load data */
		Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"),
				4, ",");
		/* Create a AS Evaluation algorithm */
		ASEvaluation eval = new GainRatioAttributeEval();
		/* Create a Weka's AS Search algorithm */
		ASSearch search = new Ranker();
		/* Wrap Wekas' Algorithms in bridge */
		WekaAttributeSelection wekaattrsel = new WekaAttributeSelection(eval,
				search);
		/*
		 * to apply algorithm to the data set and generate the new data based on
		 * the given parameters
		 */
		wekaattrsel.build(data);
		/* to retrieve the number of attributes */
		System.out.println("Total number of attributes:  "
				+ wekaattrsel.noAttributes());
		/* to display all the rank and score for each attribute */
		for (int i = 0; i < wekaattrsel.noAttributes() - 1; i++) {
			System.out.println("Attribute  " + i + "  Ranks  "
					+ wekaattrsel.rank(i) + " and Scores "
					+ wekaattrsel.score(i));
		}

	}

}
