/**
 * %SVN.HEADER%
 */
package junit;

import tutorials.classification.TutorialCrossValidation;
import tutorials.classification.TutorialEvaluateDataset;
import tutorials.classification.TutorialKDtreeKNN;
import tutorials.classification.TutorialKNN;
import tutorials.classification.TutorialLibSVM;
import tutorials.classification.TutorialRandomForest;
import tutorials.clustering.TutorialClusterEvaluation;
import tutorials.clustering.TutorialKMeans;
import tutorials.core.TutorialDataset;
import tutorials.core.TutorialDenseInstance;
import tutorials.core.TutorialSparseInstance;
import tutorials.featureselection.TutorialFeatureSubsetSelection;
import tutorials.tools.TutorialARFFLoader;
import tutorials.tools.TutorialDataLoader;
import tutorials.tools.TutorialStoreData;

public class RunTutorials {

    public static void main(String[] args) throws Exception {
        /* Classification */
        TutorialCrossValidation.main(null);
        TutorialEvaluateDataset.main(null);
        TutorialKDtreeKNN.main(null);
        TutorialKNN.main(null);
        TutorialLibSVM.main(null);
        TutorialRandomForest.main(null);
        /* Clustering */
        TutorialClusterEvaluation.main(null);
        TutorialKMeans.main(null);
        /* Core */
        TutorialDataset.main(null);
        TutorialDenseInstance.main(null);
        TutorialSparseInstance.main(null);
        /* Feature selection */
        TutorialFeatureSubsetSelection.main(null);
        /* Toolds */
        TutorialARFFLoader.main(null);
        TutorialDataLoader.main(null);
        TutorialStoreData.main(null);

    }
}
