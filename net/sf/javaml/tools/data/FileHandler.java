/**
 * FileHandler.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.SparseInstance;
import be.abeel.util.ColumnIterator;
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
        return loadDataset(f, -1, separator);
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
        return loadDataset(f, -1);

    }

    private static Dataset loadSparse(java.io.InputStream in, int classIndex, String attSep, String indexSep) {

        ColumnIterator it = new ColumnIterator(in);
        it.setDelimiter(attSep);
        it.setSkipBlanks(true);
        it.setSkipComments(true);
        Dataset out = new DefaultDataset();
        for (String[] arr : it) {
            SparseInstance inst = new SparseInstance();
            // double[] values;
            // if (classIndex == -1)
            // values = new double[arr.length];
            // else
            // values = new double[arr.length - 1];
        
            for (int i = 0; i < arr.length; i++) {
                if (i == classIndex) {
                    inst.setClassValue(arr[i]);
                } else {
                    String[]tmp=arr[i].split(indexSep);
                    double val;
                    try {
                        val = Double.parseDouble(tmp[1]);
                    } catch (NumberFormatException e) {
                        val = Double.NaN;
                    }
                    inst.put(Integer.parseInt(tmp[0]), val);
                }
            }
            out.add(inst);

        }
        return out;
    }

    private static Dataset load(java.io.InputStream in, int classIndex, String separator) {

        LineIterator it = new LineIterator(in);
        it.setSkipBlanks(true);
        it.setSkipComments(true);
        Dataset out = new DefaultDataset();
        for (String line : it) {
            String[] arr = line.split(separator);
            double[] values;
            if (classIndex == -1)
                values = new double[arr.length];
            else
                values = new double[arr.length - 1];
            String classValue = null;
            for (int i = 0; i < arr.length; i++) {
                if (i == classIndex) {
                    try {
                        classValue = arr[i];

                    } catch (Exception e) {
                        // System.err.println(f);
                        System.err.println("$" + line + "$");
                        System.exit(-1);
                    }
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
            out.add(new DenseInstance(values, classValue));

        }
        return out;
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
        if (f.getName().endsWith("gz"))
            return load(new GZIPInputStream(new FileInputStream(f)), classIndex, separator);
        if (f.getName().endsWith("zip"))
            return load(new ZipInputStream(new FileInputStream(f)), classIndex, separator);
        return load(new FileInputStream(f), classIndex, separator);

    }

    public static Dataset loadSparseDataset(File f, int classIndex, String attributeSeparator, String indexSep)
            throws IOException {
        if (f.getName().endsWith("gz"))
            return loadSparse(new GZIPInputStream(new FileInputStream(f)), classIndex, attributeSeparator, indexSep);
        if (f.getName().endsWith("zip"))
            return loadSparse(new ZipInputStream(new FileInputStream(f)), classIndex, attributeSeparator, indexSep);
        return loadSparse(new FileInputStream(f), classIndex, attributeSeparator, indexSep);
    }

}
