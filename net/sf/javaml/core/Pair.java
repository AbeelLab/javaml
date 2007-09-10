/**
 * Pair.java
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
/**
 * 
 */
package net.sf.javaml.core;

/**
 * Represents a pair or couple of objects.
 * 
 * @author Thomas Abeel
 * 
 * @param <S>
 * @param <T>
 */
public class Pair<S, T> {

    private S x;

    private T y;

    private int hashCode;

    public Pair(S x, T y) {
        this.x = x;
        this.y = y;

        this.hashCode = (1 << x.hashCode()) + y.hashCode();
    }

    public S x() {
        return x;

    }

    public T y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        Pair p = (Pair) o;
        return p.x.equals(this.x) && p.y.equals(this.y);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "[" + x.toString() + ";" + y.toString() + "]";
    }
}