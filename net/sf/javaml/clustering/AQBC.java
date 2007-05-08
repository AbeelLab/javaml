/**
 * AQBC.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.utils.GammaFunction;
import net.sf.javaml.utils.MathUtils;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

/**
 * Based on an implementation of the Adaptive Quality Based Clustering algorithm
 * by Smet et al.
 * 
 * @author Thomas Abeel
 * 
 */
public class AQBC implements Clusterer {
    private float RADNW;

    private int E;

    class TaggedInstance extends SimpleInstance {

        private int tag;

        TaggedInstance(SimpleInstance i, int tag) {
            super(i);
            this.tag = tag;

        }

        public int getTag() {
            return tag;
        }
    }

    private Dataset data;

    public Dataset[] executeClustering(Dataset data) {
        this.data = data;
        // dm=new NormalizedEuclideanDistance(data);
        dm = new EuclideanDistance();
        // Filter filter=new NormalizeMean();
        // data=filter.filterDataset(data);
        Vector<TaggedInstance> SP = normalize(data);
        // Vector<Instance> SP = new Vector<Instance>();
        // for (int i = 0; i < norm.size(); i++) {
        // SP.add(data.getInstance(i));
        // }
        int NRNOCONV = 0;
        int maxNRNOCONV = 2;
        int TOOFEWPOINTS = 0;
        int TFPTH = 10;
        int BPR = 0;
        int RRES = 0;
        int BPRTH = 10;
        // int samples = data.size();

        double REITERTHR = 0.1;
        E = data.getInstance(0).size();
        // int D = E - 2;
        float R = (float) Math.sqrt(E - 1);
        float EXTTRESH = R / 2.0f;
        int MINNRGENES = 2;
        int cluster = 0;

        while (NRNOCONV < maxNRNOCONV && TOOFEWPOINTS < TFPTH && BPR < BPRTH && RRES < 2) {

            // determine cluster center
            boolean clusterLocalisationConverged = wan_shr_adap(SP, EXTTRESH);
            if (clusterLocalisationConverged) {
                System.out.println("Found cluster -> EM");
                // System.out.println("EXTTRESH2 = "+EXTTRESH2);
                // optimize cluster quality
                System.out.println("Starting EM");
                boolean emConverged = exp_max(SP, ME, EXTTRESH2, S);
                if (emConverged) {
                    System.out.println("EM converged!!!");
                    // System.exit(-1);
                    NRNOCONV = 0;
                    if (Math.abs(RADNW - EXTTRESH) / EXTTRESH < REITERTHR) {
                        Vector<TaggedInstance> Q = retrieveInstances(SP, ME, RADNW);
                        if (Q.size() == 0) {
                            System.err.println("Significance level not reached");
                        }
                        if (Q.size() > MINNRGENES) {
                            cluster++;
                            outputCluster(Q, cluster);
                            removeInstances(SP, Q);
                            TOOFEWPOINTS = 0;
                            EXTTRESH = RADNW;
                        } else {
                            removeInstances(SP, Q);
                            TOOFEWPOINTS++;
                        }

                    } else {
                        EXTTRESH = RADNW;
                        BPR++;
                        if (BPR == BPRTH) {
                            System.out.println("Radius cannot be predicted!");

                        } else {
                            System.out.println("Trying new radius...");
                        }
                    }

                } else {
                    NRNOCONV++;
                    if (NRNOCONV < maxNRNOCONV) {
                        EXTTRESH = R / 2;

                        RRES++;
                        System.out.println("Resetting radius to: " + EXTTRESH);
                        if (RRES == 2) {
                            System.out.println("No convergence: Algorithm aborted - RRES exceeded!");
                            break;
                        } else {
                            BPR = 0;
                        }
                    } else {
                        System.out.println("No convergence: Algorithm aborted - NRNOCONV exceeded!");
                        // return null;
                        break;
                    }
                }
                if (TOOFEWPOINTS == TFPTH) {
                    System.out.println("No more significant clusters found: Algorithms aborted!");
                    // throw new RuntimeException("No more significant clusters
                    // found: Algorithms aborted!");
                    break;
                }
            }
        }

        Dataset[] output = new Dataset[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            output[i] = clusters.get(i);
        }
        return output;
    }

