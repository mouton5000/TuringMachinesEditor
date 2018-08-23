/*
 * Copyright (c) 2018 Dimitri Watel
 */

package util;

/**
 * Simple pair of objects of class T and U
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {
    public T first;
    public U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return first.hashCode()^second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || !(obj instanceof Pair))
            return false;
        Pair<T, U> pair = (Pair<T, U>) obj;

        return (this.first == pair || this.first != null && this.first.equals(pair.first))
                || (this.second == pair || this.second != null && this.second.equals(pair.second));
    }

    @Override
    public String toString() {
        return "(" + first.toString()+" "+second.toString()+")";
    }
}
