/**
 * DerGauss.java
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

public class DerGaussWavelet extends AbstractRealWaveletTransform {
   
  
    public DerGaussWavelet(int nvoice, int oct, int scale) {
        super(nvoice, oct, scale);
        // TODO Auto-generated constructor stub
    }

    Complex[] transform(double[] omega, double omega0) {
        // window = i.*omega.*exp(-omega.^2 ./2);
        
        Complex[]window=new Complex[omega.length];
        for(int i=0;i<omega.length;i++){
            window[i]=Complex.multiply(Complex.I,omega[i]*Math.exp(-(omega[i]*omega[i])/2));
        }
        return window;
    }

}
