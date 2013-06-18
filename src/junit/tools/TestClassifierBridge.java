/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.WekaClassifier;

import org.junit.Test;

import weka.classifiers.functions.SMO;

public class TestClassifierBridge {

    @Test
    public void testLoadDataset() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            System.out.println(data);
            SMO smo = new SMO();
            WekaClassifier cb = new WekaClassifier(smo);

            cb.buildClassifier(data);
            
            for (Instance i : data) {
               System.out.println(i.classValue()+"\t"+cb.classify(i));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
