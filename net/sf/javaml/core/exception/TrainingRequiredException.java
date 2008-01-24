/**
 * TrainingRequiredException.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core.exception;

public class TrainingRequiredException extends RuntimeException {

    /**
     * 
     */
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
