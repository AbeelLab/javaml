/**
 * Verbose.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.core;

import java.util.logging.Logger;
/**
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 *
 */
public class Verbose {

    private Logger log = Logger.getAnonymousLogger();

    private boolean verbose = false;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    protected void verbose(String s) {
        if (verbose)
            log.info(s);
    }
}
