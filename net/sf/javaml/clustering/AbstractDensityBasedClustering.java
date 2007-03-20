/**
 * AbstractDensityBasedClustering.java
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
 * Copyright (c) 2006-2007, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */
package net.sf.javaml.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

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
        static final int UNCLASSIFIED = -1;
        static final int UNDEFINED = Integer.MAX_VALUE;
        static final int NOISE = -2;

        Instance instance;

        
        public DataObject(Instance inst) {
            this.instance = inst;
        }

        @Override
        public boolean equals(Object obj) {
            DataObject tmp = (DataObject) obj;
            return tmp.instance.equals(this.instance);
        }

        @Override
        public int hashCode() {
            return this.instance.hashCode();
        }

        public String getKey() {
            return instance.toString();
        }
    }
    DistanceMeasure dm;
    Vector<DataObject> dataset = null;

    List<DataObject> epsilonRangeQuery(double epsilon, DataObject inst) {

        ArrayList<DataObject> epsilonRange_List = new ArrayList<DataObject>();

        for (int i = 0; i < dataset.size(); i++) {
            DataObject tmp = dataset.get(i);
            double distance = dm.calculateDistance(tmp.instance, inst.instance);
            if (distance < epsilon) {
                epsilonRange_List.add(tmp);
            }
        }

        return epsilonRange_List;

    }
}
