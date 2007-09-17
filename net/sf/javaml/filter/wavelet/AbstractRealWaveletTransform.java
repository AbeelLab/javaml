/**
 * AbstractRealWaveletTransform.java
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
package net.sf.javaml.filter.wavelet;

import net.sf.javaml.core.Complex;
import net.sf.javaml.core.ComplexInstance;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.FastFourierTransform;
import net.sf.javaml.filter.Filter;
import net.sf.javaml.utils.MathUtils;

public abstract class AbstractRealWaveletTransform implements Filter {

    private int nvoice, oct, scale;

    public AbstractRealWaveletTransform(int nvoice, int oct, int scale) {
        super();
        this.nvoice = nvoice;
        this.oct = oct;
        this.scale = scale;
    }

    public Dataset filterDataset(Dataset data) {
        Dataset out = new SimpleDataset();
        for (Instance i : data)
            out.addInstance(filterInstance(i));
        return out;
    }

    public Instance filterInstance(Instance instance) {
        return transform(instance);
    }

    public Instance unfilterInstance(Instance instance) {
        throw new UnsupportedOperationException("Not implemented");
        // XXX to implement
    }

    private Instance transform(Instance x) {
        int n = x.size();
        FastFourierTransform fft = new FastFourierTransform();
        Instance xhat = fft.filterInstance(x);
        double[] xi = new double[n];
        double factor = 2 * Math.PI / n;
        for (int i = 0; i < n; i++) {
            if (i <= n / 2)
                xi[i] = i;
            else
                xi[i] = (i - n);
            xi[i] *= factor;
        }
        double omega0 = 5;
        int noctave = (int) (Math.floor(MathUtils.log2(n)) - oct);
        int nscale = nvoice * noctave;
        double[][] rwt = new double[n][nscale];
        int kscale = 1;
        for (int jo = 1; jo <= noctave; jo++) {
            for (int jv = 1; jv <= nvoice; jv++) {
                double qscale = scale * Math.pow(2, jv / nvoice);
                double[] omega = new double[xi.length];
                for (int i = 0; i < xi.length; i++) {
                    omega[i] = n * xi[i] / qscale;
                }
                Complex[] window = transform(omega, omega0);
                for (int i = 0; i < window.length; i++) {
                    window[i].re /= Math.sqrt(qscale);
                    window[i].im /= Math.sqrt(qscale);
                }
                Complex[] what = new Complex[window.length];
                for (int i = 0; i < what.length; i++)
                    what[i] = Complex.multiply(window[i], xhat.getComplex(i));
                Instance t = new ComplexInstance(what);
                Instance w = fft.unfilterInstance(t);
                for (int i = 0; i < w.size(); i++) {
                    rwt[i][kscale - 1] = w.getValue(i);
                }
                kscale++;
            }
            scale *= 2;
        }
        double[] tmp = new double[n * nscale];
        // TODO optimize using system.arrcopy(requires switching of the
        // dimensions)
        // numbers are scales
        // first all dimension of the first scale, next the second scale and so
        // on.
        // [1 1 1 1 2 2 2 2 3 3 3 3 4 4 4 4]
        for (int i = 0; i < n; i++) {// number of dimensions
            for (int j = 0; j < nscale; j++) {// number of scales
                tmp[j * n + i] = rwt[i][j];
            }

        }
        return new SimpleInstance(tmp);
    }

    abstract Complex[] transform(double[] omega, double omega0);

}
