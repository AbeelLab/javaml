/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.kdtree.KDTree;
import net.sf.javaml.core.kdtree.TypedKDTree;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

public class TestTypedKDTree {
	// Implementation for 1D points (not provided by TypedKDTree)
	static class Point1D implements TypedKDTree.Point {
		private final double[] point;
		public Point1D(double x) { point = new double[]{ x }; }
		public Point1D(double x, boolean invalidPoint) {
			point = invalidPoint ? new double[]{ x, x } : new double[]{ x };
		}
		@Override
		public double[] getKDTreePoint() { return point; }
	}
	static class Point2D implements TypedKDTree.Point {
		private final double[] point;
		public Point2D(double x, double y) {
			point = new double[]{ x, y };
		}
		@Override
		public double[] getKDTreePoint() { return point; }
	}
	static class Point3D implements TypedKDTree.Point {
		private final double[] point;
		public Point3D(double x, double y, double z) {
			point = new double[]{ x, y, z };
		}
		@Override
		public double[] getKDTreePoint() { return point; }
	}

	// Point3D and Point2D are provided for convenience by TypedKDTree
	static final Point3D TEST_POINT_3D_1 = new Point3D(1, 2, 3);
	static final Point3D TEST_POINT_3D_2 = new Point3D(-1, -2, -3);
	static final Point3D TEST_POINT_3D_3 = new Point3D(15, 10, -3);
	static final Point3D TEST_POINT_3D_4 = new Point3D(10, 0, 0);
	static final Point3D TEST_POINT_3D_MISSING = new Point3D(0, -2, -3);

	static final Point3D TEST_RANGE_3D_1 = new Point3D(0, 0, 0);
	static final Point3D TEST_RANGE_3D_2 = new Point3D(20, 20, 20);

	static final Point2D TEST_POINT_2D_1 = new Point2D(1, 2);
	static final Point2D TEST_POINT_2D_2 = new Point2D(-1, -2);

	static final Point1D TEST_POINT_1D_1 = new Point1D(2.2);
	static final Point1D TEST_POINT_1D_2 = new Point1D(-2.2);
	static final Point1D TEST_POINT_1D_3 = new Point1D(-3.3);
	static final Point1D TEST_POINT_1D_4 = new Point1D(5.5);
	static final Point1D TEST_POINT_1D_MISSING = new Point1D(-1.0); // Nearest to TEST_POINT_1D_2
	static final Point1D TEST_POINT_1D_INVALID = new Point1D(-1.0, true);

	static final String TEST_OBJECT_1 = "TEST 1";
	static final String TEST_OBJECT_2 = "TEST 2";
	static final String TEST_OBJECT_3 = "TEST 3";


	/*****************
	 * TREE CREATION *
	 *****************/

	@Test
	public void testKDTree_creationSuccess() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		Assert.assertNotNull(treeA);

