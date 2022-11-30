/**
 * %SVN.HEADER%
 * <p>
 * based on work by Simon Levy
 * http://www.cs.wlu.edu/~levy/software/kd/
 */
package net.sf.javaml.core.kdtree;

import java.io.Serializable;
import java.util.*;

/**
 * TypedKDTree is a wrapper class for KDTree that adds in typing. To construct
 * this tree the developer will need to provide generics for both the key and
 * the value types. It has several differences to KDTree:
 * -
 *   • The underlying KDTree is lazily created once the first point is inserted,
 *       who's dimensional size will be used as the source of truth from thereon
 * -
 *   • Generics restrict the key and value types to avoid runtime errors. The
 *       key type must be a class implementing interface `TypedKDTree.Point`.
 *       This means you won't need to cast return values. See the below example.
 * -
 *   • `insert` and `delete` methods return a reference to the typed tree itself
 *       which can be used for method chaining! Best combined with
 *       `getLastValue` and `getLastValues` (only works on the last action).
 * -
 *   • There are 8 new or expanded methods, from bulk insertion/deletion to
 *       a method that can optimize the tree layout magically.
 * -
 *   • Every method returns a value:
 *       1. Either the tree itself, a value, or void if operational.
 *       2. If returning a value, then null if the value is not found.
 *       3. If the key provided is null (or provides null), the return is null.
 * -
 *   • `nearest(K key, int n)` and `nearestKeys(K key, int n)` will
 *       automatically round "n" down if greater than the size of the tree. This
 *       prevents an exception that KDTree would otherwise throw.
 * -
 *   • `toMap` will return keys as generic `Point` objects, but these will not
 *       be the same as your key type, you may want to convert them.
 * -
 *   • Most methods now only throw IllegalStateException, when your `Point`
 *       implementation has not been done correctly.
 *
 * @example
 *  // 1. Have your existing class implement TypedKDTree.KDTreePoint OR
 *  //    create a small new class like the one below:
 *  class Point3D implements TypedKDTree.Point {
 *    private final double[] point;
 *    public Point3D(double x, double y, double z) {
 *      point = new double[]{ x, y, z };
 *    }
 *    @Override
 *    public double[] getKDTreePoint() { return point; }
 *  }
 *  // 2. Create a TypedKDTree with type generics;
 *  TypedKDTree<Point3D, String> tree = new TypedKDTree<Point3D, String>();
 *  tree.insert(new Point3D(1, 2, 3), "Yes!")
 *      .insert(new Point3D(-1, -2, -3), "No!"); // Chaining supported
 *  String maybe = tree.insert(new Point3D(0, 0, 0), "Maybe").getLastValue();
 *
 * @author Paul Dilley
 * @version %I%, %G%
 * @since JDK1.6
 */
public class TypedKDTree<K extends TypedKDTree.Point, V> implements Serializable {
    /**
     * TypedKDTree only accepts keys that are classes having implemented this
     * Point. You can modify your existing class to implement this
     * interface, and adding a `getKDTreePoint` method which returns the correct
     * coordinate point as a double array, or create your own small helper class
     * to do so (see the example in the docs prior).
     * At runtime, the `getKDTreePoint` is called to get the double[] point.
     */
    public interface Point {
        /**
         * Return the coordinate as a double array
         * @return double array representing each dimension
         */
        double[] getKDTreePoint();
    }
    
    /**
     * The underlying KDTree is lazily loaded
     */
    private KDTree kdTree = null;

    /**
     * Caches the last inserted or deleted values
     */
    private Map<K, V> lastValues = new LinkedHashMap<K, V>();
    private K lastKey = null;
    private boolean isMultipleValues = false;

