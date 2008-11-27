/**
 * %SVN.HEADER%
 * 
 * based on work by Simon Levy
 * http://www.cs.wlu.edu/~levy/software/kd/
 */
package net.sf.javaml.core.kdtree;


class KeyMissingException extends Exception {

    public KeyMissingException() {
	super("Key not found");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 3L;
    
}
