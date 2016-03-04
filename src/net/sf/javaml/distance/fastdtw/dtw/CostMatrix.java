/**
 * %SVN.HEADER%
 */
package net.sf.javaml.distance.fastdtw.dtw;

/**
 * 
 * @author Thomas Abeel
 * @author Stan Salvador, stansalvador@hotmail.com
 * 
 */
interface CostMatrix {

    public abstract void put(int i, int j, double d);

    public abstract double get(int i, int j);

    public abstract int size();
}