    /**
     * The underlying KDTree is lazily created the first time `insert` is called
     * However you may want to pre-emptively initialize it if you are worried
     * about performance or memory allocation later down the line, or you don't
     * control the `Point` implementation and want to enforce a certain
     * number of dimensions.
     *
     * @param dimensions
     * 	        number of dimensions
     *
     * @throws IllegalArgumentException if the dimensions provided is < 1
     * @throws UnsupportedOperationException if already initialized
     */
    public void initialize(int dimensions) {
        if (dimensions < 1) {
            throw new IllegalArgumentException("TypedKDTree: Your " +
                "`Point` must have dimensions >= 1");
        }

        if (kdTree == null) {
            kdTree = new KDTree(dimensions);
        } else {
            throw new UnsupportedOperationException("TypedKDTree: The " +
                "underlying KDTree has already been initialized");
        }
    }

    /**
     * Insert a node in a KD-tree. Uses algorithm translated from 352.ins.c of
     *
     * @param key
     *          key for TypedKDTree node
     * @param value
     *          value at that key
     *
     * @return the tree itself, for chaining
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public TypedKDTree<K, V> insert(K key, V value) {
        if (!isMultipleValues) {
            lastValues = new LinkedHashMap<K, V>();
        }

        if (isKeyOrTreeNull(key, true)) {
            return this;
        }

        if (kdTree == null) {
            initialize(key.getKDTreePoint().length);
        }

        kdTree.insert(key.getKDTreePoint(), value);
        lastValues.put(key, value);
        lastKey = key;

        return this;
    }
    
    /**
     * Insert multiple nodes in a KD-tree
     * 
     * @param keyValuePairs
     *          a map of the key-value pairs to insert
     *          
     * @return the tree itself, for chaining
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public TypedKDTree<K, V> insert(Map<K,V> keyValuePairs) {
        isMultipleValues = true;

        try {
            lastValues = new LinkedHashMap<K, V>();

            if (keyValuePairs != null) {
                for (Map.Entry<K, V> entry : keyValuePairs.entrySet()) {
                    insert(entry.getKey(), entry.getValue());
                }
            }
        } finally {
            isMultipleValues = false;
        }

        return this;
    }

    /**
     * Find KD-tree node whose key is identical to key. Uses algorithm
     * translated from 352.srch.c of Gonnet & Baeza-Yates.
     *
     * @param key
     *          key for TypedKDTree node
     *
     * @return typed object at key, or null if not found or key is null
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public V search(K key) {
        if (isKeyOrTreeNull(key)) return null;

        @SuppressWarnings("unchecked") V result = (V) kdTree.search(
            key.getKDTreePoint()
        );

        return result;
    }

    /**
     * Delete a node from a KD-tree. Instead of actually deleting node and
     * rebuilding tree, marks node as deleted. Hence, it is up to the caller to
     * call optimize() to prune these deleted nodes
     *
     * @param key
     *          key for TypedKDTree node
     *
     * @return the tree itself, for chaining
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public TypedKDTree<K, V> delete(K key) {
        if (!isMultipleValues) {
            lastValues = new LinkedHashMap<K, V>();
        }

        if (isKeyOrTreeNull(key)) return this;

        V result = search(key);
        if (result != null) {
            kdTree.delete(key.getKDTreePoint());
            lastValues.put(key, result);
            lastKey = key;
        }

        return this;
    }

    /**
     * Delete multiple nodes from a KD-tree.
     *
     * @param keys
     *          keys for TypedKDTree node
     *
     * @return the tree itself, for chaining
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public TypedKDTree<K, V> delete(List<K> keys) {
        isMultipleValues = true;

        try {
            lastValues = new LinkedHashMap<K, V>();

            if (keys != null && keys.size() > 0) {
                for (K key : keys) {
                    delete(key);
                }
            }
        } finally {
            isMultipleValues = false;
        }

        return this;
    }

    /**
     * Find KD-tree node whose key is nearest neighbor to key. Implements the
     * Nearest Neighbor algorithm (Table 6.4) of...
     *
     * @param key
     *          key for TypedKDTree node
     *
     * @return object at node nearest to key,
     *         or null if not found, tree is empty, or key is invalid
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public V nearest(K key) {
        if (isKeyOrTreeNull(key) || kdTree.size() == 0) {
            return null;
        }

        @SuppressWarnings("unchecked") V result = (V) kdTree.nearest(
            key.getKDTreePoint()
        );
        return result;
    }

    /**
     * Find KD-tree nodes whose keys are <I>n</I> nearest neighbors to key. Uses
     * algorithm above. Neighbors are returned in ascending order of distance to
     * key.
     *
     * @param key
     *          key for TypedKDTree node
     * @param n
     *          how many neighbors to find
     *
     * @return objects at node nearest to keys,
     *         or null if not found, tree is empty, or key is invalid
     *         (the max items size returned is limited to the current tree size)
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public List<V> nearest(K key, int n) {
        int size = kdTree != null ? kdTree.size() : 0;

        List<V> results = new ArrayList<V>();

        // Return empty array if key is null or tree is null/empty
        if (isKeyOrTreeNull(key) || size == 0) {
            return results;
        }

        Object[] v = kdTree.nearest(key.getKDTreePoint(), Math.min(n, size));
        // Return empty array if result is null or no results
        if (v == null || v.length == 0) {
            return results;
        }

        for (Object item : v) {
            @SuppressWarnings("unchecked") V typedItem = (V) item;
            results.add(typedItem);
        }

        return results;
    }


    /**
     * Find KD-tree node keys whose keys are <I>n</I> nearest neighbors to key.
     * Uses algorithm above. Neighbors are returned in ascending order of
     * distance to key.
     *   Note that the keys will be implementations of `Point`, but will not be
     *   the Point implementation you provided.
     *
     * @param key
     *            key for KD-tree node
     * @param n
     *            how many neighbors to find
     *
     * @return keys at node nearest to key, or null on failure
     *
     */
    public List<Point> nearestKeys(K key, int n) {
        int size = kdTree != null ? kdTree.size() : 0;

        List<Point> results = new ArrayList<Point>();

        if (isKeyOrTreeNull(key) || size == 0) {
            return results;
        }

        double[][] keys =
            kdTree.nearestKeys(key.getKDTreePoint(), Math.min(n, size));

        if (keys == null) return results;

        for (double[] rawKey : keys) {
            results.add(new UncheckedPoint(rawKey));
        }

        return results;
    }

