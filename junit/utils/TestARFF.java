package junit.utils;

import java.io.File;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.weka.ARFF;

import org.junit.Assert;
import org.junit.Test;

public class TestARFF {
	@Test
	public void testSparse() {
		try {
			Dataset arff = ARFF
					.readARFF(new File("devtools/data/ratings.arff"));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
