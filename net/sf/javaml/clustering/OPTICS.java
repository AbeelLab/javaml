/**
 * OPTICS.java
 *
 * This file is part of the Java Machine Learning API
 * 
 * The Java Machine Learning API is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The Java Machine Learning API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning API; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2004, Matthias Schubert (schubert@dbs.ifi.lmu.de)
 * Copyright (c) 2004, Zhanna Melnikova-Albrecht (melnikov@cip.ifi.lmu.de)
 * Copyright (c) 2004, Rainer Holzmann (holzmann@cip.ifi.lmu.de)
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.NormalizedEuclideanDistance;

/**
 * Mihael Ankerst, Markus M. Breunig, Hans-Peter Kriegel, Joerg Sander: OPTICS:
 * Ordering Points To Identify the Clustering Structure. In: ACM SIGMOD
 * International Conference on Management of Data, 49-60, 1999. <p/>
 * 
 * TODO clean implementation of different types of queue and object in those 
 * queues, the code should compile without warnings.
 * XXX add references
 * XXX add pseudocode
 * 
 * @author Matthias Schubert (schubert@dbs.ifi.lmu.de)
 * @author Zhanna Melnikova-Albrecht (melnikov@cip.ifi.lmu.de)
 * @author Rainer Holzmann (holzmann@cip.ifi.lmu.de)
 * @author Thomas Abeel
 */
public class OPTICS extends AbstractDensityBasedClustering implements Clusterer {

    /**
     * Specifies the radius for a range-query
     */
    private double epsilon = 0.1;

    /**
     * Specifies the density (the range-query must contain at least minPoints
     * DataObjects)
     */
    private int minPoints = 6;

    /**
     * Emits the k next-neighbours and performs an epsilon-range-query at the
     * parallel. The returned list contains two elements: At index=0 --> list
     * with all k next-neighbours; At index=1 --> list with all dataObjects
     * within epsilon;
     * 
     * @param k
     *            number of next neighbours
     * @param epsilon
     *            Specifies the range for the query
     * @param dataObject
     *            the start object
     * @return list with the k-next neighbours (PriorityQueueElements) and a
     *         list with candidates from the epsilon-range-query
     *         (EpsilonRange_ListElements)
     */
    private List<Object> k_nextNeighbourQuery(int k, double epsilon, DataObject dataObject) {
        // Iterator iterator = dataObjectIterator();

        List<Object> return_List = new ArrayList<Object>();
        List<Object> nextNeighbours_List = new ArrayList<Object>();
        List<EpsilonRange_ListElement> epsilonRange_List = new ArrayList<EpsilonRange_ListElement>();

        PriorityQueue priorityQueue = new PriorityQueue();

        // while (iterator.hasNext()) {
        for (int i = 0; i < dataset.size(); i++) {
            DataObject next_dataObject = dataset.get(i);// (DataObject)
                                                        // iterator.next();
            //System.out.println(dataObject.instance);
           // System.out.println(next_dataObject.instance);
            double dist = dm.calculateDistance(dataObject.instance, next_dataObject.instance);

            if (dist <= epsilon)
                epsilonRange_List.add(new EpsilonRange_ListElement(dist, next_dataObject));

            if (priorityQueue.size() < k) {
                priorityQueue.add(dist, next_dataObject);
            } else {
                if (dist < priorityQueue.getPriority(0)) {
                    priorityQueue.next(); // removes the highest distance
                    priorityQueue.add(dist, next_dataObject);
                }
            }
        }

        while (priorityQueue.hasNext()) {
            nextNeighbours_List.add(0, priorityQueue.next());
        }

        return_List.add(nextNeighbours_List);
        return_List.add(epsilonRange_List);
        return return_List;
    }

