/**
 * %SVN.HEADER%
 */
package net.sf.javaml.filter.instance;

import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.InstanceFilter;

public class ReplaceValueFilter implements InstanceFilter{

    Double to,from;
    public ReplaceValueFilter(Double from, Double to) {
        this.from=from;
        this.to=to;
    }

    @Override
    public void filter(Instance inst) {
       for(Integer i:inst.keySet()){
           if(inst.get(i).equals(from))
               inst.put(i, to);
       }
        
    }

}
