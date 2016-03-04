/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.classification.bayes;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 */
 class Functions {

	// doorsnede 2 vectors
	 Vector<Integer> cutVectors(Vector<Integer> v1, Vector<Integer> v2) {
		Vector<Integer> result = new Vector();
		if (v1 == null || v2 == null) {
			return result;
		}
		// smallest size
		if (v1.size() <= v2.size()) {
			ListIterator itr = v1.listIterator();
			while (itr.hasNext()) {
				Object test = itr.next();
				if (v2.contains(test)) {
					result.add((Integer) test);
					// System.out.println("duplicate element: "+retrieveInstanceIDList);
				}

			}
		} else {
			ListIterator itr = v2.listIterator();
			while (itr.hasNext()) {
				Object test = itr.next();
				if (v1.contains(test)) {
					result.add((Integer) test);
					// System.out.println("duplicate element: "+retrieveInstanceIDList);
				}

			}
		}

		return result;
	}

	// doorsnede 2 vectors
	 Vector<Integer> cutVectorsSort2(Vector<Integer> v1,
			Vector<Integer> v2) {
		Vector<Integer> result = new Vector();
		Vector<Integer> temp = new Vector();
		if (v1 == null || v2 == null) {
			return result;
		}
		// smallest size
		ListIterator itr;
		if (v1.size() <= v2.size()) {
			itr = v1.listIterator();
			temp = v2;

		} else {
			itr = v2.listIterator();
			temp = v1;
		}

		int index = 0;
		while (itr.hasNext()) {
			Object test = itr.next();
			int index2 = binarySearch(temp, index, (temp.size() - 1),
					(Integer) test);
			if (index2 != -1) {
				result.add((Integer) test);
				if ((index2 + 1) < temp.size()) {
					index2 += 1;
				}
				index = index2;
				// System.out.println("duplicate element: "+retrieveInstanceIDList);
			}

		}

		return result;
	}

	// =========================================================== binarySearch
	/**
	 * Binary search of sorted array. Negative value on search failure. The
	 * upperbound index is not included in the search. This is to be consistent
	 * with the way Java in general expresses ranges. The performance is O(log
	 * N).
	 * 
	 * @param sorted
	 *            Array of sorted values to be searched.
	 * @param first
	 *            Index of first element to serach, sorted[first].
	 * @param upto
	 *            Index of last element to search, sorted[upto-1].
	 * @param key
	 *            Value that is being looked for.
	 * @return Returns index of the first match, or or -insertion_position -1 if
	 *         key is not in the array. This value can easily be transformed
	 *         into the position to insert it.
	 */
	 static int binarySearch(Vector<Integer> sorted, int first, int upto,
			int key) {

		int comparisonCount = 0;
		while (first < upto) {
			int mid = (first + upto) / 2; // Compute mid point.
			if (key < sorted.get(mid)) {
				upto = mid; // repeat search in bottom half.
				comparisonCount++;
			} else if (key > sorted.get(mid)) {
				first = mid + 1; // Repeat search in top half.
				comparisonCount += 2;
			} else {
				comparisonCount += 2;
				return mid; // Found it. return position
			}
		}
		return -(first + 1); // Failed to find key
	}

	// doorsnede 2 vectors
	 Vector<Integer> cutVectorsSort(Vector<Integer> v1, Vector<Integer> v2) {
		Vector<Integer> result = new Vector();
		if (v1 == null || v2 == null) {
			return result;
		}
		// smallest size
		int v1index = 0;
		int v2index = 0;

		while ((v1index < v1.size()) && (v2index < v2.size())) {
			if (((int) v1.get(v1index)) == ((int) v2.get(v2index))) {
				result.add(v1.get(v1index));
				v1index++;
				v2index++;

			} else if (((int) v1.get(v1index)) < ((int) v2.get(v2index))) {
				v1index++;
			} else {
				v2index++;
			}
		}

		// System.out.println("\n--------------------");
		// System.out.println("v1:"+v1);
		// System.out.println("v2:"+v2);
		// System.out.println("doorsnede:"+result);

		return result;
	}

	// for string
	 int cutLLSortString(LinkedList v1, LinkedList v2) {

		if (v1 == null || v2 == null || v1.size() == 0 || v2.size() == 0) {
			return 0;
		}

		int cnt = 0;
		Iterator it1 = v1.iterator();
		Iterator it2 = v2.iterator();
		String L1el = (String) it1.next();
		String L2el = (String) it2.next();

		while ((it1.hasNext()) && (it2.hasNext())) {
			int compare = L1el.compareTo(L2el);
			if (compare < 0) {
				L1el = (String) it1.next();
			} else if (compare > 0) {
				L2el = (String) it2.next();
			} else {
				cnt++;
				L1el = (String) it1.next();
				L2el = (String) it2.next();
			}
		}

		// System.out.println("\n--------------------");
		// System.out.println("v1:"+v1);
		// System.out.println("v2:"+v2);
		// System.out.println("doorsnede:"+result);

		return cnt;
	}

	// for integer
	 int cutLLSortInt(LinkedList v1, LinkedList v2) {

		if (v1 == null || v2 == null || v1.size() == 0 || v2.size() == 0) {
			return 0;
		}

		int cnt = 0;
		Iterator it1 = v1.iterator();
		Iterator it2 = v2.iterator();
		int L1el = (Integer) it1.next();
		int L2el = (Integer) it2.next();

		while ((it1.hasNext()) && (it2.hasNext())) {
			if ((L1el) == (L2el)) {
				cnt++;
				L1el = (Integer) it1.next();
				L2el = (Integer) it2.next();

			} else if ((L1el) < (L2el)) {
				L1el = (Integer) it1.next();

			} else {
				L2el = (Integer) it2.next();
			}
		}

		// System.out.println("\n--------------------");
		// System.out.println("v1:"+v1);
		// System.out.println("v2:"+v2);
		// System.out.println("doorsnede:"+result);

		return cnt;
	}

	 void in(String f) {

		HashMap<Integer, Double> featureJGivenC = new HashMap<Integer, Double>();
		File file = new File(f);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			int cnt = 0;
			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {
				featureJGivenC.put(++cnt, (double) cnt);

				// this statement reads the line from the file and print it to
				// the console.
				// System.out.println(dis.readLine());
				dis.readLine();
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	 static BufferedWriter outNew() {

		try {
			// Create file
			FileWriter fstream = new FileWriter("out.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			return out;
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			return null;
		}

	}

	 static void outAdd(String s, BufferedWriter out) {

		try {

			out.append(s);
			// Close the output stream

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	 static void outClose(BufferedWriter out) {

		try {

			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	 Vector<Integer> insertionSort(Vector<Integer> data) {
		for (int i = 0; i < data.size(); i++) {
			for (int j = i; j > 0; j--) {
				if (data.get(j - 1) > data.get(j)) {
					int swap = data.get(j);
					data.set(j, data.get(j - 1));
					data.set(j - 1, swap);

				}
			}
		}
		return data;
	}

	 double log2(double x) {
		return (Math.log(x) / Math.log(2.0));

	}

	 LinkedHashMap sortHashMapByValues(HashMap passedMap,
			boolean ascending) {
		// still problem with duplicates ? 2 check
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		if (!ascending)
			Collections.reverse(mapValues);

		LinkedHashMap someMap = new LinkedHashMap();
		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				if (passedMap.get(key).toString().equals(val.toString())) {
					passedMap.remove(key);
					mapKeys.remove(key);
					someMap.put(key, val);
					break;
				}
			}
		}
		return someMap;
	}

	 void testing_phase() {
		dotline();
		pnl("TESTING PHASE STARTED");
		dotline();
	}

	 void pnl(String s) {
		System.out.println(s);
	}

	 void dotline() {
		pnl("---------------------");
	}
}