    private Vector<TaggedInstance> normalize(Dataset data) {
        Vector<TaggedInstance> out = new Vector<TaggedInstance>();
        // Instance MU=mean(data);
        // System.out.println("MU = "+MU);
        //       
        // Instance SIGM=std(data);
        // System.out.println("SIGM = "+SIGM);

        for (int i = 0; i < data.size(); i++) {
            float[] old = data.getInstance(i).toArray();
            double[] conv = new double[old.length];
            for (int j = 0; j < old.length; j++) {
                conv[j] = old[j];
            }

            Mean m = new Mean();

            double MU = m.evaluate(conv);
            // System.out.println("MU = "+MU);
            StandardDeviation std = new StandardDeviation();
            double SIGM = std.evaluate(conv, MU);
            // System.out.println("SIGM = "+SIGM);
            float[] val = new float[old.length];
            for (int j = 0; j < old.length; j++) {
                val[j] = (float) ((old[j] - MU) / SIGM);

            }
            out.add(new TaggedInstance(new SimpleInstance(val), i));
        }
        return out;
    }

    // private Instance std(Dataset data) {
    // float[]out=new float[data.getInstance(0).size()];
    // for(int i=0;i<out.length;i++){//for each attribute
    // double[]tmp=new double[data.size()];
    // for(int j=0;j<data.size();j++){
    // tmp[j]=data.getInstance(j).getValue(i);
    // }
    // StandardDeviation std=new StandardDeviation();
    // out[i]=(float)std.evaluate(tmp);
    // }
    //        
    // return new SimpleInstance(out);
    // }
    //
    // private Instance mean(Dataset data) {
    //       
    // float[]sum=new float[data.getInstance(0).size()];
    // for(int j=0;j<data.size();j++){
    // Instance tmp=data.getInstance(j);
    // for(int i=0;i<sum.length;i++){
    // sum[i]+=tmp.getValue(i);
    // }
    //           
    // }
    // for(int i=0;i<sum.length;i++){
    // sum[i]/=data.size();
    // }
    // return new SimpleInstance(sum);
    //       
    // }

    /**
     * Remove the instances in q from sp
     * 
     * @param sp
     * @param q
     */
    private void removeInstances(Vector<TaggedInstance> sp, Vector<TaggedInstance> q) {
        sp.removeAll(q);

    }

    private Vector<Dataset> clusters = new Vector<Dataset>();

    /**
     * output all the instances in q as a single cluster with the given index
     * 
     * The index is ignored.
     * 
     * @param q
     * @param cluster
     */
    private void outputCluster(Vector<TaggedInstance> q, int index) {
        Dataset tmp = new SimpleDataset();
        for (TaggedInstance i : q) {
            tmp.addInstance(data.getInstance(i.getTag()));
        }
        clusters.add(tmp);

    }

    private DistanceMeasure dm;

    private Vector<TaggedInstance> retrieveInstances(Vector<TaggedInstance> sp, float[] me2, double radnw2) {
        Instance tmp = new SimpleInstance(me2);
        Vector<TaggedInstance> out = new Vector<TaggedInstance>();
        for (TaggedInstance inst : sp) {
            if (dm.calculateDistance(inst, tmp) < radnw2)
                out.add(inst);
        }
        return out;
    }

