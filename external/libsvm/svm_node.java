package external.libsvm;
public class svm_node implements java.io.Serializable
{
	public int index;
	public double value;
	
	public String toString(){
	    return "["+index+","+value+"]";
	}
}
