/**
 * WaveletTest.java
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
package junit.filter;

import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;
import net.sf.javaml.filter.wavelet.experimental.HiLoHaarWavelet;
import net.sf.javaml.filter.wavelet.experimental.LiftingHaarWavelet;
import net.sf.javaml.filter.wavelet.experimental.LiftingHaarWavelet2;

import org.junit.Test;

public class WaveletTest {

    @Test
    public void testHiLo() {
        HiLoHaarWavelet haar = new HiLoHaarWavelet();
        double[] tmp = { 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 7, 5, 3 };
        Instance i = new SimpleInstance(tmp);
        System.out.println(haar.filterInstance(i));

        LiftingHaarWavelet haar2=new LiftingHaarWavelet();
        System.out.println(haar2.filterInstance(i));
        
        LiftingHaarWavelet2 haar3=new LiftingHaarWavelet2();
        System.out.println(haar3.filterInstance(i));
    }
}