		TypedKDTree<Point3D, String> treeB = new TypedKDTree<Point3D, String>();
		treeB.initialize(5);
		Assert.assertNotNull(treeB);
		Assert.assertEquals(treeB.dimensions(), 5);
	}

	@Test
	public void testKDTree_canInitialize() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		treeA.initialize(3);
		Assert.assertEquals(treeA.dimensions(), 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_creationFailureZero() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		treeA.initialize(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testKDTree_creationFailureRepeated() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		treeA.initialize(3);
		treeA.initialize(4);
	}


	/******************
	 * TREE INSERTION *
	 ******************/

	@Test
	public void testKDTree_canAdd3DData() {
		// Can add an item
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		Assert.assertEquals(treeA.size(), 1);

		// Add same point again, should be no change in count
		treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		Assert.assertEquals(treeA.size(), 1);

		// Add more points
		treeA.insert(TEST_POINT_3D_2, TEST_OBJECT_2);
		treeA.insert(TEST_POINT_3D_3, TEST_OBJECT_3);
		treeA.insert(TEST_POINT_3D_4, TEST_OBJECT_3); // With repeat value
		Assert.assertEquals(treeA.size(), 4);
	}

	@Test
	public void testKDTree_canAdd2DData() {
		// Can add an item
		TypedKDTree<Point2D, String> treeA = new TypedKDTree<Point2D, String>();
		treeA.insert(TEST_POINT_2D_1, TEST_OBJECT_1);
		treeA.insert(TEST_POINT_2D_2, TEST_OBJECT_2);
		Assert.assertEquals(treeA.size(), 2);

		// Add same point again, should be no change in count
		treeA.insert(TEST_POINT_2D_1, TEST_OBJECT_3);
		Assert.assertEquals(treeA.size(), 2);
	}

	@Test
	public void testKDTree_canAdd1DData() {
		// Can add an item
		TypedKDTree<Point1D, String> treeA = new TypedKDTree<Point1D, String>();

		treeA.insert(TEST_POINT_1D_1, TEST_OBJECT_1);
		treeA.insert(TEST_POINT_1D_2, TEST_OBJECT_2);
		Assert.assertEquals(treeA.size(), 2);

		// Add same point again, should be no change in count
		treeA.insert(TEST_POINT_1D_1, TEST_OBJECT_3);
		Assert.assertEquals(treeA.size(), 2);
	}

	@Test
	public void testKDTree_canAddNullData() {
		// Can add an item
		TypedKDTree<Point1D, String> treeA = new TypedKDTree<Point1D, String>();
		treeA.insert(TEST_POINT_1D_1, null);
		treeA.insert(TEST_POINT_1D_2, null);
		Assert.assertEquals(treeA.size(), 2);

		// Add same point again, should be no change in count
		treeA.insert(TEST_POINT_1D_1, null);
		Assert.assertEquals(treeA.size(), 2);
	}

	@Test
	public void testKDTree_canAddMultipleData() {
		// Can add an items
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		HashMap<Point3D, String> map = new HashMap<Point3D, String>();
		map.put(TEST_POINT_3D_1, TEST_OBJECT_1);
		map.put(TEST_POINT_3D_2, TEST_OBJECT_2);
		map.put(TEST_POINT_3D_3, TEST_OBJECT_3);
		map.put(TEST_POINT_3D_4, TEST_OBJECT_3);

		treeA.insert(map);
		Assert.assertEquals(treeA.size(), 4);
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotAddMixedKeys() {
		// Attempt to add 2D to a 3D tree
		TypedKDTree<Point1D, String> treeA = new TypedKDTree<Point1D, String>();
		treeA.insert(TEST_POINT_1D_1, TEST_OBJECT_1);
		treeA.insert(TEST_POINT_1D_INVALID, TEST_OBJECT_2);
	}

	@Test
	public void testKDTree_canAddNullKey() {
		// Attempt to add null to a 3D tree
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		treeA.insert(null, TEST_OBJECT_1);
		Assert.assertEquals(treeA.size(), 0);
	}


	/******************
	 * TREE SEARCHING *
	 ******************/

	@Test
	public void testKDTree_canSearchByPresentKey3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Get by valid key
		Object result = treeA.search(TEST_POINT_3D_2);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, TEST_OBJECT_2);
	}

	@Test
	public void testKDTree_canSearchByMissingKey3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Get by missing valid key
		String result = treeA.search(TEST_POINT_3D_MISSING);
		Assert.assertNull(result);
	}

	@Test
	public void testKDTree_canSearchByPresentKey1D() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Get by valid key
		Object result = treeA.search(TEST_POINT_1D_3);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, TEST_OBJECT_3);
	}

	@Test
	public void testKDTree_canSearchNullKey() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		String result = treeA.search(null);
		Assert.assertNull(result);
	}


	/*****************
	 * TREE DELETING *
	 *****************/

	@Test
	public void testKDTree_canDeletePresentKey3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Delete data, check it's gone
		treeA.delete(TEST_POINT_3D_2);
		String result = treeA.search(TEST_POINT_3D_2);
		Assert.assertNull(result);
	}

	@Test
	public void testKDTree_canDeletePresentKey1D() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Delete data, check it's gone
		treeA.delete(TEST_POINT_1D_2);
		String result = treeA.search(TEST_POINT_1D_2);
		Assert.assertNull(result);
	}

	@Test
	public void testKDTree_canDeleteMultipleKeys3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		ArrayList<Point3D> keys = new ArrayList<Point3D>();
		keys.add(TEST_POINT_3D_1);
		keys.add(TEST_POINT_3D_2);

		// Delete data, check it's gone
		treeA.delete(keys);
		String result1 = treeA.search(TEST_POINT_3D_1);
		String result2 = treeA.search(TEST_POINT_3D_2);
		Assert.assertNull(result1);
		Assert.assertNull(result2);
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotDeleteInvalidKey() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Search by invalid key (wrong size)
		treeA.delete(TEST_POINT_1D_INVALID);
	}

	@Test
	public void testKDTree_canDeleteNullKey() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertEquals(treeA.size(), 4);

		// Search by invalid key (null)
		treeA.delete((Point3D) null);

		Assert.assertEquals(treeA.size(), 4);
	}

	@Test
	public void testKDTree_canDeleteByMissingKey() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertEquals(treeA.size(), 4);

		// Delete by missing key
		treeA.delete(TEST_POINT_3D_MISSING);

		Assert.assertEquals(treeA.size(), 4);
	}


	/*****************************
	 * TREE NEAREST - SINGLE ARG *
	 *****************************/

	@Test
	public void testKDTree_canGetNearestWithPresentKey3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		String result = treeA.nearest(TEST_POINT_3D_MISSING);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, TEST_OBJECT_2); // from TEST_POINT_3D_2
	}

	@Test
	public void testKDTree_canGetNearestWithPresentKey1D() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		Object result = treeA.nearest(TEST_POINT_1D_MISSING);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, TEST_OBJECT_2); // from TEST_POINT_1D_2
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotGetNearestWithInvalidKey() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Search by invalid key (wrong size)
		treeA.nearest(TEST_POINT_1D_INVALID);
	}

	@Test
	public void testKDTree_canGetNearestWithNullKey() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		String result = treeA.nearest(null);
		Assert.assertNull(result);
	}

	@Test
	public void testKDTree_canGetNearestWithLackOfData() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		String result = treeA.nearest(TEST_POINT_3D_1);
		Assert.assertNull(result);
	}


	/*******************************
	 * TREE NEAREST - MULTIPLE ARG *
	 *******************************/

	@Test
	public void testKDTree_canGetNearest2WithPresentKey3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		List<String> result = treeA.nearest(TEST_POINT_3D_MISSING, 2);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.get(0));
		Assert.assertNotNull(result.get(1));
		Assert.assertEquals(result.get(0), TEST_OBJECT_2); // from TEST_POINT_3D_2
		Assert.assertEquals(result.get(1), TEST_OBJECT_1); // from TEST_POINT_3D_1
	}

	@Test
	public void testKDTree_canGetNearest2WithPresentKey1D() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		List<String> result = treeA.nearest(TEST_POINT_1D_MISSING, 3);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.get(0));
		Assert.assertNotNull(result.get(1));
		Assert.assertNotNull(result.get(2));
		Assert.assertEquals(result.get(0), TEST_OBJECT_2); // from TEST_POINT_1D_2
		Assert.assertEquals(result.get(1), TEST_OBJECT_3); // from TEST_POINT_1D_3
		Assert.assertEquals(result.get(2), TEST_OBJECT_1); // from TEST_POINT_1D_1
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotGetNearest2WithInvalidKey() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Search by invalid key (wrong size)
		treeA.nearest(TEST_POINT_1D_INVALID, 2);
	}

	@Test
	public void testKDTree_canGetNearest2WithNullKey() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		List<String> result = treeA.nearest(null, 2);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 0);
	}

	@Test
	public void testKDTree_canGetNearest2WithLackOfData() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		List<String> result = treeA.nearest(TEST_POINT_3D_1, 2);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 0);
	}

	@Test
	public void testKDTree_canGetNearestWithInvalidCount() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by missing key
		List<String> result = treeA.nearest(TEST_POINT_3D_1, 1000);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 4);
	}


	/*********************
	 * TREE NEAREST KEYS *
	 *********************/

	@Test
	public void testKDTree_canGetNearestAsKeys() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		List<TypedKDTree.Point> results = treeA.nearestKeys(TEST_POINT_3D_1, 1);
		Assert.assertNotNull(results);
		Assert.assertTrue(results.size() > 0);
		Assert.assertArrayEquals(
			results.get(0).getKDTreePoint(),
			TEST_POINT_3D_1.getKDTreePoint(),
			0.0
		);
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotGetNearestKeysWithInvalidKey() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Search by invalid key (wrong size)
		treeA.nearestKeys(TEST_POINT_1D_INVALID, 1);
	}

	@Test
	public void testKDTree_canGetNearestKeysWithNullKey() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		List<TypedKDTree.Point> results = treeA.nearestKeys(null, 1);
		Assert.assertEquals(results.size(), 0);
	}

	@Test
	public void testKDTree_canGetNearestKeysWithLackOfData() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();

		// Search by missing key
		List<TypedKDTree.Point> results = treeA.nearestKeys(TEST_POINT_3D_1, 1);
		Assert.assertEquals(results.size(), 0);
	}

	@Test
	public void testKDTree_canGetNearestKeysWithInvalidCount() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by missing key
		List<TypedKDTree.Point> result =
			treeA.nearestKeys(TEST_POINT_3D_1, 1000);

		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 4);
	}


	/**************
	 * TREE RANGE *
	 **************/

	@Test
	public void testKDTree_canRangeSearch3D() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		List<String> result = treeA.range(TEST_RANGE_3D_1, TEST_RANGE_3D_2);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 2);
		Assert.assertNotNull(result.get(0));
		Assert.assertNotNull(result.get(1));
		Assert.assertEquals(result.get(0), TEST_OBJECT_1); // from TEST_POINT_3D_1
		Assert.assertEquals(result.get(1), TEST_OBJECT_3); // from TEST_POINT_3D_4
	}

	@Test
	public void testKDTree_canRangeSearch1D() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		List<String> result = treeA.range(TEST_POINT_1D_2, TEST_POINT_1D_4);
		Assert.assertEquals(result.size(), 3);
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotRangeSearchWithInvalidKeyDiff() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Search by invalid key (wrong size)
		treeA.range(TEST_POINT_1D_INVALID, TEST_POINT_1D_MISSING);
	}

	@Test(expected = IllegalStateException.class)
	public void testKDTree_cannotRangeSearchWithInvalidKeyWrong() {
		TypedKDTree<Point1D, String> treeA = createTreeWithTestData1D();

		// Search by invalid key (wrong size)
		treeA.range(TEST_POINT_1D_INVALID, TEST_POINT_1D_INVALID);
	}

	@Test
	public void testKDTree_canRangeSearchWithInvalidKeyNullLeft() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		List<String> result = treeA.range(null, TEST_RANGE_3D_2);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 0);
	}

	@Test
	public void testKDTree_canRangeSearchWithInvalidKeyNullRight() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		List<String> result = treeA.range(TEST_RANGE_3D_2, null);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 0);
	}

	@Test
	public void testKDTree_canRangeSearchWithLackOfData() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		List<String> result = treeA.range(TEST_RANGE_3D_1, TEST_RANGE_3D_2);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.size(), 0);
	}

	/*********************
	 * TREE OPTIMIZATION *
	 *********************/

	@Test
	public void testKDTree_canOptimizeTree() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();

		// Initial tree items inserted in a random, suboptimal order
		treeA.insert(new Point3D(10, 20, 30), "A")
			.insert(new Point3D(-1, -2, -3), "B")
			.insert(new Point3D(1, 1, 1), "C")
			.insert(new Point3D(10, 2, 3), "D")
			.insert(new Point3D(1, 29, 3), "E") // Specified center point
			.insert(new Point3D(-10, 2, -3), "F")
			.insert(new Point3D(0, 0, 0), "G")
			.insert(new Point3D(2, 6, 3), "H")
			.delete(new Point3D(0, 0, 0));

		String snapshot1 = treeA.toString();
		List<String> snapshotA = new ArrayList<String>(treeA.toMap().values());
		Assert.assertEquals(snapshotA.get(0), "A");

		// Optimize around the median of all points for each dimension
		treeA.optimize(new Point3D(1.5, 29.5, 3.5)); // near the "E" point

		String snapshot2 = treeA.toString();
		List<String> snapshotB = new ArrayList<String>(treeA.toMap().values());
		Assert.assertEquals(snapshotB.get(0), "E");

		// The .toString method shows deleted notes with a '*'. So the second
		// snapshot should have less characters than the first.
		Assert.assertTrue(snapshot1.length() > snapshot2.length());
	}

	/**************
	 * TREE UTILS *
	 **************/

	@Test
	public void testKDTree_canGetLastValue() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		String lastValue = treeA.getLastValue();
		Assert.assertEquals(lastValue, TEST_OBJECT_3);

		lastValue = treeA.delete(TEST_POINT_3D_2).getLastValue();
		Assert.assertEquals(lastValue, TEST_OBJECT_2);
	}

	@Test
	public void testKDTree_canGetLastValues() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		HashMap<Point3D, String> map = new HashMap<Point3D, String>();
		map.put(TEST_POINT_3D_1, TEST_OBJECT_1);
		map.put(TEST_POINT_3D_2, TEST_OBJECT_2);
		map.put(TEST_POINT_3D_3, TEST_OBJECT_3);
		map.put(TEST_POINT_3D_4, TEST_OBJECT_3);

		Map<Point3D, String> lastValues = treeA.insert(map).getLastValues();
		Assert.assertEquals(lastValues.size(), 4);
		Assert.assertEquals(lastValues.get(TEST_POINT_3D_2), TEST_OBJECT_2);

		List<Point3D> keyList = new ArrayList<Point3D>(map.keySet());
		lastValues = treeA.delete(keyList).getLastValues();
		Assert.assertEquals(lastValues.size(), 4);
		Assert.assertEquals(lastValues.get(TEST_POINT_3D_2), TEST_OBJECT_2);
	}

	@Test
	public void testKDTree_canGetTreeSize() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertEquals(treeA.size(), 4);

		TypedKDTree<Point3D, String> treeB = new TypedKDTree<Point3D, String>();
		Assert.assertEquals(treeB.size(), 0);
	}

	@Test
	public void testKDTree_canGetTreeDimensions() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertEquals(treeA.dimensions(), 3);

		TypedKDTree<Point1D, String> treeB = createTreeWithTestData1D();
		Assert.assertEquals(treeB.dimensions(), 1);
	}

	@Test
	public void testKDTree_canGetTreeAsString() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertTrue(treeA.toString().length() > 4);

		TypedKDTree<Point3D, String> treeB = new TypedKDTree<Point3D, String>();
		Assert.assertEquals(treeB.toString(), "null");
	}

	@Test
	public void testKDTree_canGetTreeAsMap() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Map<TypedKDTree.Point, String> testTreeMap = treeA.toMap();
		Assert.assertEquals(testTreeMap.size(), 4);

		treeA.delete(TEST_POINT_3D_3);
		testTreeMap = treeA.toMap();
		Assert.assertEquals(testTreeMap.size(), 3);
	}

	/***************
	 * COMBO TESTS *
	 ***************/

	@Test
	public void testKDTree_canUseChaining() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		String lastValue =
			treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1)
				.insert(TEST_POINT_3D_2, TEST_OBJECT_2)
				.insert(TEST_POINT_3D_3, TEST_OBJECT_3)
				.insert(TEST_POINT_3D_4, TEST_OBJECT_3)
				.delete(TEST_POINT_3D_3)
				.getLastValue();

		Assert.assertEquals(treeA.size(), 3);
		Assert.assertEquals(lastValue, TEST_OBJECT_3);
	}

	@Test
	public void testKDTree_canUpdateData() {
		// Can add an item
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertEquals(treeA.search(TEST_POINT_3D_1), TEST_OBJECT_1);
		Assert.assertEquals(treeA.size(), 4);

		treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_3);
		Assert.assertEquals(treeA.search(TEST_POINT_3D_1), TEST_OBJECT_3);
		Assert.assertEquals(treeA.size(), 4);
	}

	@Test
	public void testKDTree_sizeIsCorrect() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();
		Assert.assertEquals(treeA.size(), 4);

		// Delete data, check size
		treeA.delete(TEST_POINT_3D_1);
		Assert.assertEquals(treeA.size(), 3);

		treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		Assert.assertEquals(treeA.size(), 4);

		treeA.insert(TEST_POINT_3D_MISSING, TEST_OBJECT_2);
		Assert.assertEquals(treeA.size(), 5);
	}

	@Test
	public void testKDTree_canSearchAfterEmptying() {
		TypedKDTree<Point3D, String> treeA = createTreeWithTestData3D();

		// Delete all points
		treeA.delete(TEST_POINT_3D_1);
		treeA.delete(TEST_POINT_3D_2);
		treeA.delete(TEST_POINT_3D_3);
		treeA.delete(TEST_POINT_3D_4);

		// Search while data is supposedly empty
		String result = treeA.nearest(TEST_POINT_3D_1);
		Assert.assertNull(result);
	}


	/** Private test helpers */
	private TypedKDTree<Point3D, String> createTreeWithTestData3D() {
		TypedKDTree<Point3D, String> treeA = new TypedKDTree<Point3D, String>();
		treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_2);
		treeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1); // With repeat key, updated value
		treeA.insert(TEST_POINT_3D_2, TEST_OBJECT_2);
		treeA.insert(TEST_POINT_3D_3, TEST_OBJECT_3);
		treeA.insert(TEST_POINT_3D_4, TEST_OBJECT_3); // With repeat value

		return treeA;
	}

	private TypedKDTree<Point1D, String> createTreeWithTestData1D() {
		TypedKDTree<Point1D, String> treeA = new TypedKDTree<Point1D, String>();
		treeA.insert(TEST_POINT_1D_1, TEST_OBJECT_1);
		treeA.insert(TEST_POINT_1D_2, TEST_OBJECT_2);
		treeA.insert(TEST_POINT_1D_3, TEST_OBJECT_3);
		treeA.insert(TEST_POINT_1D_4, TEST_OBJECT_3); // With repeat value

		return treeA;
	}
}
