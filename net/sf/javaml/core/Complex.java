/**
 * Complex.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

/**
 * Implements a mutable Complex number.
 * 
 * @author Thomas Abeel
 * 
 */
public class Complex {

    public double im = 0, re = 0;

    /**
     * Creates a new Complex number with the supplied real and complex part.
     * 
     * @param re
     *            the real part
     * @param im
     *            the complex part
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Creates a new Complex number with 0 for its real and imaginary part.
     */
    public Complex() {
        this(0, 0);
    }

    /**
     * Returns a string representation of the object
     */
    public String toString() {
        return re + " + " + im + "i";
    }

    /**
     * The absolute value of this complex number. Basically it return |this|.
     * 
     * @return the absolute value of this complex number
     */
    public double abs() {
        return Math.sqrt(re * re + im * im);
    }

    /**
     * Add a complex number to this one.
     * 
     * @param b
     *            the number to add to this one.
     */
    public void plus(Complex b) {
        re += b.re;
        im += b.re;
    }

    /**
     * Subtracts a complex number from this one.
     * 
     * @param b
     *            the number to subtract from this one.
     */
    public void minus(Complex b) {
        re -= b.re;
        im -= b.im;
    }

    /**
     * Multiply this complex number with another one.
     * 
     * @param b
     *            the number to multiply with.
     */
    public void times(Complex b) {
        double real = re * b.re - im * b.im;
        double imag = re * b.im + im * b.re;
        re = real;
        im = imag;
    }

    /**
     * Multiply with real number
     * 
     * @param alpha
     *            the real number to multiply with.
     */
    public void times(double alpha) {
        re *= alpha;
        im *= alpha;
    }

    /**
     * Take the conjugate of this complex number.
     * 
     */
    public void conjugate() {
        im = -im;
    }

    /**
     * Add two complex numbers and return the result.
     * 
     * @param a
     *            first complex number
     * @param b
     *            second complex number
     * @return the sum of a and b
     */
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    public static Complex multiply(Complex a, double d) {
        return new Complex(a.re * d, a.im * d);
    }

    public static Complex multiply(Complex a, Complex b) {
        // (x + yi)(u + vi) = (xu – yv) + (xv + yu)i.
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    public static final Complex I = new Complex(0, 1);

}
