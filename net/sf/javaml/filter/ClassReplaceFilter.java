/**
 * ClassReplaceFilter.java
 *
 * %SVN.HEADER%
 */
package net.sf.javaml.filter;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleInstance;

/**
 * Replaces a certain class value with another one.
 * 
 * {@jmlSource}
 * 
 * 
 * @version %SVN.REVISION%
 * 
 * @author Thomas Abeel
 * 
 */
public class ClassReplaceFilter extends AbstractFilter {

    private int oldClassValue, newClassValue;

    public ClassReplaceFilter(int oldValue, int newClassValue) {
        this.newClassValue = newClassValue;
        this.oldClassValue = oldValue;
    }

    public void build(Dataset data) {
        // do nothing, requires no training

    }

    @Override
    public Instance filterInstance(Instance inst) {
        if (inst.classValue() != oldClassValue)
            return inst;
        else
            return new SimpleInstance(inst.toArray(), newClassValue, inst.weight());
    }

}
