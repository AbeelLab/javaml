/**
 * %SVN.HEADER%
 */
package net.sf.javaml.featureselection;


public class FeatureSelection {

    /**
     * Returns the indices of the best <code>threshold</code> fraction features. The AttributeRanking
     * should be pre-trained.
     * 
     */
    public static int[] bestFeatures(AttributeRanking ar, double threshold) {

        int[] tmpRanking = new int[ar.noFeatures()];
        for (int i = 0; i < ar.noFeatures(); i++)
            tmpRanking[ar.rank(i)] = i;

        int[] outRanking = new int[(int) (ar.noFeatures() * threshold + 0.5)];
        System.arraycopy(tmpRanking, 0, outRanking, 0, outRanking.length);
        return outRanking;

    }
}
