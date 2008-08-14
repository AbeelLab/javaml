/**
 * %SVN.HEADER%
 */
package tutorials.tools;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Test;

/**
 * This tutorial shows how to load data from a local file.
 * 
 * The two files that are used here can be retrieved from the SVN or at the
 * following URLS:
 * 
 * {@linkplain http
 * ://java-ml.svn.sourceforge.net/viewvc/java-ml/trunk/devtools/data/iris.data}
 * and {@linkplain http
 * ://java-ml.svn.sourceforge.net/viewvc/java-ml/trunk/devtools/data/sparse.tsv}
 * .
 * 
 * Check out these two files for the dense and sparse file formats. The class
 * label can have any value, the other attributes should be numbers.
 * 
 * @author Thomas Abeel
 * 
 */
public class TutorialDataLoader {

	@Test
	public void testLoader() {
		try {
			Dataset data = FileHandler.loadDataset(new File(
					"devtools/data/iris.data"), 4, ",");
			System.out.println(data);
			data = FileHandler.loadSparseDataset(new File(
					"devtools/data/sparse.tsv"), 0, " ", ":");
			System.out.println(data);
		} catch (IOException e) {
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void testSparseLoader(){
	    try{
	        Dataset data =  FileHandler.loadSparseDataset(new File(
            "devtools/data/sparse.txt.gz"), 0, ";",":");
	        System.out.println(data.classes());
	        
	    }catch(IOException e){
	        Assert.assertFalse(true);
	    }
	}
}
