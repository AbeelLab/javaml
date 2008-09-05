/**
 * %SVN.HEADER%
 */
package external.libsvm;
public class svm_node implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -931407775287087324L;
	public int index;
	public double value;
	
	public String toString(){
	    return "["+index+","+value+"]";
	}
}