    /**
     * Range search in a KD-tree. Uses algorithm translated from 352.range.c of
     * Gonnet & Baeza-Yates.
     *
     * @param lowk
     *          lower-bounds for key
     * @param uppk
     *          upper-bounds for key
     *
     * @return array of Objects whose keys fall in range [lowk,uppk]
     *         or null if either key is null
     *
     * @throws IllegalStateException if key length mismatches the size defined
     *                               in the TypedKDTree
     */
    public List<V> range(K lowk, K uppk) {
        int size = kdTree != null ? kdTree.size() : 0;
        List<V> results = new ArrayList<V>();

        // Return empty array if key is null or tree is null/empty
        if (isKeyOrTreeNull(lowk) || isKeyOrTreeNull(uppk) || size == 0) {
            return results;
        }

        Object[] v = kdTree.range(lowk.getKDTreePoint(), uppk.getKDTreePoint());
        // Return empty array if result is null or no results
        if (v == null || v.length == 0) {
            return results;
        }

        for (Object item : v) {
            @SuppressWarnings("unchecked") V typedItem = (V) item;
            results.add(typedItem);
        }

        return results;
    }

    /**
     * Get all the data points of the KDTree, with correct value data type.
     * The map is ordered: top-down, left-to-right.
     *   Note that the keys will be implementations of `Point`, but will not be
     *   the Point implementation you provided.
     *
     * @return returns a map of all the data points
     */
    public Map<TypedKDTree.Point, V> toMap() {
        Map<TypedKDTree.Point, V> results =
            new LinkedHashMap<TypedKDTree.Point, V>();

        Map<double[], Object> map = kdTree != null ?  kdTree.toMap() : null;

        if (map == null) return results;

        Set<Map.Entry<double[], Object>> mapSet = map.entrySet();
        for (Map.Entry<double[], Object> entry : mapSet) {
            @SuppressWarnings({"unchecked"}) V value = (V) entry.getValue();
            results.put(new UncheckedPoint(entry.getKey()), value);
        }

        return results;
    }

