/**
 * %SVN.HEADER%
 */
package net.sf.javaml.core.exception;

/**
 * Indicates that the algorithm that throws this exception should have been
 * trained prior to point the exception was thrown. Typically this implies that
 * the build method of a particular algorithm has not yet been called.
 * 
 * @author Thomas Abeel
 * 
 */
public class TrainingRequiredException extends RuntimeException {

    private static final long serialVersionUID = 1774207131386358008L;

    public TrainingRequiredException() {
    }

    public TrainingRequiredException(String arg0) {
        super(arg0);
    }

    public TrainingRequiredException(Throwable arg0) {
        super(arg0);
    }

    public TrainingRequiredException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
