/**
 * FileHandler.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import be.abeel.util.LineIterator;

/**
 * A class to load data sets from file and write them back.
 * <p>
 * The format of the file should be as follows: One instance on each line, all
 * values of an entry on a single line, values should be separated by a tab
 * character or specified with the method and all entries should have the same
 * number of values.
 * <p>
 * Only class values are allowed to be non double. If a data set has no labels,
 * then all columns should be double values.
 * 
 * {@jmlSource}
 * 
 * @see net.sf.javaml.core.Dataset
 * @see net.sf.javaml.core.Instance
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class FileHandler {

    /**
     * Utility method to load from a file without class set.
     * 
     * @param f
     * @param separator
     * @return
     * @throws IOException
     */
    public static Dataset loadDataset(File f, String separator) throws IOException {
        return loadDataset(f, Integer.MAX_VALUE, separator);
    }

    /**
     * This method will load the data stored in a file..
     * <p>
     * Only the column with the class values is allowed to have different values
     * than doubles.
     * <p>
     * Symbols that cannot be parsed to numbers will be converted to missing
     * values.
     * 
     * @param f
     *            the file to be loaded.
     * @param classIndex
     *            the index of the column that contains the class labels. This
     *            index starts from zero for the first column and should not be
     *            negative.
     */
    public static Dataset loadDataset(File f, int classIndex) throws IOException {
        return loadDataset(f, classIndex, "\t");
    }

    public static Dataset loadDataset(File f) throws IOException {
        return loadDataset(f, Integer.MAX_VALUE, "\t");
    }

    /**
     * Load the data from a file.
     * <p>
     * All columns should only contain double values, except the class column
     * which can only contain integer values.
     * <p>
     * Values that cannot be parsed to numbers will be entered as missing values
     * in the instances.
     * <p>
     * When the classIndex is outside the range of available attributes, all
     * instances will have the same class.
     * 
     * @param f
     *            the file to be loaded.
     */
    public static Dataset loadDataset(File f, int classIndex, String separator) throws IOException {
        LineIterator it = new LineIterator(f);
        it.setSkipBlanks(true);
        Dataset out = new SimpleDataset();
        for (String line : it) {
            String[] arr = line.split(separator);
            double[] values = new double[arr.length];
            int classValue = 0;
            for (int i = 0; i < arr.length; i++) {
                if (i == classIndex) {
                    classValue = Integer.parseInt(arr[i]);
                } else {
                    double val;
                    try {
                        val = Double.parseDouble(arr[i]);
                    } catch (NumberFormatException e) {
                        val = Double.NaN;
                    }
                    if (i > classIndex)
                        values[i - 1] = val;
                    else
                        values[i] = val;
                }
            }
            if (classIndex == Integer.MAX_VALUE)
                out.add(new SimpleInstance(values));
            else
                out.add(new SimpleInstance(values, classValue));
        }
        return out;

    }

    /**
     * Writes a data set to a file.
     * 
     * NOTE: The weight of instances will never be written to the file.
     * 
     * NOTE: The class will always be written on index 0. If no class value is
     * available, the line will start with a separator character.
     * 
     * @param data
     * @param f
     * @param classIndex
     */
    public static void writeDataset(Dataset data, File f, String separator) throws IOException {
        PrintWriter out = new PrintWriter(f);
        for (Instance inst : data) {
            if (inst.isClassSet())
                out.print(inst.classValue());

            for (int i = 0; i < inst.size(); i++) {
                out.print(separator);
                out.print(inst.value(i));
            }
            out.println();
        }
        out.close();

    }
}
