/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools.weka;

/**
 * This exception should be thrown when something went wrong with calls to the
 * WEKA library.
 * 
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class WekaException extends RuntimeException {

    public WekaException() {
        super();
    }

    public WekaException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public WekaException(String arg0) {
        super(arg0);
    }

    public WekaException(Throwable arg0) {
        super(arg0);
    }

    private static final long serialVersionUID = 185381938656230128L;

}
