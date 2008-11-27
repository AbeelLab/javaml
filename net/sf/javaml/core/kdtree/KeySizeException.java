/**
 * %SVN.HEADER%
 * 
 * based on work by Simon Levy
 * http://www.cs.wlu.edu/~levy/software/kd/
 */
package net.sf.javaml.core.kdtree;


 /**
  * KeySizeException is thrown when a KDTree method is invoked on a
  * key whose size (array length) mismatches the one used in the that
  * KDTree's constructor.
  *
  * @author      Simon Levy
  * @version     %I%, %G%
  * @since JDK1.2 
  */

public class KeySizeException extends Exception {

    protected KeySizeException() {
	super("Key size mismatch");
    }
    
    // arbitrary; every serializable class has to have one of these
    public static final long serialVersionUID = 2L;
    
}
