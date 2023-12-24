/**
 * %SVN.HEADER%
 */
package junit.core;

import net.sf.javaml.core.kdtree.KDTree;
import org.junit.Assert;
import org.junit.Test;
import java.util.Map;
import java.util.NoSuchElementException;

public class TestKDTree {
	static final double[] TEST_POINT_3D_1 = new double[]{ 1, 2, 3 };
	static final double[] TEST_POINT_3D_2 = new double[]{ -1, -2, -3 };
	static final double[] TEST_POINT_3D_3 = new double[]{ 15, 10, -3 };
	static final double[] TEST_POINT_3D_4 = new double[]{ 10, 0, 0 };
	static final double[] TEST_POINT_3D_MISSING = new double[]{ 0, -2, -3 };

	static final double[] TEST_RANGE_3D_1 = new double[]{ 0, 0, 0 };
	static final double[] TEST_RANGE_3D_2 = new double[]{ 20, 20, 20 };

	static final double[] TEST_POINT_2D_1 = new double[]{ 1, 2 };
	static final double[] TEST_POINT_2D_2 = new double[]{ -1, -2 };

	static final double[] TEST_POINT_1D_1 = new double[]{ 2.2 };
	static final double[] TEST_POINT_1D_2 = new double[]{ -2.2 };
	static final double[] TEST_POINT_1D_3 = new double[]{ -3.3 };
	static final double[] TEST_POINT_1D_4 = new double[]{ 5.5 };
	static final double[] TEST_POINT_1D_MISSING = new double[]{ -1 }; // Nearest to TEST_POINT_1D_2

	static final String TEST_OBJECT_1 = "TEST 1";
	static final String TEST_OBJECT_2 = "TEST 2";
	static final String TEST_OBJECT_3 = "TEST 3";


	/*****************
	 * TREE CREATION *
	 *****************/