    // modifies: RADNW
    private boolean exp_max(Vector<TaggedInstance> AS, float[] CK, double QUAL, double S) {

        float D = E - 2;
        double R = Math.sqrt(E - 1);
        // System.out.println("CK= "+Arrays.toString(CK));
        float[] RD = calculateDistances(AS, CK);
        // System.out.println("RD = "+Arrays.toString(RD));
        int samples = RD.length;
        int MAXITER = 500;
        double CDIF = 0.001;

        float count = 0;// float sum = 0;
        for (int i = 0; i < RD.length; i++) {
            if (RD[i] < QUAL) {
                count++;
                // sum += RD[i];
            }
        }
        // System.out.println("count = "+count);
        // System.out.println("RD.length = "+RD.length);
        float PC = count / RD.length;// sum / RD.length;
        float PB = 1 - PC;
        // System.out.println("PC = "+PC);
        // System.out.println("PB = "+PB);
        double tmpVAR = 0;
        // double sum=0;
        for (int i = 0; i < RD.length; i++) {
            if (RD[i] < QUAL) {
                // sum += RD[i];
                tmpVAR += RD[i] * RD[i];
            }
        }

        // System.out.println("sum = "+sum);
        // System.out.println("tmpVAR = "+tmpVAR);
        double VAR = (1 / D) * tmpVAR / count;

        boolean CONV = false;
        for (int i = 0; i < MAXITER && !CONV; i++) {
            // System.out.println("\tEM iteration: "+i);
            // System.out.println("\tVAR = "+VAR);
            float[] prc = clusterdistrib(RD, VAR, D, R);
            // System.out.println("PRC = "+Arrays.toString(prc));
            float[] prb = background(RD, D, R);
            float[] prcpc = new float[prc.length];
            for (int j = 0; j < prc.length; j++) {
                prcpc[j] = prc[j] * PC;
            }
            float[] prbpb = new float[prb.length];
            for (int j = 0; j < prb.length; j++) {
                prbpb[j] = prb[j] * PB;
            }
            float[] pr = new float[prcpc.length];
            for (int j = 0; j < prc.length; j++) {
                pr[j] = prcpc[j] + prbpb[j];
            }
            float[] pcr = new float[prcpc.length];
            for (int j = 0; j < prc.length; j++) {
                pcr[j] = prcpc[j] / pr[j];
            }
            float SM = 0;
            for (int j = 0; j < prc.length; j++) {
                SM += pcr[j];
            }
            // System.out.println("\tSM = "+SM);
            if (MathUtils.eq(SM, 0) || Float.isInfinite(SM)) {
                i = MAXITER;// will return from loop
            }
            float tmpVAR_new = 0;
            for (int j = 0; j < prc.length; j++) {
                tmpVAR_new += RD[j] * RD[j] * pcr[j];
            }
            // System.out.println("tmpVAR_new = "+tmpVAR_new);
            float VAR_new = (1 / D) * tmpVAR_new / SM;
            // System.out.println("PCR = "+Arrays.toString(pcr));
            // System.out.println("\tVAR_new = "+VAR_new);
            // System.out.println("\tPC = "+PC);

            float PC_new = SM / samples;
            // System.out.println("\tPC_new = "+PC_new);
            float PB_new = 1 - PC_new;
            if (Math.abs(VAR_new - VAR) < CDIF && Math.abs(PC_new - PC) < CDIF) {
                CONV = true;
            } else {
                // System.out.println("Iteration: "+i);
                // System.out.println("\tPRC = "+Arrays.toString(prc));
                // System.out.println("\tPCR = "+Arrays.toString(pcr));
                // System.out.println("\tSM = "+SM);
                // System.out.println("\tsamples = "+samples);
                // System.out.println("\tPC = "+PC);
                // System.out.println("\tPC_new = "+PC_new);
                // System.out.println("\tPCdif = "+Math.abs(PC_new - PC));
                // System.out.println("\tVAR = "+VAR);
                // System.out.println("\tVAR_new = "+VAR_new);
                // System.out.println("\tVARdif = "+Math.abs(VAR_new - VAR));

            }
            PC = PC_new;
            PB = PB_new;
            VAR = VAR_new;
        }

        if (CONV) {
            if (MathUtils.eq(PC, 0) || MathUtils.eq(PB, 0)) {
                System.out.println("EM: No or incorrect convergence! - PC==0 || PB==0");
                CONV = false;
                RADNW = 0;
                return false;
            }
            double SD = (2 * Math.pow(Math.PI, D / 2)) / (GammaFunction.gamma(D / 2));
            double SD1 = (2 * Math.pow(Math.PI, (D + 1) / 2)) / (GammaFunction.gamma((D + 1) / 2));
            // System.out.println("SD = "+SD);
            // System.out.println("SD1 = "+SD1);
            double CC = SD * (1 / (Math.pow(2 * Math.PI * VAR, D / 2)));
            double CB = (SD / (SD1 * Math.pow(Math.sqrt(D + 1), D)));
            double LO = (S / (1 - S)) * ((PB * CB) / (PC * CC));
            // System.out.println("PB = "+PB);
            // System.out.println("PC = "+PC);
            // System.out.println("S = "+S);
            // System.out.println("CC = "+CC);
            // System.out.println("CB = "+CB);
            // System.out.println("LO = "+LO);
            if (LO <= 0) {
                System.out.println("EM: Impossible to calculate radius - LO<0!");
                return false;
            }

            double DIS = -2 * VAR * Math.log(LO);
            // System.out.println("DIS = "+DIS);
            if (DIS <= 0) {
                System.out.println("EM: Impossible to calculate radius - DIS<0!");
                System.out.println();
                return false;
            }
            RADNW = (float) Math.sqrt(DIS);
            return true;
        } else {
            System.out.println("EM: No or incorrect convergence! Probably not enough iterations for EM");
            return false;
        }
    }