    /**
     * Calculates the coreDistance for the specified DataObject. The returned
     * list contains three elements: At index=0 --> list with all k
     * next-neighbours; At index=1 --> list with all dataObjects within epsilon;
     * At index=2 --> coreDistance as Double-value
     * 
     * @param minPoints
     *            minPoints-many neighbours within epsilon must be found to have
     *            a non-undefined coreDistance
     * @param epsilon
     *            Specifies the range for the query
     * @param dataObject
     *            Calculate coreDistance for this dataObject
     * @return list with the k-next neighbours (PriorityQueueElements) and a
     *         list with candidates from the epsilon-range-query
     *         (EpsilonRange_ListElements) and the double-value for the
     *         calculated coreDistance
     */
    private List coreDistance(int minPoints, double epsilon, DataObject dataObject) {
        List<Object> list = k_nextNeighbourQuery(minPoints, epsilon, dataObject);

        if (((List) list.get(1)).size() < minPoints) {
            list.add(new Double(DataObject.UNDEFINED));
            return list;
        } else {
            List nextNeighbours_List = (List) list.get(0);
            PriorityQueueElement priorityQueueElement = (PriorityQueueElement) nextNeighbours_List
                    .get(nextNeighbours_List.size() - 1);
            if (priorityQueueElement.getPriority() <= epsilon) {
                list.add(new Double(priorityQueueElement.getPriority()));
                return list;
            } else {
                list.add(new Double(DataObject.UNDEFINED));
                return list;
            }
        }
    }

    /**
     * Expands the ClusterOrder for this dataObject
     * 
     * @param dataObject
     *            Start-DataObject
     * @param seeds
     *            SeedList that stores dataObjects with reachability-distances
     */
    private void expandClusterOrder(DataObject dataObject, UpdateQueue seeds) {
        List list = coreDistance(minPoints, epsilon, dataObject);
        List epsilonRange_List = (List) list.get(1);
        dataObject.r_dist = DataObject.UNDEFINED;
        dataObject.c_dist = ((Double) list.get(2)).doubleValue();
        dataObject.processed = true;

        resultVector.addElement(dataObject);
        dataObject.clusterIndex=clusterID;
        if (dataObject.c_dist != DataObject.UNDEFINED) {
            update(seeds, epsilonRange_List, dataObject);
            while (seeds.hasNext()) {
                UpdateQueueElement updateQueueElement = seeds.next();
                DataObject currentDataObject = (DataObject) updateQueueElement.getObject();
                currentDataObject.r_dist = updateQueueElement.getPriority();
                List list_1 = coreDistance(minPoints, epsilon, currentDataObject);
                List epsilonRange_List_1 = (List) list_1.get(1);
                currentDataObject.c_dist = ((Double) list_1.get(2)).doubleValue();
                currentDataObject.processed = true;
                currentDataObject.clusterIndex=clusterID;
                resultVector.addElement(currentDataObject);

                if (currentDataObject.c_dist != DataObject.UNDEFINED) {
                    update(seeds, epsilonRange_List_1, currentDataObject);
                }
            }
        }
    }

    /**
     * Updates reachability-distances in the Seeds-List
     * 
     * @param seeds
     *            UpdateQueue that holds DataObjects with their corresponding
     *            reachability-distances
     * @param epsilonRange_list
     *            List of DataObjects that were found in epsilon-range of
     *            centralObject
     * @param centralObject
     */
    private void update(UpdateQueue seeds, List epsilonRange_list, DataObject centralObject) {
        double coreDistance = centralObject.c_dist;
        double new_r_dist = DataObject.UNDEFINED;

        for (int i = 0; i < epsilonRange_list.size(); i++) {
            EpsilonRange_ListElement listElement = (EpsilonRange_ListElement) epsilonRange_list.get(i);
            DataObject neighbourhood_object = listElement.getDataObject();
            if (!neighbourhood_object.processed) {
                new_r_dist = Math.max(coreDistance, listElement.getDistance());
                seeds.add(new_r_dist, neighbourhood_object, neighbourhood_object.getKey());
            }
        }
    }

    

    private int clusterID = 0;

    private Vector<DataObject> resultVector;

