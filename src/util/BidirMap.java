/*
 * Copyright (c) 2018 Dimitri Watel
 */

package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bidirectional Map
 *
 * Contain the classical methods of a java Map: {@link #put(Object, Object)}, {@link #getV(Object)} {@link #size()}
 * {@link #keySet()} {@link #values()}, {@link #entrySet()} and {@link #removeK(Object)}. getV and removeK
 * corresponds to the classical get and remove methods. It also contains two other methods {@link #getK(Object)} and
 * {@link #removeV(Object)} to access directly the key and the values.
 *
 * This map is bidirectional: every key is uniquely associated to a value and conversely. Thus, for instance adding a
 * pair (k, v) and then a pair(k2, v) remove the first pair as v should be associated to a unique key. This is done
 * using two java Map.
 *
 * @see java.util.Map
 */
public class BidirMap<K, V> {

    Map<K, V> direct;
    Map<V, K> reverse;

    public BidirMap() {
        direct = new HashMap<>();
        reverse = new HashMap<>();
    }

    public void put(K key, V value){

        V pvalue = direct.get(key);
        K pkey = reverse.get(value);

        direct.put(key, value);
        reverse.put(value, key);


        if(pvalue != null)
            reverse.remove(pvalue);
        if(pkey != null)
            direct.remove(pkey);
    }

    public int size(){
        return direct.size();
    }

    public void clear(){
        direct.clear();
        reverse.clear();
    }

    public Set<K> keySet(){
        return direct.keySet();
    }

    public Collection<V> values(){
        return direct.values();
    }

    public Set<Map.Entry<K, V>> entrySet(){
        return direct.entrySet();
    }

    public V getV(K key){
        return direct.get(key);
    }

    public K getK(V value){
        return reverse.get(value);
    }

    public V removeK(K key){
        V value = direct.remove(key);
        reverse.remove(value);
        return value;
    }

    public K removeV(V value){
        K key = reverse.remove(value);
        direct.remove(key);
        return key;
    }

    public boolean containsK(K key) {
        return direct.containsKey(key);
    }

    public boolean containsV(V value) {
        return reverse.containsKey(value);
    }
}
