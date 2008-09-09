/**
 * TestLibSVM.java
 *
 * %SVN.HEADER%
 */
package junit.classification.svm;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

import external.libsvm.LibSVM;

public class TestLibSVM {

    @Test
    public void testLibSVM() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            LibSVM ls = new LibSVM();
            // ls.buildClassifier(data);

            ls.buildClassifier(data);
            data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            int t = 0, f = 0;
            for (Instance i : data)
                if (i.classValue().equals(ls.classify(i)))
                    t++;
                else
                    f++;
            System.out.println("Correct: " + t);
            System.out.println("Wrong: " + f);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void testLibSVMSparse() {
        try {
            Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.tsv"), 0, " ", ":");
            LibSVM ls = new LibSVM();
            ls.buildClassifier(data);
            data = FileHandler.loadSparseDataset(new File("devtools/data/sparse.tsv"), 0, " ", ":");
            int t = 0, f = 0;
            for (Instance i : data)
                if (i.classValue().equals(ls.classify(i)))
                    t++;
                else
                    f++;
            System.out.println("Correct: " + t);
            System.out.println("Wrong: " + f);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