    public Dataset[] executeClustering(Dataset data) {
        this.dm = new NormalizedEuclideanDistance(data);
        resultVector = new Vector<DataObject>();

        dataset = new Vector<DataObject>();
        for (int i = 0; i < data.size(); i++) {
            dataset.add(new DataObject(data.getInstance(i)));

        }
        UpdateQueue seeds = new UpdateQueue();

        /** OPTICS-Begin */
        for (int i = 0; i < dataset.size(); i++) {
            DataObject tmp = dataset.get(i);
            if (!tmp.processed) {
                expandClusterOrder(tmp, seeds);
                clusterID++;
            }
            
        }
        Dataset[] clusters = new Dataset[clusterID + 1];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new SimpleDataset();
        }
        int noiseCount = 0;
        int notKnownCount = 0;
        for (DataObject dataObject : dataset) {
            if (dataObject.clusterIndex >= 0)
                clusters[dataObject.clusterIndex].addInstance(dataObject.instance);
            if (DataObject.NOISE == dataObject.clusterIndex) {
                clusters[clusterID].addInstance(dataObject.instance);
                noiseCount++;
            }
            if (DataObject.UNCLASSIFIED == dataObject.clusterIndex)
                notKnownCount++;
        }
        return clusters;

    }

    class UpdateQueue {

        /**
         * Used to store the binary heap
         */
        private ArrayList<UpdateQueueElement> queue;

        /**
         * Used to get efficient access to the stored Objects
         */
        private TreeMap<String,Integer> objectPositionsInHeap;

        // *****************************************************************************************************************
        // constructors
        // *****************************************************************************************************************

        /**
         * Creates a new PriorityQueue (backed on a binary heap) with the
         * ability to efficiently update the priority of the stored objects in
         * the heap. The ascending (!) queue is dynamically growing and
         * shrinking.
         */
        public UpdateQueue() {
            queue = new ArrayList<UpdateQueueElement>();
            objectPositionsInHeap = new TreeMap<String,Integer>();
        }

        // *****************************************************************************************************************
        // methods
        // *****************************************************************************************************************

        /**
         * Adds a new Object to the queue
         * 
         * @param priority
         *            The priority associated with the object (in this case: the
         *            reachability-distance)
         * @param objectKey
         *            The key for this object
         * @param o
         */
        public void add(double priority, Object o, String objectKey) {
            int objectPosition = 0;

            if (objectPositionsInHeap.containsKey(objectKey)) {
                objectPosition = ((Integer) objectPositionsInHeap.get(objectKey)).intValue();
                if (((UpdateQueueElement) queue.get(objectPosition)).getPriority() <= priority)
                    return;
                queue.set(objectPosition++, new UpdateQueueElement(priority, o, objectKey));
            } else {
                queue.add(new UpdateQueueElement(priority, o, objectKey));
                objectPosition = size();
            }
            heapValueUpwards(objectPosition);
        }

        /**
         * Returns the priority for the object at the specified index
         * 
         * @param index
         *            the index of the object
         * @return priority
         */
        public double getPriority(int index) {
            return ((UpdateQueueElement) queue.get(index)).getPriority();
        }

        /**
         * Restores the heap after inserting a new object
         */
        private void heapValueUpwards(int pos) {
            int a = pos;
            int c = a / 2;

            UpdateQueueElement recentlyInsertedElement = (UpdateQueueElement) queue.get(a - 1);

            /** ascending order! */
            while (c > 0 && getPriority(c - 1) > recentlyInsertedElement.getPriority()) {
                queue.set(a - 1, queue.get(c - 1)); // shift parent-node down
                objectPositionsInHeap.put(((UpdateQueueElement) queue.get(a - 1)).getObjectKey(), new Integer(a - 1));
                a = c; // (c <= 0) => no parent-node remains
                c = a / 2;
            }
            queue.set(a - 1, recentlyInsertedElement);
            objectPositionsInHeap.put(((UpdateQueueElement) queue.get(a - 1)).getObjectKey(), new Integer(a - 1));
        }

        /**
         * Restores the heap after removing the next element
         */
        private void heapValueDownwards() {
            int a = 1;
            int c = 2 * a; // descendant

            UpdateQueueElement updateQueueElement = (UpdateQueueElement) queue.get(a - 1);

            if (c < size() && (getPriority(c) < getPriority(c - 1)))
                c++;

            while (c <= size() && getPriority(c - 1) < updateQueueElement.getPriority()) {
                queue.set(a - 1, queue.get(c - 1));
                objectPositionsInHeap.put(((UpdateQueueElement) queue.get(a - 1)).getObjectKey(), new Integer(a - 1));
                a = c;
                c = 2 * a;
                if (c < size() && (getPriority(c) < getPriority(c - 1)))
                    c++;
            }
            queue.set(a - 1, updateQueueElement);
            objectPositionsInHeap.put(((UpdateQueueElement) queue.get(a - 1)).getObjectKey(), new Integer(a - 1));
        }

        /**
         * Returns the queue's size
         * 
         * @return size
         */
        public int size() {
            return queue.size();
        }

        /**
         * Tests, if the queue has some more elements left
         * 
         * @return true, if there are any elements left, else false
         */
        public boolean hasNext() {
            return !(queue.size() == 0);
        }

        /**
         * Returns the element with the lowest priority
         * 
         * @return next element
         */
        public UpdateQueueElement next() {
            UpdateQueueElement next = (UpdateQueueElement) queue.get(0);
            queue.set(0, queue.get(size() - 1));
            queue.remove(size() - 1);
            objectPositionsInHeap.remove(next.getObjectKey());
            if (hasNext()) {
                heapValueDownwards();
            }
            return next;
        }

        // *****************************************************************************************************************
        // inner classes
        // *****************************************************************************************************************

    }

    class UpdateQueueElement {

        /**
         * Holds the priority for the object (in this case: the
         * reachability-distance)
         */
        private double priority;

        /**
         * Holds the original object
         */
        private Object o;

        /**
         * Holds the key for this object
         */
        private String objectKey;

        // *****************************************************************************************************************
        // constructors
        // *****************************************************************************************************************
        public UpdateQueueElement(double priority, Object o, String objectKey) {
            this.priority = priority;
            this.o = o;
            this.objectKey = objectKey;
        }

        // *****************************************************************************************************************
        // methods
        // *****************************************************************************************************************

        /**
         * Returns the priority for this object
         * 
         * @return priority
         */
        public double getPriority() {
            return priority;
        }

        /**
         * Returns the object
         * 
         * @return
         */
        public Object getObject() {
            return o;
        }

        /**
         * Returns the key
         * 
         * @return objectKey
         */
        public String getObjectKey() {
            return objectKey;
        }

        // *****************************************************************************************************************
        // inner classes
        // *****************************************************************************************************************
    }

    class EpsilonRange_ListElement {

        /**
         * Holds the dataObject
         */
        private DataObject dataObject;

        /**
         * Holds the distance that was calculated for this dataObject
         */
        private double distance;

        // *****************************************************************************************************************
        // constructors
        // *****************************************************************************************************************

        /**
         * Constructs a new Element that is stored in the ArrayList which is
         * built in the k_nextNeighbourQuery-method from a specified database.
         * This structure is chosen to deliver not only the DataObjects that are
         * within the epsilon-range but also deliver the distances that were
         * calculated. This reduces the amount of distance-calculations within
         * some data-mining-algorithms.
         * 
         * @param distance
         *            The calculated distance for this dataObject
         * @param dataObject
         *            A dataObject that is within the epsilon-range
         */
        public EpsilonRange_ListElement(double distance, DataObject dataObject) {
            this.distance = distance;
            this.dataObject = dataObject;
        }

        // *****************************************************************************************************************
        // methods
        // *****************************************************************************************************************

        /**
         * Returns the distance that was calulcated for this dataObject (The
         * distance between this dataObject and the dataObject for which an
         * epsilon-range-query was performed.)
         * 
         * @return distance
         */
        public double getDistance() {
            return distance;
        }

        /**
         * Returns this dataObject
         * 
         * @return dataObject
         */
        public DataObject getDataObject() {
            return dataObject;
        }

        // *****************************************************************************************************************
        // inner classes
        // *****************************************************************************************************************

    }

    class PriorityQueue {

        /**
         * Used to store the binary heap
         */
        private ArrayList<PriorityQueueElement> queue;

        // *****************************************************************************************************************
        // constructors
        // *****************************************************************************************************************

        /**
         * Creates a new PriorityQueue backed on a binary heap. The queue is
         * dynamically growing and shrinking and it is descending, that is: the
         * highest priority is always in the root.
         */
        public PriorityQueue() {
            queue = new ArrayList<PriorityQueueElement>();
        }

        // *****************************************************************************************************************
        // methods
        // *****************************************************************************************************************

        /**
         * Adds a new Object to the queue
         * 
         * @param priority
         *            The priority associated with the object
         * @param o
         */
        public void add(double priority, Object o) {
            queue.add(new PriorityQueueElement(priority, o));
            heapValueUpwards();
        }

        /**
         * Returns the priority for the object at the specified index
         * 
         * @param index
         *            the index of the object
         * @return priority
         */
        public double getPriority(int index) {
            return ((PriorityQueueElement) queue.get(index)).getPriority();
        }

        /**
         * Restores the heap after inserting a new object
         */
        private void heapValueUpwards() {
            int a = size();
            int c = a / 2;

            PriorityQueueElement recentlyInsertedElement = queue.get(a - 1);

            while (c > 0 && getPriority(c - 1) < recentlyInsertedElement.getPriority()) {
                queue.set(a - 1, queue.get(c - 1)); // shift parent-node down
                a = c; // (c <= 0) => no parent-node remains
                c = a / 2;
            }
            queue.set(a - 1, recentlyInsertedElement);
        }

        /**
         * Restores the heap after removing the next element
         */
        private void heapValueDownwards() {
            int a = 1;
            int c = 2 * a; // descendant

            PriorityQueueElement priorityQueueElement = queue.get(a - 1);

            if (c < size() && (getPriority(c) > getPriority(c - 1)))
                c++;

            while (c <= size() && getPriority(c - 1) > priorityQueueElement.getPriority()) {
                queue.set(a - 1, queue.get(c - 1));
                a = c;
                c = 2 * a;
                if (c < size() && (getPriority(c) > getPriority(c - 1)))
                    c++;
            }
            queue.set(a - 1, priorityQueueElement);
        }

        /**
         * Returns the queue's size
         * 
         * @return size
         */
        public int size() {
            return queue.size();
        }

        /**
         * Tests, if the queue has some more elements left
         * 
         * @return true, if there are any elements left, else false
         */
        public boolean hasNext() {
            return !(size() == 0);
        }

        /**
         * Returns the element with the highest priority
         * 
         * @return next element
         */
        public PriorityQueueElement next() {
            PriorityQueueElement next = (PriorityQueueElement) queue.get(0);
            queue.set(0, queue.get(size() - 1));
            queue.remove(size() - 1);
            if (hasNext()) {
                heapValueDownwards();
            }
            return next;
        }

        // *****************************************************************************************************************
        // inner classes
        // *****************************************************************************************************************

    }

    class PriorityQueueElement {

        /**
         * Holds the priority for the object (in this case: the distance)
         */
        private double priority;

        /**
         * Holds the original object
         */
        private Object o;

        // *****************************************************************************************************************
        // constructors
        // *****************************************************************************************************************
        public PriorityQueueElement(double priority, Object o) {
            this.priority = priority;
            this.o = o;
        }

        // *****************************************************************************************************************
        // methods
        // *****************************************************************************************************************

        /**
         * Returns the priority for this object
         * 
         * @return priority
         */
        public double getPriority() {
            return priority;
        }

        /**
         * Returns the object
         * 
         * @return
         */
        public Object getObject() {
            return o;
        }

        // *****************************************************************************************************************
        // inner classes
        // *****************************************************************************************************************

    }
}