    /**
     * implements background.m
     * 
     * @param r
     * @param D
     * @param R
     * @return
     */
    private float[] background(float[] r, double D, double R) {
        double SD = (2 * Math.pow(Math.PI, D / 2)) / (GammaFunction.gamma(D / 2));
        double SD1 = (2 * Math.pow(Math.PI, (D + 1) / 2)) / (GammaFunction.gamma((D + 1) / 2));
        float[] out = new float[r.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (float) ((SD / (SD1 * (Math.pow(R, D)))) * (Math.pow(r[i], D - 1)));
        }
        return out;
    }

    /**
     * implements clusterdistrib
     * 
     * @param r
     * @param VAR
     * @param D
     * @param R
     * @return
     */
    private float[] clusterdistrib(float[] r, double VAR, double D, double R) {
        // System.out.println("\t\tCD:VAR = "+VAR);
        // System.out.println("\t\tCD:D = "+D);
        // System.out.println("\t\tCD:R = "+R);
        // System.out.println("\t\tCD:r = "+Arrays.toString(r));
        float[] out = new float[r.length];
        if (MathUtils.eq(VAR, 0)) {
            // System.out.println("\t\tCD: VAR is considered ZERO !!!");
            for (int i = 0; i < r.length; i++) {
                if (MathUtils.eq(r[i], 0)) {
                    out[i] = Float.POSITIVE_INFINITY;
                }
            }
        } else {
            double SD = (2 * Math.pow(Math.PI, D / 2)) / (GammaFunction.gamma(D / 2));
            double tmp_piVAR = 2 * Math.PI * VAR;
            double tmp_piVARpow = Math.pow(tmp_piVAR, D / 2);
            double tmp_piVARpowINV = 1 / tmp_piVARpow;

            // System.out.println("\t\tCD:SD = "+SD);
            // System.out.println("\t\tCD:tmp_piVAR = "+tmp_piVAR);
            // System.out.println("\t\tCD:tmp_piVARpow = "+tmp_piVARpow);
            // System.out.println("\t\tCD:tmp_piVARpowINV = "+tmp_piVARpowINV);

            for (int i = 0; i < r.length; i++) {
                double tmp_exp = -((r[i] * r[i]) / (2 * VAR));
                // System.out.println("\t\tMath.pow(r[i],D-1) =
                // "+Math.pow(r[i],D-1));
                // System.out.println("\t\tCD:tmp_exp = "+tmp_exp);
                // System.out.println("\t\tCD:exp(tmp_exp) =
                // "+Math.exp(tmp_exp));
                out[i] = (float) (SD * tmp_piVARpowINV * Math.pow(r[i], D - 1) * Math.exp(tmp_exp));
            }
            for (int i = 0; i < r.length; i++) {
                if (MathUtils.eq(r[i], 0))
                    out[i] = 1;

            }
        }

        return out;
    }

    /**
     * Comparable to dist_misval
     * 
     * Calculates the distance between each instance and the instance given as a
     * float array.
     * 
     * @param as
     * @param ck
     * @return
     */
    private float[] calculateDistances(Vector<TaggedInstance> as, float[] ck) {
        // voor elke instance van AS, trek er CK van af
        // return de sqrt van de som de kwadraten van de attributen van het
        // verschil
        float[] out = new float[as.size()];
        for (int i = 0; i < as.size(); i++) {
            float[] values = as.get(i).toArray();
            // float[]dif=new float[values.length];
            float sum = 0;
            for (int j = 0; j < values.length; j++) {
                // dif[j]=
                float dif = values[j] - ck[j];
                sum += dif * dif;
            }
            out[i] = (float) Math.sqrt(sum);
        }
        // Instance tmp=new SimpleInstance(ck);
        // float[]out=new float[as.size()];
        // for(int i=0;i<as.size();i++){
        // out[i]=(float)dm.calculateDistance(tmp,as.get(i));
        // }
        return out;
    }

