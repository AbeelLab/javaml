/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.data;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import be.abeel.io.LineIterator;

/**
 * Provides method to load data from ARFF formatted files.
 * 
 * For a detailed description on the ARFF format, please see
 * http://weka.wiki.sourceforge.net/ARFF
 * 
 * @author Thomas Abeel
 * 
 */
public class ARFFHandler {
    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read. This method does not
     * read class labels.
     * 
     * @param file
     *            the file to read the data from
     * 
     * @return the data set represented in the provided file
     * @throws FileNotFoundException
     *             if the file can not be found.
     */
    public static Dataset loadARFF(File file) throws FileNotFoundException {
        return loadARFF(file, -1);
    }

    /**
     * Load a data set from an ARFF formatted file. Due to limitations in the
     * Java-ML design only numeric attributes can be read.
     * 
     * @param file
     *            the file to read the data from
     * @param classIndex
     *            the index of the class label
     * @return the data set represented in the provided file
     * @throws FileNotFoundException
     *             if the file can not be found.
     */
    public static Dataset loadARFF(File file, int classIndex) throws FileNotFoundException {
        LineIterator it = new LineIterator(file);
        it.setSkipBlanks(true);
        it.setCommentIdentifier("%");
        it.setSkipComments(true);

        Dataset out = new DefaultDataset();

        /* Indicates whether we are reading data */
        boolean dataMode = false;
        for (String line : it) {
            /* When we passed the @data tag, we are reading data */
            if (dataMode) {
                String[] arr = line.split(",");
                double[] values;
                if (classIndex == -1)
                    values = new double[arr.length];
                else
                    values = new double[arr.length - 1];
                String classValue = null;
                for (int i = 0; i < arr.length; i++) {
                    if (i == classIndex) {
                        classValue = arr[i];
                    } else {
                        double val;
                        try {
                            val = Double.parseDouble(arr[i]);
                        } catch (NumberFormatException e) {
                            val = Double.NaN;
                        }
                        if (classIndex!=-1 && i > classIndex)
                            values[i - 1] = val;
                        else
                            values[i] = val;
                    }
                }
                out.add(new DenseInstance(values, classValue));
            }
            /* Ignore everything in the header, i.e. everything up to @data */
            if (line.equalsIgnoreCase("@data"))
                dataMode = true;
        }
        return out;
    }
}
