/**
 * %SVN.HEADER%
 */
/*
 Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
 is hereby granted without fee, provided that the above copyright notice appear in all copies and 
 that both that copyright notice and this permission notice appear in supporting documentation. 
 CERN makes no representations about the suitability of this software for any purpose. 
 It is provided "as is" without expressed or implied warranty.
 */
/**
 * Gamma and Beta functions.
 * <p>
 * <b>Implementation:</b>
 * <dt>
 * Some code taken and adapted from the <A HREF="http://www.sci.usq.edu.au/staff/leighb/graph/Top.html">Java 2D Graph Package 2.4</A>,
 * which in turn is a port from the <A HREF="http://people.ne.mediaone.net/moshier/index.html#Cephes">Cephes 2.2</A> Math Library (C).
 * Most Cephes code (missing from the 2D Graph Package) directly ported.
 *
 * @author wolfgang.hoschek@cern.ch
 * @author Thomas Abeel
 * 
 */
package net.sf.javaml.utils;


public class GammaFunction {
	private static final double SQTPI = 2.50662827463100050242E0;

	/**
	 * Returns the Gamma function computed by Stirling's formula; formerly named
	 * <tt>stirf</tt>. The polynomial STIR is valid for 33 <= x <= 172.
	 */
	public static double stirlingFormula(double x) throws ArithmeticException {
		double STIR[] = { 7.87311395793093628397E-4,
				-2.29549961613378126380E-4, -2.68132617805781232825E-3,
				3.47222221605458667310E-3, 8.33333333333482257126E-2, };
		double MAXSTIR = 143.01608;
		double w = 1.0 / x;
		double y = Math.exp(x);

		w = 1.0 + w * Polynomial.polevl(w, STIR, 4);

		if (x > MAXSTIR) {
			/* Avoid overflow in Math.pow() */
			double v = Math.pow(x, 0.5 * x - 0.25);
			y = v * (v / y);
		} else {
			y = Math.pow(x, x - 0.5) / y;
		}
		y = SQTPI * y * w;
		return y;
	}

	/**
	 * Returns the Gamma function of the argument.
	 */
	public static double gamma(double x) throws ArithmeticException {

		double P[] = { 1.60119522476751861407E-4, 1.19135147006586384913E-3,
				1.04213797561761569935E-2, 4.76367800457137231464E-2,
				2.07448227648435975150E-1, 4.94214826801497100753E-1,
				9.99999999999999996796E-1 };
		double Q[] = { -2.31581873324120129819E-5, 5.39605580493303397842E-4,
				-4.45641913851797240494E-3, 1.18139785222060435552E-2,
				3.58236398605498653373E-2, -2.34591795718243348568E-1,
				7.14304917030273074085E-2, 1.00000000000000000320E0 };
		// double MAXGAM = 171.624376956302725;
		// double LOGPI = 1.14472988584940017414;

		double p, z;
		//int i;

		double q = Math.abs(x);

		if (q > 33.0) {
			if (x < 0.0) {
				p = Math.floor(q);
				if (p == q)
					throw new ArithmeticException("gamma: overflow");
				//i = (int) p;
				z = q - p;
				if (z > 0.5) {
					p += 1.0;
					z = q - p;
				}
				z = q * Math.sin(Math.PI * z);
				if (z == 0.0)
					throw new ArithmeticException("gamma: overflow");
				z = Math.abs(z);
				z = Math.PI / (z * stirlingFormula(q));

				return -z;
			} else {
				return stirlingFormula(x);
			}
		}

		z = 1.0;
		while (x >= 3.0) {
			x -= 1.0;
			z *= x;
		}

		while (x < 0.0) {
			if (x == 0.0) {
				throw new ArithmeticException("gamma: singular");
			} else if (x > -1.E-9) {
				return (z / ((1.0 + 0.5772156649015329 * x) * x));
			}
			z /= x;
			x += 1.0;
		}

		while (x < 2.0) {
			if (x == 0.0) {
				throw new ArithmeticException("gamma: singular");
			} else if (x < 1.e-9) {
				return (z / ((1.0 + 0.5772156649015329 * x) * x));
			}
			z /= x;
			x += 1.0;
		}

		if ((x == 2.0) || (x == 3.0))
			return z;

		x -= 2.0;
		p = Polynomial.polevl(x, P, 6);
		q = Polynomial.polevl(x, Q, 7);
		return z * p / q;
	}
	
	/**
	 * Returns the natural logarithm of the gamma function; formerly named <tt>lgamma</tt>.
	 */
	public static double logGamma(double x) throws ArithmeticException {
		double p, q, w, z;
		double LOGPI = 1.14472988584940017414;

			 double A[] = {
						   8.11614167470508450300E-4,
						   -5.95061904284301438324E-4,
							7.93650340457716943945E-4,
						   -2.77777777730099687205E-3,
							8.33333333333331927722E-2
						   };
			 double B[] = {
						   -1.37825152569120859100E3,
						   -3.88016315134637840924E4,
						   -3.31612992738871184744E5,
						   -1.16237097492762307383E6,
						   -1.72173700820839662146E6,
						   -8.53555664245765465627E5
						   };
			 double C[] = {
						   /* 1.00000000000000000000E0, */
						   -3.51815701436523470549E2,
						   -1.70642106651881159223E4,
						   -2.20528590553854454839E5,
						   -1.13933444367982507207E6,
						   -2.53252307177582951285E6,
						   -2.01889141433532773231E6
						  };

			 if( x < -34.0 ) {
	  	   q = -x;
		   w = logGamma(q);
		   p = Math.floor(q);
		   if( p == q ) throw new ArithmeticException("lgam: Overflow");
		   z = q - p;
		   if( z > 0.5 ) {
			p += 1.0;
			z = p - q;
	 	   }
		   z = q * Math.sin( Math.PI * z );
		   if( z == 0.0 ) throw new 
								   ArithmeticException("lgamma: Overflow");
		   z = LOGPI - Math.log( z ) - w;
		   return z;
		 }

			 if( x < 13.0 ) {
	  	   z = 1.0;
		   while( x >= 3.0 ) {
			x -= 1.0;
			z *= x;
		   }
		   while( x < 2.0 ) {
			if( x == 0.0 ) throw new 
									ArithmeticException("lgamma: Overflow");
			z /= x;
			x += 1.0;
		   }
		   if( z < 0.0 ) z = -z;
		   if( x == 2.0 ) return Math.log(z);
		   x -= 2.0;
		   p = x * Polynomial.polevl( x, B, 5 ) / Polynomial.p1evl( x, C, 6);
	 	   return( Math.log(z) + p );
		 }

			 if( x > 2.556348e305 ) throw new 
							  ArithmeticException("lgamma: Overflow");

			 q = ( x - 0.5 ) * Math.log(x) - x + 0.91893853320467274178;
			 //if( x > 1.0e8 ) return( q );
			 if( x > 1.0e8 ) return( q );

			 p = 1.0/(x*x);
			 if( x >= 1000.0 )
		     q += ((   7.9365079365079365079365e-4 * p
			      - 2.7777777777777777777778e-3) *p
			     + 0.0833333333333333333333) / x;
			 else
		     q += Polynomial.polevl( p, A, 4 ) / x;
			 return q;
		 }
}
