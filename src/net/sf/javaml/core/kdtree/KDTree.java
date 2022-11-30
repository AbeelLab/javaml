/**
 * %SVN.HEADER%
 * 
 * based on work by Simon Levy
 * http://www.cs.wlu.edu/~levy/software/kd/
 */
package net.sf.javaml.core.kdtree;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;
import java.util.NoSuchElementException;

/**
 * KDTree is a class supporting KD-tree insertion, deletion, equality search,
 * range search, and nearest neighbor(s) using double-precision floating-point
 * keys. Splitting dimension is chosen naively, by depth modulo K. Semantics are
 * as follows:
 * 
 * <UL>
 * <LI>Two different keys containing identical numbers should retrieve the same
 * value from a given KD-tree. Therefore keys are cloned when a node is
 * inserted. <BR>
 * <BR>
 * <LI>As with Hashtables, values inserted into a KD-tree are <I>not</I> cloned.
 * Modifying a value between insertion and retrieval will therefore modify the
 * value stored in the tree.
 *</UL>
 * 
 * @author Simon Levy, Bjoern Heckel
 * @version %I%, %G%
 * @since JDK1.2
 */
public class KDTree implements Serializable {

	// K = number of dimensions
	private final int m_K;

	// root of KD-tree
	private KDNode m_root;

	// count of nodes
	private int m_count;

	/**
	 * Creates a KD-tree with specified number of dimensions.
	 * 
	 * @param k
	 *            number of dimensions
	 *
	 * @throws IllegalArgumentException
	 * 			if the key size provided is less than 1
	 */
	public KDTree(int k) {

		if (k < 1) {
			throw new IllegalArgumentException("KDTree: invalid key size!");
		}

		m_K = k;
		m_root = null;
	}

	/**
	 * Insert a node in a KD-tree. Uses algorithm translated from 352.ins.c of
	 * 
	 * <PRE>
	 *   &#064;Book{GonnetBaezaYates1991,                                   
	 *     author =    {G.H. Gonnet and R. Baeza-Yates},
	 *     title =     {Handbook of Algorithms and Data Structures},
	 *     publisher = {Addison-Wesley},
	 *     year =      {1991}
	 *   }
	 * </PRE>
	 * 
	 * @param key
	 *            key for KD-tree node
	 * @param value
	 *            value at that key
	 * 
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 */
	public void insert(double[] key, Object value) {
		boolean[] isUpdate = { false };

		if (key == null || key.length != m_K) {
			throw new IllegalArgumentException("KDTree: wrong key size!");
		}

		else {
			m_root = KDNode.ins(new HPoint(key), value, m_root, 0, m_K, isUpdate);

			if (!isUpdate[0]) {
				m_count++;
			}
		}
	}

	/**
	 * Find KD-tree node whose key is identical to key. Uses algorithm
	 * translated from 352.srch.c of Gonnet & Baeza-Yates.
	 * 
	 * @param key
	 *            key for KD-tree node
	 * 
	 * @return object at key, or null if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 */
	public Object search(double[] key) {

		if (key == null || key.length != m_K) {
			throw new IllegalArgumentException("KDTree: wrong key size!");
		}

		KDNode kd = KDNode.srch(new HPoint(key), m_root, m_K);

		return (kd == null ? null : kd.v);
	}

	/**
	 * Delete a node from a KD-tree. Instead of actually deleting node and
	 * rebuilding tree, marks node as deleted. Hence, it is up to the caller to
	 * rebuild the tree as needed for efficiency.
	 * 
	 * @param key
	 *            key for KD-tree node
	 * 
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 * @throws NoSuchElementException
	 *             if no node in tree has key
	 */
	public void delete(double[] key) {

		if (key == null || key.length != m_K) {
			throw new IllegalArgumentException("KDTree: wrong key size!");
		}

		else {

			KDNode t = KDNode.srch(new HPoint(key), m_root, m_K);
			if (t == null) {
				throw new NoSuchElementException("KDTree: key missing!");
			} else {
				t.deleted = true;
			}

			m_count--;
		}
	}

	/**
	 * Find KD-tree node whose key is nearest neighbor to key. Implements the
	 * Nearest Neighbor algorithm (Table 6.4) of
	 * 
	 * <PRE>
	 * &#064;techreport{AndrewMooreNearestNeighbor,
	 *   author  = {Andrew Moore},
	 *   title   = {An introductory tutorial on kd-trees},
	 *   institution = {Robotics Institute, Carnegie Mellon University},
	 *   year    = {1991},
	 *   number  = {Technical Report No. 209, Computer Laboratory, 
	 *              University of Cambridge},
	 *   address = {Pittsburgh, PA}
	 * }
	 * </PRE>
	 * 
	 * @param key
	 *            key for KD-tree node
	 * 
	 * @return object at node nearest to key, or null on failure
	 * 
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 * @throws IndexOutOfBoundsException
	 *             if <I>n</I> is negative or exceeds tree size
	 */
	public Object nearest(double[] key) {

		Object[] nbrs = nearest(key, 1);
		return nbrs[0];
	}