    // Significance level
    private float S = 0.95f;

    private float EXTTRESH2;

    private float[] ME;

    // modifies: CE,ME,EXTTRESH2
    /**
     * returns true if this step converged
     */
    private boolean wan_shr_adap(Vector<TaggedInstance> A, float EXTTRESH) {
        int samples = A.size();
        float[] CE = new float[samples];
        int MAXITER = 100;
        float NRWAN = 30;
        float[] ME1 = mean(A);
        // System.out.println("A = "+A);
        // System.out.println("ME1 = "+Arrays.toString(ME1));
        // System.out.println("EXTTRESH = "+EXTTRESH);
        float[] DMI = calculateDistances(A, ME1);
        // System.out.println("DMI = "+Arrays.toString(DMI));
        float maxDMI = DMI[0];
        float minDMI = DMI[0];
        for (int i = 1; i < DMI.length; i++) {
            if (DMI[i] > maxDMI)
                maxDMI = DMI[i];
            if (DMI[i] < minDMI)
                minDMI = DMI[i];
        }
        EXTTRESH2 = maxDMI;
        float MDIS = minDMI;
        if (MathUtils.eq(MDIS, EXTTRESH2)) {
            ME = ME1;
            for (int i = 0; i < CE.length; i++)
                CE[i] = 1;
            EXTTRESH2 += 0.000001;
            System.out.println("Cluster center localisation did not reach preliminary estimate of radius!");
            return true;

        }
        float DELTARAD = (EXTTRESH2 - EXTTRESH) / NRWAN;
        float RADPR = EXTTRESH2;
        EXTTRESH2 = EXTTRESH2 - DELTARAD;
        if (EXTTRESH2 <= MDIS) {
            EXTTRESH2 = (RADPR + MDIS) / 2;
        }
        Vector<Integer> Q = findLower(DMI, EXTTRESH2);
        for (int i = 0; i < MAXITER; i++) {
            float[] ME2 = mean(select(A, Q));
            if (MathUtils.eq(ME1, ME2) && MathUtils.eq(RADPR, EXTTRESH2)) {
                ME = ME2;
                for (Integer index : Q) {
                    CE[index] = 1;
                }
                return true;
            }
            RADPR = EXTTRESH2;
            DMI = calculateDistances(A, ME2);
            if (EXTTRESH2 > EXTTRESH) {
                EXTTRESH2 = Math.max(EXTTRESH, EXTTRESH2 - DELTARAD);
                if (EXTTRESH2 < MathUtils.min(DMI)) {
                    EXTTRESH2 = RADPR;
                }
            }
            Q = findLower(DMI, EXTTRESH2);
            ME1 = ME2;

        }
        System.err.println("Preliminary cluster location did not converge");
        return false;
    }

    /**
     * return all the indices that are lower that the threshold
     * 
     * @param array
     * @param thres
     * @return
     */
    private Vector<Integer> findLower(float[] array, float threshold) {
        Vector<Integer> out = new Vector<Integer>();
        for (int i = 0; i < array.length; i++) {
            if (array[i] < threshold)
                out.add(i);
        }
        return out;

    }

    /**
     * Return a vector with all instances that have their index in the indices
     * vector.
     * 
     * @param instances
     * @param indices
     * @return
     */
    private Vector<TaggedInstance> select(Vector<TaggedInstance> instances, Vector<Integer> indices) {
        Vector<TaggedInstance> out = new Vector<TaggedInstance>();
        for (Integer index : indices) {
            out.add(instances.get(index));
        }
        return out;
    }

    private float[] mean(Vector<TaggedInstance> a) {
        float[] out = new float[a.get(0).size()];
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < a.get(0).size(); j++)
                out[j] += a.get(i).getValue(j) / a.size();
        }
        return out;

    }

}
