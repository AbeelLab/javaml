/**
 * AbstractFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;


/**
 * Umbrella class for filters that implements both the
 * {@link net.sf.javaml.filter.InstanceFilter} and
 * {@link net.sf.javaml.filter.DatasetFilter} interfaces. No implementation is
 * done in this class.
 * 
 * {@jmlSource}
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 */
public abstract class AbstractFilter implements DatasetFilter, InstanceFilter {

//    public Dataset unfilterDataset(Dataset d){
//        throw new UnsupportedOperationException("One-way filter only.");
//    }
//    public Instance unfilterInstance(Instance i){
//        throw new UnsupportedOperationException("One-way filter only.");
//    }
}
