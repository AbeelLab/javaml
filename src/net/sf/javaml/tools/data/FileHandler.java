/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import be.abeel.io.GZIPPrintWriter;
import be.abeel.io.UnicodeReader;

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
 * 
 * @see net.sf.javaml.core.Dataset
 * @see net.sf.javaml.core.Instance
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class FileHandler extends StreamHandler{

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
     * @param classIndex
     *            the index of the class value
     * @param separator
     *            the symbol used to separate two fields, typically ,(comma)
     *            ;(semi-colon) or \t (tab).
     * @return a data set containing the data from the file
     * @throws IOException
     */
    public static Dataset loadDataset(File f, int classIndex, String separator) throws IOException {
        if (f.getName().endsWith("gz"))
            return load(new UnicodeReader(new GZIPInputStream(new FileInputStream(f))), classIndex, separator);
        if (f.getName().endsWith("zip"))
            return load(new UnicodeReader(new ZipInputStream(new FileInputStream(f))), classIndex, separator);
        return load(new UnicodeReader(new FileInputStream(f)), classIndex, separator);

    }

    public static Dataset loadSparseDataset(File f, int classIndex) throws IOException {
        return loadSparseDataset(f, classIndex, "\t", ":");

    }

    public static Dataset loadSparseDataset(File f, int classIndex, String attributeSeparator, String indexSep)
            throws IOException {
        if (f.getName().endsWith("gz"))
            return loadSparse(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))), classIndex, attributeSeparator, indexSep);
        if (f.getName().endsWith("zip"))
            return loadSparse(new InputStreamReader(new ZipInputStream(new FileInputStream(f))), classIndex, attributeSeparator, indexSep);
        return loadSparse(new InputStreamReader(new FileInputStream(f)), classIndex, attributeSeparator, indexSep);
    }

    /**
     * Exports a data set to a file. Each instance is output separately with the
     * class label on position 0. The fields are delimited with a tab character.
     * 
     * Note: data sets with mixed sparse and dense instances may not be loadable
     * with the load methods.
     * 
     * @param data
     *            data set
     * @param outFile
     *            file to write data to
     * @param compress
     *            flag to indicate whether GZIP compression should be used.
     * @throws IOException
     *             when something went wrong during the export
     */
    public static void exportDataset(Dataset data, File outFile, boolean compress, String sep) throws IOException {
        PrintWriter out;
        if (compress)
            out = new GZIPPrintWriter(outFile);
        else
            out = new PrintWriter(outFile);
        for (Instance inst : data) {
            if (inst.classValue() != null)
                out.print(inst.classValue() +sep);
            out.println(string(inst,sep));
        }

        out.close();

    }

    private static String string(Instance inst,String sep) {
        StringBuffer out = new StringBuffer();
        if (inst instanceof SparseInstance) {
            for (Integer index : inst.keySet()) {
                if (out.length() != 0)
                    out.append(sep+ index + ":" + inst.value(index));
                else
                    out.append(index + ":" + inst.value(index));

            }
        } else {
            out.append(inst.value(0));
            for (int i = 1; i < inst.noAttributes(); i++)
                out.append(sep + inst.value(i));

        }

        return out.toString();
    }

    /**
     * Exports a data set to a file. Each instance is output separately with the
     * class label on position 0. The fields are delimited with a tab character.
     * 
     * Note: data sets with mixed sparse and dense instances may not be loadable
     * with the load methods.
     * 
     * By default compression of the output is turned off.
     * 
     * @param data
     *            data set
     * @param outFile
     *            file to write data to
     * @throws IOException
     *             when something went wrong during the export
     */
    public static void exportDataset(Dataset data, File file) throws IOException {
        exportDataset(data, file, false);

    }

	public static void exportDataset(Dataset data, File file, boolean b) throws IOException {
		exportDataset(data, file, b,"\t");
		
	}

}