    /**
     * After using insert or delete, this can be called to get the very last
     * value that was inserted or removed from the tree. Useful for chaining.
     *
     * @return The very last value, either inserted or deleted
     */
    public V getLastValue() {
        if (lastKey == null || lastValues == null){
            return null;
        }
        return lastValues.get(lastKey);
    }

    /**
     * After using insert or delete, this can be called to get the last changes
     * made to the tree. This is useful for chaining.
     *   Note: It only returns values for the final call, so use one call to
     *         insert multiple values if you need to get them all back.
     *
     * @return A map of the last changes, either inserted or deleted
     */
    public Map<K, V> getLastValues() {
        return lastValues;
    }

    /**
     * Refreshes the tree, creating a new one that excludes any items marked as
     * deleted, and inserts the rest back in the nearest order to the provided
     * center point.
     *
     * @param point
     *          centre point to determine nearest, and insert into the tree from
     *          nearest to furthest order
     */
    public void optimize(K point) {
        if (kdTree == null || kdTree.size() == 0) return;

        if (point == null) {
            throw new IllegalArgumentException("TypedKDTree: To optimize the" +
                "tree, you must provide a valid center point, but you passed" +
                " null");
        }

        int dimensions = kdTree.dimensions();
        int size = kdTree.size();
        double[] centerPoint = point.getKDTreePoint();
        Map<double[], Object> map = kdTree.toMap();

        // Get points in order of nearest from the center point
        double[][] result = kdTree.nearestKeys(centerPoint, size);

        // Create a new tree, insert key-values back in, in nearest order
        kdTree = new KDTree(dimensions);
        for (double[] key : result) {
            kdTree.insert(key, map.get(key));
        }
    }

    /**
     * The size of the KDTree.
     *
     * @return returns the number of key-value mappings in this KDTree, or 0 if
     *         not initialized or empty
     */
    public int size() {
        return kdTree != null ? kdTree.size() : 0;
    }

    /**
     * The number of dimensions of the KDTree.
     *
     * @return returns the number of dimensions of this KDTree, or 0 if the
     *         underlying tree hasn't yet been initialized
     */
    public int dimensions() {
        return kdTree != null ? kdTree.dimensions() : 0;
    }

    /**
     * String representation of the tree
     *
     * @return KDTree to string
     */
    public String toString() {
        return kdTree != null ? kdTree.toString() : "null";
    }


    private boolean isKeyOrTreeNull(K key) {
        return isKeyOrTreeNull(key, false);
    }
    private boolean isKeyOrTreeNull(K key, boolean skipTreeCheck) {
        if (!skipTreeCheck && kdTree == null) {
            return true;
        }

        boolean isNullKey = key == null || key.getKDTreePoint() == null;

        // Validate the returned key is the right key length
        if (!isNullKey && kdTree != null &&
                key.getKDTreePoint().length != kdTree.dimensions()) {
            throw new IllegalStateException("TypedKDTree: The key returned by" +
                " your `KDTreePoint` implementation must have the same length" +
                " as the TypedKDTree");
        }

        return isNullKey;
    }

    /**
     * This is used internally by TypedKDTree.
     *   It bypasses type checks by just accepting a double[], so it's not for
     *   use outside of guaranteed situations within this class.
     */
    private static class UncheckedPoint implements TypedKDTree.Point {
        double[] point;
        public UncheckedPoint(double[] point) { this.point = point; }
        @Override
        public double[] getKDTreePoint() { return point; }
    }
}
