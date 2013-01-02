/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements additional operations for lists
 * 
 * @author Thomas Abeel
 * 
 */
public class ListTools {

	/**
	 * Create a list of the specified size filled with integers from 0 to size-1
	 * 
	 * @param size
	 *            size of the returned list
	 * @return list of integers with each value equal to position in the list
	 */
	public static List<Integer> incfill(int size) {
		List<Integer>out=new ArrayList<Integer>(size);
		for(int i=0;i<size;i++)
			out.add(i);
		return out;
	}
}
