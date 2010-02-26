/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import libsvm.LibSVM;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

import be.abeel.util.LineIterator;

public class TestFileHandler {

	@Test
	public void testReadSparse() {
		try {
			Dataset data = FileHandler.loadSparseDataset(new File("devtools/data/testsparse.txt"), 0, " ", ":");
			Dataset data2 = FileHandler.loadSparseDataset(new File("devtools/data/testsparse2.txt"), 0, " ", ":");
			Assert.assertEquals(52, data.noAttributes());

		} catch (IOException e) {
			Assert.fail();
		}
	}

	@Test
	public void testReadSparseX() {
		try {
			Dataset data = new DefaultDataset();

			for (String line : new LineIterator("devtools/data/testsparse.txt")) {
				net.sf.javaml.core.SparseInstance inst = new net.sf.javaml.core.SparseInstance();
				StringTokenizer stok = new StringTokenizer(line);
				while (stok.hasMoreTokens()) {
					String tok = stok.nextToken();
					if (tok.equals("-1")) {
						inst.setClassValue(new Integer(0));
					} else {
						if (tok.equals("1")) {
							inst.setClassValue(new Integer(1));
						} else {
							int pos = Integer.parseInt(tok.substring(0, tok.indexOf(":")));
							double value = Double.parseDouble(tok.substring(tok.indexOf(":") + 1));

							// System.out.println("pos/value : " + pos + "/" +
							// value);
							inst.put(pos, value);
						}
					}
				}
				data.add(inst);
				// line = reader.readLine();
			}
			Assert.assertEquals(52, data.noAttributes());
		} catch (IOException e) {
			Assert.fail();
		}

	}
}
