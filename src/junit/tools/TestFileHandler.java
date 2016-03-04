/**
 * %SVN.HEADER%
 */
package junit.tools;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.tools.data.FileHandler;

import org.junit.Assert;
import org.junit.Test;

import be.abeel.io.LineIterator;

public class TestFileHandler {

	@Test
	public void testReadDense() {
		Dataset data = new DefaultDataset();
		for (int i = 0; i < 10; i++) {
			Instance tmpInstance = InstanceTools.randomInstance(25);
			tmpInstance.setClassValue("test");
			data.add(tmpInstance);
		}
		try {
			FileHandler.exportDataset(data, new File("testfile.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		try {
			data = FileHandler.loadDataset(new File("testfile.txt"),0);
		} catch (IOException e) {

			e.printStackTrace();
			Assert.fail();
		}
		for (Instance i : data) {
			Assert.assertEquals("test",i.classValue());
		}
		if (!new File("testfile.txt").delete()) {
			Assert.fail("Failed to remove test file");
		}
	}

	@Test
	public void testWriteDense() {
		Dataset data = new DefaultDataset();
		for (int i = 0; i < 10; i++) {
			Instance tmpInstance = InstanceTools.randomInstance(25);
			tmpInstance.setClassValue("test");
			data.add(tmpInstance);
		}
		try {
			FileHandler.exportDataset(data, new File("testfile.txt"));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		LineIterator it = new LineIterator(new File("testfile.txt"));
		for (String line : it) {
			if (!line.startsWith("test"))
				Assert.fail("All lines should start with the class value");
		}
		it.close();
		if (!new File("testfile.txt").delete()) {
			Assert.fail("Failed to remove test file");
		}

	}

	@Test
	public void testReadSparse() {
		try {
			Dataset data = FileHandler.loadSparseDataset(new File(
					"devtools/data/testsparse.txt"), 0, " ", ":");

			Assert.assertEquals(52, data.noAttributes());

		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testReadSparseX() {

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
						int pos = Integer.parseInt(tok.substring(0,
								tok.indexOf(":")));
						double value = Double.parseDouble(tok.substring(tok
								.indexOf(":") + 1));

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

	}
}
