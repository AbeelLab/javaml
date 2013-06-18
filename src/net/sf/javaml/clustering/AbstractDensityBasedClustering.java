/**
 * %SVN.HEADER% 
 */
package net.sf.javaml.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
/**
 * XXX add doc
 * 
 * @author Thomas Abeel
 *
 */
abstract class AbstractDensityBasedClustering {
    class DataObject {
        int clusterIndex = -1;

        /**
         * Holds the coreDistance for this DataObject
         */
        double c_dist;

        /**
         * Holds the reachabilityDistance for this DataObject
         */
        double r_dist;
        
        boolean processed=false;
        /**
         * XXX doc
         */
        static final int UNCLASSIFIED = -1;
        /**
         * XXX doc
         */
        static final int UNDEFINED = Integer.MAX_VALUE;
        /**
         * XXX doc
         */
        static final int NOISE = -2;
        /**
         * XXX doc
         */
        Instance instance;

        /**
         * XXX doc
         */
        public DataObject(Instance inst) {
            this.instance = inst;
        }
        /**
         * XXX doc
         */
        @Override
        public boolean equals(Object obj) {
            DataObject tmp = (DataObject) obj;
            return tmp.instance.equals(this.instance);
        }
        /**
         * XXX doc
         */
        @Override
        public int hashCode() {
            return this.instance.hashCode();
        }
        /**
         * XXX doc
         */
        public String getKey() {
            return instance.toString();
        }
    }/**
     * XXX doc
     */
    DistanceMeasure dm;
    /**
     * XXX doc
     */
    Vector<DataObject> dataset = null;
    /**
     * XXX doc
     */
    List<DataObject> epsilonRangeQuery(double epsilon, DataObject inst) {

        ArrayList<DataObject> epsilonRange_List = new ArrayList<DataObject>();

        for (int i = 0; i < dataset.size(); i++) {
            DataObject tmp = dataset.get(i);
            double distance = dm.measure(tmp.instance, inst.instance);
            if (distance < epsilon) {
                epsilonRange_List.add(tmp);
            }
        }

        return epsilonRange_List;

    }
}
