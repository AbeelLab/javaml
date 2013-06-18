/**
 * %SVN.HEADER%
 */
package junit.filter;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.featureselection.scoring.RELIEF;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

public class TestRELIEF {
    @Test
    public void testRELIEFSynthetic() {
        // double[] vals = { 1, 2, 3, 4, 5, 6 };

        Dataset data = new DefaultDataset();
        for (int i = 0; i < 100; i++) {
            double[] vals = { vary(5 * (i / 50), 1), vary(1, 0),Math.random() };
            Instance inst = new DenseInstance(vals, i /50);
            data.add(inst);

        }
//        NOrma dnm=new DatasetNormalizeMidrange(0.5,1);
//        dnm.build(data);
//        Dataset test=dnm.filterDataset(data);
        
        System.out.println(data);
        RELIEF ga = new RELIEF();
        //ga.setNumNeigbors(4);
     
        ga.build(data);
     
        System.out.println(ga.score(0));
        System.out.println(ga.score(1));
        System.out.println(ga.score(2));
        
      
       

    }
    
    @Test
    public void testRELIEFReal(){
        try {
            Dataset data = FileHandler.loadDataset(new File("devtools/data/iris.data"), 4,",");
            RELIEF ga = new RELIEF();
            //ga.setNumNeigbors(4);
         
            ga.build(data);
         
            System.out.println(ga.score(0));
            System.out.println(ga.score(1));
            System.out.println(ga.score(2));
            System.out.println(ga.score(3));
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double vary(double i, double j) {
        return i + j * Math.random();
    }
}