	/**
	 * Find KD-tree nodes whose keys are <I>n</I> nearest neighbors to key. Uses
	 * algorithm above. Neighbors are returned in ascending order of distance to
	 * key.
	 *
	 * @param key
	 *            key for KD-tree node
	 * @param n
	 *            how many neighbors to find
	 *
	 * @return objects at node nearest to key, or null on failure
	 *
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 * @throws IndexOutOfBoundsException
	 *             if <I>n</I> is negative or exceeds tree size
	 */
	public Object[] nearest(double[] key, int n) {
		return nearest(key, n, false);
	}

	/**
	 * Find KD-tree node keys whose keys are <I>n</I> nearest neighbors to key.
	 * Uses algorithm above. Neighbors are returned in ascending order of
	 * distance to key.
	 *
	 * @param key
	 *            key for KD-tree node
	 * @param n
	 *            how many neighbors to find
	 *
	 * @return objects at node nearest to key, or null on failure
	 *
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 * @throws IndexOutOfBoundsException
	 *             if <I>n</I> is negative or exceeds tree size
	 */
	public double[][] nearestKeys(double[] key, int n) {
		Object[] results = nearest(key, n, true);

		double[][] keys = new double[results.length][];

		for (int i = 0; i < results.length; i++) {
			keys[i] = (double[]) results[i];
		}

		return keys;
	}

	/**
	 * Find KD-tree nodes whose keys are <I>n</I> nearest neighbors to key. Uses
	 * algorithm above. Neighbors are returned in ascending order of distance to
	 * key.
	 * 
	 * @param key
	 *            key for KD-tree node
	 * @param n
	 *            how many neighbors to find
	 * @param isReturnKey
	 *            true to return the nearest key rather than value
	 * 
	 * @return objects at node nearest to key, or null on failure
	 * 
	 * @throws IllegalArgumentException
	 *             if key.length mismatches K
	 * @throws IndexOutOfBoundsException
	 *             if <I>n</I> is negative or exceeds tree size
	 */
	private Object[] nearest(double[] key, int n, boolean isReturnKey) {

		if (key == null || key.length != m_K) {
			throw new IllegalArgumentException("KDTree: wrong key size!");
		}

		if (n < 0 || n > m_count) {
			throw new IndexOutOfBoundsException("Number of neighbors (" + n + ") cannot"
					+ " be negative or greater than number of nodes (" + m_count + ").");
		}

		Object[] nbrs = new Object[n];
		NearestNeighborList nnl = new NearestNeighborList(n);

		// initial call is with infinite hyper-rectangle and max distance
		HRect hr = HRect.infiniteHRect(key.length);
		double max_dist_sqd = Double.MAX_VALUE;
		HPoint keyp = new HPoint(key);

		KDNode.nnbr(m_root, keyp, hr, max_dist_sqd, 0, m_K, nnl);

		for (int i = 0; i < n; ++i) {
			KDNode kd = (KDNode) nnl.removeHighest();
			nbrs[n - i - 1] = isReturnKey ? kd.k.coord : kd.v;
		}

		return nbrs;
	}

	/**
	 * Range search in a KD-tree. Uses algorithm translated from 352.range.c of
	 * Gonnet & Baeza-Yates.
	 * 
	 * @param lowk
	 *            lower-bounds for key
	 * @param uppk
	 *            upper-bounds for key
	 * 
	 * @return array of Objects whose keys fall in range [lowk,uppk]
	 * 
	 * @throws IllegalArgumentException
	 *             on mismatch among lowk.length, uppk.length, or K
	 */
	public Object[] range(double[] lowk, double[] uppk) {

		if (lowk == null || uppk == null || lowk.length != uppk.length || lowk.length != m_K) {
			throw new IllegalArgumentException("KDTree: wrong key size!");
		}

		Vector<KDNode> v = new Vector<KDNode>();
		KDNode.rsearch(new HPoint(lowk), new HPoint(uppk), m_root, 0, m_K, v);
		Object[] o = new Object[v.size()];
		for (int i = 0; i < v.size(); ++i) {
			KDNode n = v.elementAt(i);
			o[i] = n.v;
		}
		return o;
	}

	/**
	 * Get all the data points of the KDTree.
	 *
	 * @return returns a map of all the data points
	 */
	public Map<double[], Object> toMap() {
		if (m_root == null) return null;
		return m_root.recursiveMap();
	}

	/**
	 * The size of the KDTree.
	 *
	 * @return returns the number of key-value mappings in this KDTree.
	 */
	public int size() {
		return m_count;
	}

	/**
	 * The number of dimensions of the KDTree.
	 *
	 * @return returns the number of dimensions of this KDTree.
	 */
	public int dimensions() {
		return m_K;
	}

	public String toString() {
		return m_root != null ? m_root.toString(0) : "null";
	}
}
