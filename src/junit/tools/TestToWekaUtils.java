/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.tools.weka.ToWekaUtils;

import org.junit.Test;

import weka.classifiers.functions.SMO;
import weka.core.Instances;

public class TestToWekaUtils {

    @Test
    public void testLoadDataset() {
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4, ",");
            System.out.println(data);
            ToWekaUtils twu = new ToWekaUtils(data);
            Instances insts = twu.getDataset();
            System.out.println(insts);

            SMO smo = new SMO();
            smo.buildClassifier(insts);
            for (Instance i : data) {
                System.out.println(i.classValue() + "\t"
                        + twu.convertClass(smo.classifyInstance(twu.instanceToWeka(i))));
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