	@Test
	public void testKDTree_creationSuccess() {
		KDTree testTreeA = new KDTree(1);
		Assert.assertNotNull(testTreeA);

		KDTree testTreeB = new KDTree(3);
		Assert.assertNotNull(testTreeB);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_creationFailureZero() {
		new KDTree(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_creationFailureNegative() {
		new KDTree(-1);
	}


	/******************
	 * TREE INSERTION *
	 ******************/

	@Test
	public void testKDTree_canAdd3DData() {
		// Can add an item
		KDTree testTreeA = new KDTree(3);
		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		Assert.assertEquals(testTreeA.size(), 1);

		// Add same point again, should be no change in count
		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		Assert.assertEquals(testTreeA.size(), 1);

		// Add more points
		testTreeA.insert(TEST_POINT_3D_2, TEST_OBJECT_2);
		testTreeA.insert(TEST_POINT_3D_3, TEST_OBJECT_3);
		testTreeA.insert(TEST_POINT_3D_4, TEST_OBJECT_3); // With repeat value
		Assert.assertEquals(testTreeA.size(), 4);
	}

	@Test
	public void testKDTree_canAdd2DData() {
		// Can add an item
		KDTree testTreeA = new KDTree(2);
		testTreeA.insert(TEST_POINT_2D_1, TEST_OBJECT_1);
		testTreeA.insert(TEST_POINT_2D_2, TEST_OBJECT_2);
		Assert.assertEquals(testTreeA.size(), 2);

		// Add same point again, should be no change in count
		testTreeA.insert(TEST_POINT_2D_1, TEST_OBJECT_3);
		Assert.assertEquals(testTreeA.size(), 2);
	}

	@Test
	public void testKDTree_canAdd1DData() {
		// Can add an item
		KDTree testTreeA = new KDTree(1);
		testTreeA.insert(TEST_POINT_1D_1, TEST_OBJECT_1);
		testTreeA.insert(TEST_POINT_1D_2, TEST_OBJECT_2);
		Assert.assertEquals(testTreeA.size(), 2);

		// Add same point again, should be no change in count
		testTreeA.insert(TEST_POINT_1D_1, TEST_OBJECT_3);
		Assert.assertEquals(testTreeA.size(), 2);
	}

	@Test
	public void testKDTree_canAddNullData() {
		// Can add an item
		KDTree testTreeA = new KDTree(1);
		testTreeA.insert(TEST_POINT_1D_1, null);
		testTreeA.insert(TEST_POINT_1D_2, null);
		Assert.assertEquals(testTreeA.size(), 2);

		// Add same point again, should be no change in count
		testTreeA.insert(TEST_POINT_1D_1, null);
		Assert.assertEquals(testTreeA.size(), 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotAddMixedKeys() {
		// Attempt to add 2D to a 3D tree
		KDTree testTreeA = new KDTree(3);
		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		testTreeA.insert(TEST_POINT_2D_1, TEST_OBJECT_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotAddNullKey() {
		// Attempt to add null to a 3D tree
		KDTree testTreeA = new KDTree(3);
		testTreeA.insert(null, TEST_OBJECT_1);
	}


	/******************
	 * TREE SEARCHING *
	 ******************/

	@Test
	public void testKDTree_canSearchByPresentKey3D() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Get by valid key
		Object result = testTreeA.search(TEST_POINT_3D_2);
		Assert.assertTrue(result instanceof String);
		Assert.assertEquals(result, TEST_OBJECT_2);
	}

	@Test
	public void testKDTree_canSearchByMissingKey3D() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Get by missing valid key
		Object result = testTreeA.search(TEST_POINT_3D_MISSING);
		Assert.assertNull(result);
	}

	@Test
	public void testKDTree_canSearchByPresentKey1D() {
		KDTree testTreeA = createTreeWithTestData1D();

		// Get by valid key
		Object result = testTreeA.search(TEST_POINT_1D_3);
		Assert.assertTrue(result instanceof String);
		Assert.assertEquals(result, TEST_OBJECT_3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotSearchInvalidKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.search(TEST_POINT_2D_1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotSearchNullKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.search(null);
	}


	/*****************
	 * TREE DELETING *
	 *****************/

	@Test
	public void testKDTree_canDeletePresentKey3D() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Delete data, check it's gone
		testTreeA.delete(TEST_POINT_3D_2);
		Object result = testTreeA.search(TEST_POINT_3D_2);
		Assert.assertNull(result);
	}

	@Test
	public void testKDTree_canDeletePresentKey1D() {
		KDTree testTreeA = createTreeWithTestData1D();

		// Delete data, check it's gone
		testTreeA.delete(TEST_POINT_1D_2);
		Object result = testTreeA.search(TEST_POINT_1D_2);
		Assert.assertNull(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotDeleteInvalidKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.delete(TEST_POINT_2D_1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotDeleteNullKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.delete(null);
	}

	@Test(expected = NoSuchElementException.class)
	public void testKDTree_cannotDeleteByMissingKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by missing key
		testTreeA.delete(TEST_POINT_3D_MISSING);
	}


	/*****************************
	 * TREE NEAREST - SINGLE ARG *
	 *****************************/

	@Test
	public void testKDTree_canGetNearestWithPresentKey3D() {
		KDTree testTreeA = createTreeWithTestData3D();

		Object result = testTreeA.nearest(TEST_POINT_3D_MISSING);
		Assert.assertTrue(result instanceof String);
		Assert.assertEquals(result, TEST_OBJECT_2); // from TEST_POINT_3D_2
	}

	@Test
	public void testKDTree_canGetNearestWithPresentKey1D() {
		KDTree testTreeA = createTreeWithTestData1D();

		Object result = testTreeA.nearest(TEST_POINT_1D_MISSING);
		Assert.assertTrue(result instanceof String);
		Assert.assertEquals(result, TEST_OBJECT_2); // from TEST_POINT_1D_2
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotGetNearestWithInvalidKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.nearest(TEST_POINT_2D_1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotGetNearestWithNullKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.nearest(null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testKDTree_cannotGetNearestWithLackOfData() {
		KDTree testTreeA = new KDTree(3);

		// Search by missing key
		testTreeA.nearest(TEST_POINT_3D_1);
	}


	/*******************************
	 * TREE NEAREST - MULTIPLE ARG *
	 *******************************/

	@Test
	public void testKDTree_canGetNearest2WithPresentKey3D() {
		KDTree testTreeA = createTreeWithTestData3D();

		Object[] result = testTreeA.nearest(TEST_POINT_3D_MISSING, 2);
		Assert.assertTrue(result[0] instanceof String);
		Assert.assertTrue(result[1] instanceof String);
		Assert.assertEquals(result[0], TEST_OBJECT_2); // from TEST_POINT_3D_2
		Assert.assertEquals(result[1], TEST_OBJECT_1); // from TEST_POINT_3D_1
	}

	@Test
	public void testKDTree_canGetNearest2WithPresentKey1D() {
		KDTree testTreeA = createTreeWithTestData1D();

		Object[] result = testTreeA.nearest(TEST_POINT_1D_MISSING, 3);
		Assert.assertTrue(result[0] instanceof String);
		Assert.assertTrue(result[1] instanceof String);
		Assert.assertTrue(result[2] instanceof String);
		Assert.assertEquals(result[0], TEST_OBJECT_2); // from TEST_POINT_1D_2
		Assert.assertEquals(result[1], TEST_OBJECT_3); // from TEST_POINT_1D_3
		Assert.assertEquals(result[2], TEST_OBJECT_1); // from TEST_POINT_1D_1
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotGetNearest2WithInvalidKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.nearest(TEST_POINT_2D_1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotGetNearest2WithNullKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.nearest(null, 2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testKDTree_cannotGetNearest2WithLackOfData() {
		KDTree testTreeA = new KDTree(3);

		// No data in the tree
		testTreeA.nearest(TEST_POINT_3D_1, 2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testKDTree_cannotGetNearestWithInvalidCount() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by missing key
		testTreeA.nearest(TEST_POINT_3D_1, 1000);
	}


	/*********************
	 * TREE NEAREST KEYS *
	 *********************/

	@Test
	public void testKDTree_canGetNearestAsKeys() {
		KDTree testTreeA = createTreeWithTestData3D();

		double[][] results = testTreeA.nearestKeys(TEST_POINT_3D_1, 1);
		Assert.assertNotNull(results);
		Assert.assertTrue(results.length > 0);
		Assert.assertArrayEquals(results[0], TEST_POINT_3D_1, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotGetNearestKeysWithInvalidKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.nearestKeys(TEST_POINT_2D_1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotGetNearestKeysWithNullKey() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.nearestKeys(null, 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testKDTree_cannotGetNearestKeysWithLackOfData() {
		KDTree testTreeA = new KDTree(3);

		// Search by missing key
		testTreeA.nearestKeys(TEST_POINT_3D_1, 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testKDTree_cannotGetNearestKeysWithInvalidCount() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by missing key
		testTreeA.nearestKeys(TEST_POINT_3D_1, 1000);
	}


	/**************
	 * TREE RANGE *
	 **************/

	@Test
	public void testKDTree_canRangeSearch3D() {
		KDTree testTreeA = createTreeWithTestData3D();

		Object[] result = testTreeA.range(TEST_RANGE_3D_1, TEST_RANGE_3D_2);
		Assert.assertEquals(result.length, 2);
		Assert.assertTrue(result[0] instanceof String);
		Assert.assertTrue(result[1] instanceof String);
		Assert.assertEquals(result[0], TEST_OBJECT_1); // from TEST_POINT_3D_1
		Assert.assertEquals(result[1], TEST_OBJECT_3); // from TEST_POINT_3D_4
	}

	@Test
	public void testKDTree_canRangeSearch1D() {
		KDTree testTreeA = createTreeWithTestData1D();

		Object[] result = testTreeA.range(
			TEST_POINT_1D_2,
			TEST_POINT_1D_4
		);
		Assert.assertEquals(result.length, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotRangeSearchWithInvalidKeyDiff() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.range(TEST_POINT_2D_1, TEST_RANGE_3D_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotRangeSearchWithInvalidKeyWrong() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (wrong size)
		testTreeA.range(TEST_POINT_2D_1, TEST_POINT_2D_1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotRangeSearchWithInvalidKeyNullLeft() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.range(null, TEST_RANGE_3D_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKDTree_cannotRangeSearchWithInvalidKeyNullRight() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Search by invalid key (null)
		testTreeA.range(TEST_RANGE_3D_2, null);
	}

	@Test
	public void testKDTree_canRangeSearchWithLackOfData() {
		KDTree testTreeA = new KDTree(3);
		Object[] result = testTreeA.range(TEST_RANGE_3D_1, TEST_RANGE_3D_2);
		Assert.assertEquals(result.length, 0);
	}


	/**************
	 * TREE UTILS *
	 **************/

	@Test
	public void testKDTree_canGetTreeSize() {
		KDTree testTreeA = createTreeWithTestData3D();
		Assert.assertEquals(testTreeA.size(), 4);

		KDTree testTreeB = new KDTree(3);
		Assert.assertEquals(testTreeB.size(), 0);
	}

	@Test
	public void testKDTree_canGetTreeDimensions() {
		KDTree testTreeA = createTreeWithTestData3D();
		Assert.assertEquals(testTreeA.dimensions(), 3);

		KDTree testTreeB = createTreeWithTestData1D();
		Assert.assertEquals(testTreeB.dimensions(), 1);
	}

	@Test
	public void testKDTree_canGetTreeAsString() {
		KDTree testTreeA = createTreeWithTestData3D();
		Assert.assertTrue(testTreeA.toString().length() > 4);

		KDTree testTreeB = new KDTree(3);
		Assert.assertEquals(testTreeB.toString(), "null");
	}

	@Test
	public void testKDTree_canGetTreeAsMap() {
		KDTree testTreeA = createTreeWithTestData3D();
		Map<double[], Object> testTreeMap = testTreeA.toMap();
		Assert.assertEquals(testTreeMap.size(), 4);

		testTreeA.delete(TEST_POINT_3D_3);
		testTreeMap = testTreeA.toMap();
		Assert.assertEquals(testTreeMap.size(), 3);
	}

	@Test
	public void testKDTree_canGetTreeAsMapWithLackOfData() {
		KDTree testTreeA = new KDTree(3);
		Map<double[], Object> testTreeMap = testTreeA.toMap();
		Assert.assertNull(testTreeMap);
	}


	/***************
	 * COMBO TESTS *
	 ***************/

	@Test
	public void testKDTree_canUpdateData() {
		// Can add an item
		KDTree testTreeA = createTreeWithTestData3D();
		Assert.assertEquals(testTreeA.search(TEST_POINT_3D_1), TEST_OBJECT_1);
		Assert.assertEquals(testTreeA.size(), 4);

		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_3);
		Assert.assertEquals(testTreeA.search(TEST_POINT_3D_1), TEST_OBJECT_3);
		Assert.assertEquals(testTreeA.size(), 4);
	}

	@Test
	public void testKDTree_sizeIsCorrect() {
		KDTree testTreeA = createTreeWithTestData3D();
		Assert.assertEquals(testTreeA.size(), 4);

		// Delete data, check size
		testTreeA.delete(TEST_POINT_3D_1);
		Assert.assertEquals(testTreeA.size(), 3);

		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1);
		Assert.assertEquals(testTreeA.size(), 4);

		testTreeA.insert(TEST_POINT_3D_MISSING, TEST_OBJECT_2);
		Assert.assertEquals(testTreeA.size(), 5);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testKDTree_cannotSearchAfterEmptying() {
		KDTree testTreeA = createTreeWithTestData3D();

		// Delete all points
		testTreeA.delete(TEST_POINT_3D_1);
		testTreeA.delete(TEST_POINT_3D_2);
		testTreeA.delete(TEST_POINT_3D_3);
		testTreeA.delete(TEST_POINT_3D_4);

		// Search while data is supposedly empty
		testTreeA.nearest(TEST_POINT_3D_1);
	}


	@Test
	public void testKDTree_smokeTest() {
		KDTree testTreeA = new KDTree(3);
		testTreeA.insert(new double[]{ 1, 2, 3 }, TEST_OBJECT_1);
		testTreeA.insert(new double[]{ -1, 2, 3 }, TEST_OBJECT_2);
		testTreeA.insert(new double[]{ 1, -2, 3 }, TEST_OBJECT_3);
		testTreeA.insert(new double[]{ 10, 20, -3 }, TEST_OBJECT_3); // With repeat value
		testTreeA.insert(new double[]{ 12, 2.5, -34 }, TEST_OBJECT_1);
		testTreeA.insert(new double[]{ 0, 0, 0 }, TEST_OBJECT_2);
		testTreeA.insert(new double[]{ -0.5, 2, 3 }, TEST_OBJECT_3);

		String result = (String) testTreeA.search(new double[]{ 1, 2, 3 });
		Assert.assertEquals(result, TEST_OBJECT_1);
		Assert.assertEquals(testTreeA.size(), 7);

		testTreeA.insert(new double[]{ 1, 2, 3 }, TEST_OBJECT_3); // With repeat key
		String result2 = (String) testTreeA.search(new double[]{ 1, 2, 3 });
		Assert.assertEquals(result2, TEST_OBJECT_3);
		Assert.assertEquals(testTreeA.size(), 7);

		testTreeA.delete(new double[]{ 1, 2, 3 });
		String result4 = (String) testTreeA.search(new double[]{ 1, 2, 3 });
		Assert.assertNull(result4);
		Assert.assertEquals(testTreeA.size(), 6);

		String result5 =
			(String) testTreeA.nearest(new double[]{ -0.1, -0.1, -0.1 });
		Assert.assertEquals(result5, TEST_OBJECT_2);

		testTreeA.insert(new double[]{ -0.1, -0.1, -0.1 }, TEST_OBJECT_1);
		String result6 =
			(String) testTreeA.nearest(new double[]{ -0.1, -0.1, -0.1 });
		Assert.assertEquals(result6, TEST_OBJECT_1);
	}


	/** Private test helpers */
	private KDTree createTreeWithTestData3D() {
		KDTree testTreeA = new KDTree(3);
		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_2);
		testTreeA.insert(TEST_POINT_3D_1, TEST_OBJECT_1); // With repeat key, updated value
		testTreeA.insert(TEST_POINT_3D_2, TEST_OBJECT_2);
		testTreeA.insert(TEST_POINT_3D_3, TEST_OBJECT_3);
		testTreeA.insert(TEST_POINT_3D_4, TEST_OBJECT_3); // With repeat value

		return testTreeA;
	}

	private KDTree createTreeWithTestData1D() {
		KDTree testTreeA = new KDTree(1);
		testTreeA.insert(TEST_POINT_1D_1, TEST_OBJECT_1);
		testTreeA.insert(TEST_POINT_1D_2, TEST_OBJECT_2);
		testTreeA.insert(TEST_POINT_1D_3, TEST_OBJECT_3);
		testTreeA.insert(TEST_POINT_1D_4, TEST_OBJECT_3); // With repeat value

		return testTreeA;
	}
}
