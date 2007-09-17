/**
 * FastFourierTransform.java
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
package net.sf.javaml.filter;

import net.sf.javaml.core.Complex;
import net.sf.javaml.core.ComplexInstance;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;

public class FastFourierTransform implements Filter {

    

    public Instance filterInstance(Instance instance) {
       return doFourierTransform(instance, -1);
    }

    public Instance unfilterInstance(Instance instance) {
        return doFourierTransform(instance, 1);
    }
    public Dataset filterDataset(Dataset data) {
        Dataset out=new SimpleDataset();
        for(Instance i:data){
            out.addInstance(filterInstance(i));
        }
        return out;
    }
    private ComplexInstance doFourierTransform(final Instance input, int direction) {
        Complex[] output = new Complex[input.size()];
        for (int i = 0; i < output.length; i++)
            output[i] = new Complex();

        // Ensure input length is a power of two
        int length = input.size();

        if ((length < 1) | ((length & (length - 1)) != 0))
            throw new RuntimeException("Length of input (" + length + ") is not a power of 2.");

        if ((direction != 1) && (direction != -1))
            throw new RuntimeException("Bad direction specified.  Should be 1 or -1.");

        // Determine max number of bits
        int maxbits, n = length;

        for (maxbits = 0; maxbits < 16; maxbits++) {
            if (n == 0)
                break;
            n /= 2;
        }

        maxbits -= 1;

        // Binary reversion & interlace result real/imaginary
        int i, t, bit;

        for (i = 0; i < length; i++) {
            t = 0;
            n = i;

            for (bit = 0; bit < maxbits; bit++) {
                t = (t * 2) | (n & 1);
                n /= 2;
            }
            output[t].re = input.getComplex(i).re;
            output[t].im = input.getComplex(i).im;

        }

        // put it all back together (Danielson-Lanczos butterfly)
        int mmax = 2, istep, j, m; // counters
        double theta, wtemp, wpr, wr, wpi, wi, tempr, tempi; // trigonometric
        // recurrences

        n = length * 2;

        while (mmax < n) {
            istep = mmax * 2;
            theta = (direction * 2 * Math.PI) / mmax;
            wtemp = Math.sin(0.5 * theta);
            wpr = -2.0 * wtemp * wtemp;
            wpi = Math.sin(theta);
            wr = 1.0;
            wi = 0.0;

            for (m = 0; m < mmax; m += 2) {
                for (i = m; i < n; i += istep) {
                    j = i + mmax;
                    tempr = wr * output[j / 2].re - wi * output[j / 2].im;
                    tempi = wr * output[j / 2].im + wi * output[j / 2].re;

                    output[j / 2].re = output[i / 2].re - tempr;
                    output[j / 2].im = output[i / 2].im - tempi;

                    output[i / 2].re += tempr;
                    output[i / 2].im += tempi;
                }

                wr = (wtemp = wr) * wpr - wi * wpi + wr;
                wi = wi * wpr + wtemp * wpi + wi;
            }

            mmax = istep;
        }
        if(direction==1){//rescaling in case of inverse transform
            // if isInverse && (size(y,1) > 1)
            // r = eml_const(ones(class(y)) / size(y,1));
            // y = y * r;
            //        end
            for(i=0;i<output.length;i++){
                output[i].re/=output.length;
                output[i].im/=output.length;
            }
        }
        return new ComplexInstance(output);
    }

}
