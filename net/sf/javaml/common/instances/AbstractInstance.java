/**
 * AbstractInstance.java, 11-okt-06
 *
 * This file is part of the Java Machine Learning API
 * 
 * php-agenda is free software; you can redistribute it and/or modify
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
 * Copyright (c) 2006, Thomas Abeel
 * 
 * Project: http://sourceforge.net/projects/java-ml/
 * 
 */

package net.sf.javaml.common.instances;

import net.sf.javaml.common.Instance;
import net.sf.javaml.common.Value;

public abstract class AbstractInstance implements Instance{
	protected Value classValue=null;
	protected double weight=1;
	
	public AbstractInstance(){
		this(null);
	}
	public AbstractInstance(Value classValue){
		this.classValue=classValue;
	}
    /* (non-Javadoc)
     * @see net.sf.javaml.common.Instance#getClassValue()
     */
    public Value getClassValue() {
        return classValue;
    }
    /* (non-Javadoc)
     * @see net.sf.javaml.common.Instance#getWeight()
     */
    public double getWeight() {
        return weight;
    }
    /* (non-Javadoc)
     * @see net.sf.javaml.common.Instance#isClassValueMissing()
     */
    public boolean isClassMissing() {
        return classValue==null;
    }
    /* (non-Javadoc)
     * @see net.sf.javaml.common.Instance#setClassValue(net.sf.javaml.common.Attribute)
     */
    public void setClassValue(Value a) {
        this.classValue=a;
        
    }
    /* (non-Javadoc)
     * @see net.sf.javaml.common.Instance#setClassValueMissing()
     */
    public void setClassMissing() {
        setClassValue(null);
    }
    /* (non-Javadoc)
     * @see net.sf.javaml.common.Instance#setWeight(double)
     */
    public void setWeight(double d) {
        this.weight=d;
        
    }

}
