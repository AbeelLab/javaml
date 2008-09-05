/**
 * %SVN.HEADER%
 */
package external.libsvm;
public class svm_problem implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8499689386922121368L;
	public int l;
	public double[] y;
	public svm_node[][] x;
}
